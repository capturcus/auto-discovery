package kaczkipingujapobroadcascie.com.autodiscovery;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class WifiTest extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wifi_test);
    }

    public void onConnectClick(View v) {
        WifiUtils.connect(getApplicationContext(), ((EditText) findViewById(R.id.ssid)).getText().toString(), ((EditText) findViewById(R.id.password)).getText().toString(), WifiUtils.EncryptionType.ENC_WPA, new WifiUtils.ConnectCallback() {
            @Override
            public void onConnect(boolean success) {
                Log.d("debug", "onConnect");
                if(success)
                    ((TextView) findViewById(R.id.textView)).setText("success");
                else
                    ((TextView) findViewById(R.id.textView)).setText("fuck");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_wifi_test, menu);
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
}
