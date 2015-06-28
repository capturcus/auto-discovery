package kaczkipingujapobroadcascie.com.autodiscovery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by marcin on 27.06.15.
 */

public class RowAdapter extends ArrayAdapter<ZeroConfInterface> {

    public RowAdapter(Context context) {
        super(context, R.layout.row);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row, null);
        }
        ZeroConfInterface o = getItem(position);
        if (o != null) {
            ImageView iv = (ImageView) v.findViewById(R.id.image_view);
            switch (o.name){
                case "print":
                    iv.setImageResource(R.drawable.print);
                    break;
                case "slides":
                    iv.setImageResource(R.drawable.slides);
                    break;
                case "wifi":
                    iv.setImageResource(R.drawable.wifi);
                    break;
                case "volume":
                    iv.setImageResource(R.drawable.volume);
                    break;
                default:
                    iv.setImageResource(R.drawable.trollface);
                    break;
            }
            TextView tt = (TextView) v.findViewById(R.id.beacon_name);
            if (tt != null) {
                tt.setText(o.name);
            }
        }
        return v;
    }
}
