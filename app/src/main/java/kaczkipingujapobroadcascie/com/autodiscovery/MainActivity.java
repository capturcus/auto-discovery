package kaczkipingujapobroadcascie.com.autodiscovery;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BeaconCommunication.OnInterfacesFoundListener {

    ArrayAdapter<String> aa = null;
    public static MainActivity instance = null;

    BeaconCommunication bc = null;

    public MainActivity() {
        super();
        instance = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bc = new BeaconCommunication();
        bc.onCreate(getApplicationContext());

        aa = new ArrayAdapter<>(getApplicationContext(),
                R.layout.row);

        setContentView(R.layout.activity_main);
        ListView lv = (ListView)findViewById(R.id.list_view);
        lv.setAdapter(aa);

        BluetoothAdapter.getDefaultAdapter().enable();

        bc.startSearch(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bc.onDestroy();
    }

    @Override
    public void interfacesFound(final List<ZeroConfInterface> zinterfaces) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                aa.clear();
                for (ZeroConfInterface zci : zinterfaces) {
                    if (zci != null && zci.name != null) {
                        aa.add(zci.name);
                    }
                }
                aa.notifyDataSetChanged();
                ProgressBar pb = (ProgressBar) findViewById(R.id.spinner);
                if (aa.isEmpty()) {
                    pb.setVisibility(View.VISIBLE);
                } else {
                    pb.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        bc.stopSearch();
    }

    private void runInterface(ZeroConfInterface zinterface) {
        WifiUtils.connect(getApplicationContext(), zinterface.ssid, zinterface.password,
                WifiUtils.EncryptionType.valueOf(zinterface.authType),
                new WifiUtils.ConnectCallback() {
                    @Override
                    public void onConnect(boolean success) {
                        Log.d("debug", "onConnect");
                        if(success) {
                            Intent intent = new Intent(MainActivity.this, BrowserActivity.class);
                            Bundle data = new Bundle();
                            data.putString("url", "http://google.com");
                            intent.putExtras(data);
                            startActivity(intent);
                        } else {
                            // error handling
                        }
                    }
                });
    }
}
