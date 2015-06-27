package kaczkipingujapobroadcascie.com.autodiscovery;

import com.kontakt.sdk.android.device.BeaconDevice;
import com.loopj.android.http.*;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class BeaconMetaData
{
    public interface OnMetaDataGotListener
    {
        void metaDataGot(BeaconMetaData metadata);
    }

    public BeaconMetaData(BeaconDevice device)
    {
        assert device != null;
        m_device = device;
    }

    public void requestMetaData(String api_key, OnMetaDataGotListener listener)
    {
        m_callbackListener = listener;
        AsyncHttpClient http_client = new AsyncHttpClient();
        String url = DEVICE_URL;
        url = url.concat(m_device.getUniqueId());

        RequestParams params = new RequestParams();
        params.add("Accept", "application/vnd.com.kontakt+json; version=6");
        params.add("Api-Key", api_key);

        http_client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json_str = new String(responseBody);
                try {
                    JSONObject json = new JSONObject(json_str);
                    m_alias = json.getString("alias");
                    m_metadata = json.getJSONObject("metadata");
                    if (m_metadata != null)
                        m_callbackListener.metaDataGot(BeaconMetaData.this);
                } catch (JSONException e) {
                    // TODO: DO shit here
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // TODO: Do something on fail
                error.printStackTrace();
            }
        });

    }

    public BeaconDevice getDevice()
    {
        return m_device;
    }

    public String getString(String key)
    {
        try
        {
            return m_metadata.getString(key);
        } catch (JSONException e)
        {
            // TODO: Handle shit here
            e.printStackTrace();
            return null;
        }
    }

    public String getAlias()
    {
        return m_alias;
    }

    private final String DEVICE_URL = "https://api.kontakt.io/device/";

    private OnMetaDataGotListener m_callbackListener;
    private BeaconDevice m_device;
    private JSONObject m_metadata;
    private String m_alias;
}
