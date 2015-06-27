package kaczkipingujapobroadcascie.com.autodiscovery;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;

import com.kontakt.sdk.android.configuration.ForceScanConfiguration;
import com.kontakt.sdk.android.configuration.MonitorPeriod;
import com.kontakt.sdk.android.connection.OnServiceBoundListener;
import com.kontakt.sdk.android.device.BeaconDevice;
import com.kontakt.sdk.android.device.Region;
import com.kontakt.sdk.android.http.KontaktApiClient;
import com.kontakt.sdk.android.manager.BeaconManager;
import com.kontakt.sdk.android.model.Beacon;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class BeaconCommunication {
    public interface OnInterfacesFoundListener {
        void interfacesFound(List<ZeroConfInterface> zinterfaces);
    }

    public void onCreate(final Context context)
    {
        // Awesome...
        try
        {
            Bundle metadata = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData;
            m_apiKey = metadata.getString("kontakt.io.API_KEY");
        } catch(PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
            // TODO: Handle that shit
            m_apiKey = "";
        }


        m_beaconManager = BeaconManager.newInstance(context);
        m_beaconManager.setMonitorPeriod(MonitorPeriod.MINIMAL);
        m_beaconManager.setForceScanConfiguration(ForceScanConfiguration.DEFAULT);
        m_beaconManager.registerRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<BeaconDevice> beacons) {
                Dictionary<BeaconDevice, BeaconData> new_devices = new Hashtable<>();
                for (BeaconDevice beacon : beacons)
                {
                    BeaconData old_data = m_devices.get(beacon);
                    if (old_data == null)
                        old_data = new BeaconData();
                    new_devices.put(beacon, old_data);
                    if (old_data.metaData == null)
                    {
                        // TODO: Request meta data and do shit with it
                        old_data.metaData = new BeaconMetaData(beacon);
                        old_data.metaData.requestMetaData(m_apiKey, new BeaconMetaData.OnMetaDataGotListener() {
                            @Override
                            public void metaDataGot(BeaconMetaData metadata) {
                                handleMetaData(metadata);
                            }
                        });
                    }
                }
            }
        });
    }

    public void onDestroy()
    {
        if (isSearching())
            stopSearch();
        m_beaconManager.disconnect();
        m_beaconManager = null;
    }

    public void startSearch(OnInterfacesFoundListener listener)
    {
        assert !m_active;
        assert m_updateCallback == null;

        m_active = true;
        m_updateCallback = listener;
        assert m_beaconManager.isBluetoothEnabled();

        if (m_beaconManager.isConnected())
        {
            try {
                m_beaconManager.startRanging();
            } catch (RemoteException e)
            {
                e.printStackTrace();
            }
        } else
            connectBeacons();
    }
    public void stopSearch()
    {
        assert m_active;
        assert m_updateCallback != null;
        m_active = false;
        m_updateCallback = null;
        m_beaconManager.stopRanging();
        m_devices = new Hashtable<>();
    }
    public boolean isSearching()
    {
        return m_active;
    }

    /************
     * PRIVATES *
     ************/

    private class BeaconData
    {
        public ZeroConfInterface zeroconf;
        public BeaconMetaData metaData;
    }

    private void connectBeacons()
    {
        try {
            m_beaconManager.connect(new OnServiceBoundListener() {
                @Override
                public void onServiceBound() throws RemoteException {
                    m_beaconManager.startRanging();
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void handleMetaData(BeaconMetaData metadata)
    {
        BeaconDevice beacon = metadata.getDevice();
        String service = metadata.getString("service");
        assert service.equals("wifi");
        ZeroConfInterface zinf = new ZeroConfInterface();

        zinf.name = metadata.getAlias();
        zinf.ssid = metadata.getString("ssid");
        zinf.password = metadata.getString("pass");
        zinf.encryptionType = metadata.getString("auth_type");

        BeaconData bdata = m_devices.get(beacon);
        if (bdata != null)
        {
            bdata.zeroconf = zinf;
            sendCallback();
        }
    }

    private void sendCallback()
    {
        List<ZeroConfInterface> param = new ArrayList<>();
        Enumeration<BeaconData> it = m_devices.elements();
        while (it.hasMoreElements())
        {
            BeaconData bdata = it.nextElement();
            if (bdata.zeroconf != null)
                param.add(bdata.zeroconf);
        }
        m_updateCallback.interfacesFound(param);
    }

    private OnInterfacesFoundListener m_updateCallback = null;
    private BeaconManager m_beaconManager;
    private boolean m_active = false;
    private Dictionary<BeaconDevice, BeaconData> m_devices = new Hashtable<>();
    private String m_apiKey;
}
