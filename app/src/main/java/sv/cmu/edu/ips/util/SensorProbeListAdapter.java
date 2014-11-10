package sv.cmu.edu.ips.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;

import sv.cmu.edu.ips.R;
import sv.cmu.edu.ips.data.SensorDataCollector;

/**
 * Created by sumeet on 10/31/14.
 */
public class SensorProbeListAdapter extends ArrayAdapter<SensorDataCollector> {
    private ArrayList<SensorDataCollector> probes;

    public SensorProbeListAdapter(Context context, int textViewResourceId, ArrayList<SensorDataCollector> sensorDataCollectors) {
        super(context, textViewResourceId, sensorDataCollectors);
        this.probes = sensorDataCollectors;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item, null);
        }

        SensorDataCollector sensorDataCollector = probes.get(position);
        if (sensorDataCollector != null) {
            CheckBox chkBox = (CheckBox) v.findViewById(R.id.chkListItem);

            if (chkBox != null) {
                chkBox.setText(sensorDataCollector.getName());
            }
        }

        return v;
    }
}
