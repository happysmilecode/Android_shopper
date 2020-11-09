package c.offerak.speedshopper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import c.offerak.speedshopper.R;

public class ResideMenuAdapter extends BaseAdapter {

    public static final String TAG = ResideMenuAdapter.class.getSimpleName();
    private Context context;
    private List<String> strings = new ArrayList<>();

    public ResideMenuAdapter(Context context, List<String> strings) {
        this.context = context;
        this.strings = strings;
    }

    @Override
    public int getCount() {
        return strings.size();
    }

    @Override
    public Object getItem(int position) {
        return strings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.residemenu_item, parent, false);

            TextView view = convertView.findViewById(R.id.tv_title);
            view.setText(strings.get(position));
        }
        return convertView;
    }
}
