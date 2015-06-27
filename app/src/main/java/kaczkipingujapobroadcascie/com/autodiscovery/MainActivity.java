package kaczkipingujapobroadcascie.com.autodiscovery;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BeaconCommunication.OnInterfacesFoundListener {

    RowAdapter ra = null;

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

        ra = new RowAdapter(getApplicationContext());

        setContentView(R.layout.activity_main);
        ListView lv = (ListView)findViewById(R.id.list_view);
        lv.setAdapter(ra);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ZeroConfInterface zci = ra.getItem(position);
                runInterface(zci);
            }
        });

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
                ra.clear();
                for (ZeroConfInterface zci : zinterfaces) {
                    if (zci != null) {
                        ra.add(zci);
                    }
                }
                ra.notifyDataSetChanged();
                ProgressBar pb = (ProgressBar) findViewById(R.id.spinner);
                if (ra.isEmpty()) {
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

    private void runInterface(final ZeroConfInterface zinterface) {
        WifiUtils.connect(getApplicationContext(), zinterface.ssid, zinterface.password,
                WifiUtils.EncryptionType.valueOf(zinterface.authType),
                new WifiUtils.ConnectCallback() {
                    @Override
                    public void onConnect(boolean success) {
                        Log.d("debug", "onConnect");
                        if(success) {
                            Intent intent = new Intent(MainActivity.this, BrowserActivity.class);
                            Bundle data = new Bundle();
                            data.putString("url", zinterface.url);
                            intent.putExtras(data);
                            startActivity(intent);
                        } else {
                            // error handling
                        }
                    }
                });
    }
}
