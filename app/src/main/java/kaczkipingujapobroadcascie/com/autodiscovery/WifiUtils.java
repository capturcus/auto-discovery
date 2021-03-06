package kaczkipingujapobroadcascie.com.autodiscovery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;

/**
 * Created by koz4k on 2015-06-27.
 */
public class WifiUtils {
    public enum EncryptionType {
        NONE,
        WEP,
        WPA
    }

    public interface ConnectCallback {
        void onConnect(boolean success);
    }

    static class ConnectBroadcastReceiver extends BroadcastReceiver {
        public ConnectBroadcastReceiver(String ssid, ConnectCallback callback) {
            this.ssid_ = ssid;
            this.callback_ = callback;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("debug", "onReceive");
            WifiManager mgr = (WifiManager)
                    context.getSystemService(Context.WIFI_SERVICE);
            if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) &&
                    ssid_.equals(mgr.getConnectionInfo().getSSID())) {
                NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                Log.d("debug", info.getDetailedState().toString());
                if(info.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                    context.unregisterReceiver(this);
                    callback_.onConnect(true);
                } else if(info.getDetailedState() == NetworkInfo.DetailedState.FAILED) {
                    context.unregisterReceiver(this);
                    callback_.onConnect(false);
                }
            }
        }

        private String ssid_;
        private ConnectCallback callback_;
        private int disconnectedCount_ = 0;
    }

    private WifiUtils() {
    }

    public static void connect(Context context, String ssid, String password,
                               EncryptionType enc, ConnectCallback callback) {
        WifiConfiguration newConf = new WifiConfiguration();
        newConf.SSID = "\"" + ssid + "\"";
        Log.d("debug", "dupa1");

        switch(enc) {
            case NONE:
                newConf.allowedKeyManagement.set(
                        WifiConfiguration.KeyMgmt.NONE);
                break;

            case WEP:
                newConf.wepKeys[0] = "\"" + password + "\"";
                newConf.wepTxKeyIndex = 0;
                newConf.allowedKeyManagement.set(
                        WifiConfiguration.KeyMgmt.NONE);
                newConf.allowedGroupCiphers.set(
                        WifiConfiguration.GroupCipher.WEP40);
                break;

            case WPA:
                newConf.preSharedKey = "\"" + password + "\"";
                break;
        }

        WifiManager mgr = (WifiManager)
                context.getSystemService(Context.WIFI_SERVICE);

        if(mgr.isWifiEnabled() && newConf.SSID.equals(mgr.getConnectionInfo().getSSID())) {
            Log.d("debug", "sdalidfiljhdsfjildsfjilsdafdsfdsflhdsfdsfkdfs");
            callback.onConnect(true);
            return;
        }
        mgr.setWifiEnabled(true);
        mgr.addNetwork(newConf);

        List<WifiConfiguration> confs = mgr.getConfiguredNetworks();
        boolean found = false;
        if(confs != null) {
            for (WifiConfiguration conf : confs) {
                if (conf.SSID != null && conf.SSID.equals(newConf.SSID)) {
                    mgr.disconnect();
                    mgr.enableNetwork(conf.networkId, true);
                    mgr.reconnect();
                    found = true;
                    break;
                }
            }
        }
        Log.d("debug", "trying to connect");

        if(!found) {
            callback.onConnect(false);
            return;
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        context.registerReceiver(new ConnectBroadcastReceiver(newConf.SSID, callback), filter);

        Log.d("debug", "connecting in async");
    }
}
