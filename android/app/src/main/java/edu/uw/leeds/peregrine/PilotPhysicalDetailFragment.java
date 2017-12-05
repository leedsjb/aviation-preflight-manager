package edu.uw.leeds.peregrine;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.uw.leeds.peregrine.dummy.DummyContent;

/**
 * A fragment representing a single PilotPhysical detail screen.
 * This fragment is either contained in a {@link PilotPhysicalListActivity}
 * in two-pane mode (on tablets) or a {@link PilotPhysicalDetailActivity}
 * on handsets.
 */
public class PilotPhysicalDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String TAG = "PilotPhysicalDetail";

    /**
     * The dummy content this fragment is presenting.
     */
    private PilotPhysicalContent.PilotPhysicalItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PilotPhysicalDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = PilotPhysicalContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            Log.v(TAG, "The key is " + getArguments().getString(ARG_ITEM_ID));

            Log.v(TAG, "mItem is: " + mItem);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null) {
//                appBarLayout.setTitle(mItem.title);
//            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pilotphysical_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.pilotphysical_detail)).setText(mItem.description);
        }

        return rootView;
    }
}
