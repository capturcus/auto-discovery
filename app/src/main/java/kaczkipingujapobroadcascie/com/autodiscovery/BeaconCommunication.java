package kaczkipingujapobroadcascie.com.autodiscovery;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.RemoteException;

import com.kontakt.sdk.android.configuration.ForceScanConfiguration;
import com.kontakt.sdk.android.configuration.MonitorPeriod;
import com.kontakt.sdk.android.connection.OnServiceBoundListener;
import com.kontakt.sdk.android.device.BeaconDevice;
import com.kontakt.sdk.android.device.Region;
import com.kontakt.sdk.android.manager.BeaconManager;

import java.util.ArrayList;
import java.util.List;

public class BeaconCommunication {
    public interface OnInterfacesFoundListener {
        void interfacesFound(List<ZeroConfInterface> zinterfaces);
    }

    public void onCreate(final Context context)
    {
        m_beaconManager = BeaconManager.newInstance(context);
        m_beaconManager.setMonitorPeriod(MonitorPeriod.MINIMAL);
        m_beaconManager.setForceScanConfiguration(ForceScanConfiguration.DEFAULT);
        m_beaconManager.registerRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<BeaconDevice> beacons) {
                List<ZeroConfInterface> zinterfaces = new ArrayList<>();
                for (BeaconDevice beacon : beacons)
                {
                    ZeroConfInterface zinterface = new ZeroConfInterface();
                    zinterface.name = beacon.getName();
                    zinterfaces.add(zinterface);
                }
                m_updateCallback.interfacesFound(zinterfaces);
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
    }
    public boolean isSearching()
    {
        return m_active;
    }

    /************
     * PRIVATES *
     ************/

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

    private OnInterfacesFoundListener m_updateCallback = null;
    private BeaconManager m_beaconManager;
    private boolean m_active = false;
}
