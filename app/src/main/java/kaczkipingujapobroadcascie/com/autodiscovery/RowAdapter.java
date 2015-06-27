package kaczkipingujapobroadcascie.com.autodiscovery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by marcin on 27.06.15.
 */

public class RowAdapter extends BaseAdapter {

    ArrayList<ZeroConfInterface> views;
    private Context context;

    public RowAdapter(Context context) {
        views = new ArrayList<>();
        this.context = context;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public ZeroConfInterface getItem(int position) {
        return views.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clear() {
        views.clear();
    }

    @Override
    public boolean isEmpty() {
        return views.isEmpty();
    }

    public void add(ZeroConfInterface zci) {
        views.add(zci);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.row, parent, false);
        TextView tv = (TextView)v;
        tv.setText(getItem(position).name);
        return v;
    }
}
