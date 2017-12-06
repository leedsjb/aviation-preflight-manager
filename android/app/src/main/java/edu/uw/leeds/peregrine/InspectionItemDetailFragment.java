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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A fragment representing a single InspectionItem detail screen.
 * This fragment is either contained in a {@link InspectionItemListActivity}
 * in two-pane mode (on tablets) or a {@link InspectionItemDetailActivity}
 * on handsets.
 */
public class InspectionItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private InspectionContent.InspectionItem mItem;
    private static final String TAG = "InspectionItmDtlFrgmnt";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public InspectionItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            String key = getArguments().getString(ARG_ITEM_ID);
            mItem = InspectionContent.ITEM_MAP.get(key);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
//                appBarLayout.setTitle(mItem.title);
                appBarLayout.setTitle(mItem.title);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
//                appBarLayout.setTitle(mItem.title);
            appBarLayout.setTitle(mItem.title);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.inspectionitem_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
            String date = format.format(new Date(mItem.dueNext));

            ((TextView) rootView.findViewById(R.id.inspectionitem_detail)).setText(mItem.description);
            ((TextView) rootView.findViewById(R.id.inspectionitem_requirements)).setText(mItem.requirements);
            ((TextView) rootView.findViewById(R.id.inspectionitem_resources)).setText(mItem.resources);
            ((TextView) rootView.findViewById(R.id.inspectionitem_duenext)).setText(date);
        }

        return rootView;
    }
}
