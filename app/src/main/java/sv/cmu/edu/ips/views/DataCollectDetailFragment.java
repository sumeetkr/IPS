package sv.cmu.edu.ips.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sv.cmu.edu.ips.R;

import sv.cmu.edu.ips.util.DalaCollectorsList;
import sv.cmu.edu.ips.service.dataCollectors.SensorDataCollector;

/**
 * A fragment representing a single DataCollect detail screen.
 * This fragment is either contained in a {@link DataCollectListActivity}
 * in two-pane mode (on tablets) or a {@link DataCollectDetailActivity}
 * on handsets.
 */
public class DataCollectDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private SensorDataCollector mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DataCollectDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DalaCollectorsList.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_datacollect_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.datacollect_detail)).setText(mItem.getName());
        }

        return rootView;
    }
}
