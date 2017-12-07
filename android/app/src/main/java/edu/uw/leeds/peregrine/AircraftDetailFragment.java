package edu.uw.leeds.peregrine;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single Aircraft detail screen.
 * This fragment is either contained in a {@link AircraftListActivity}
 * in two-pane mode (on tablets) or a {@link AircraftDetailActivity}
 * on handsets.
 * @author benjaminleeds
 * @version Modified: Wednesday December 6 2017
 */
public class AircraftDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private static final String TAG = "AcftDtlFragment";

    /**
     * The Aircraft this fragment is presenting.
     */
    private AircraftContent.AircraftItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AircraftDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the aircraft detail specified by the fragment
            // arguments.
            // // TODO Consider using a Loader to load content from a content provider.
            mItem = AircraftContent.ITEM_MAP.get(getArguments().getInt(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.planeName);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.aircraft_detail, container, false);
        String aircraft = getString(R.string.aircraft);
        String serial = getResources().getString(R.string.text_ac_serial_number_label);
        String yr = getResources().getString(R.string.text_ac_year_manufactured_label);
        String tach = getResources().getString(R.string.text_ac_tach_time_label);
        String fuel = getResources().getString(R.string.text_ac_fuel_level_label);
        // Show aircraft details as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.aircraft_detail_planeName)).setText(aircraft + ": " + mItem.planeName);
            ((TextView) rootView.findViewById(R.id.aircraft_detail_serial)).setText(serial + ": " + mItem.serialNumber);
            ((TextView) rootView.findViewById(R.id.aircraft_detail_yearManufactured)).setText(yr + ": " + mItem.yearOfManufacture);
            ((TextView) rootView.findViewById(R.id.aircraft_detail_tachometerTime)).setText(tach + ": " + mItem.tachometerTime);
            ((TextView) rootView.findViewById(R.id.aircraft_detail_fuelLevel)).setText(fuel + ": " + mItem.fuelLevel);
        }

        return rootView;
    }
}