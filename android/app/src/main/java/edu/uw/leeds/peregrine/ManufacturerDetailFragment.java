package edu.uw.leeds.peregrine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import java.util.List;
import java.util.Random;

/**
 * A fragment representing a single Manufacturer detail screen.
 * This fragment shows the types the manufacturer manufactures.
 * This fragment is either contained in a {@link ManufacturerListActivity}
 * in two-pane mode (on tablets) or a {@link ManufacturerDetailActivity}
 * on handsets.
 */
public class ManufacturerDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private static final String TAG = "MfacturerDtlFrgmnt";

    /**
     * The AircraftManufacturer this fragment is presenting.
     */
    private AircraftDatabase.AircraftManufacturer mItem;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    private boolean mTwoPane;
    private static SimpleItemRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ManufacturerDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = AircraftDatabase.MANUFACTURER_MAP.get(getArguments().getInt(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.manufacturerCode);
            }

            if (getActivity().findViewById(R.id.type_detail_container) != null) {
                // The detail container view will be present only in the
                // large-screen layouts (res/values-w900dp).
                // If this view is present, then the
                // activity should be in two-pane mode.
                mTwoPane = true;
            }

            this.mRecyclerView = getActivity().findViewById(R.id.type_list); // set RecyclerView xml layout

            assert this.mRecyclerView != null;
            setupRecyclerView(this.mRecyclerView);
            this.mLayoutManager = new LinearLayoutManager(getActivity()); // set LinearLayoutManager
        }
    }


    /**
     * Lifecycle Callback
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // TODO this view obscures the RecyclerView preventing click events from firing
        // TODO temporarily set its size to 0dp x 0dp to resolve issue
        View rootView = inflater.inflate(R.layout.manufacturer_detail, container, false);

        // setup volley
        volleySetup(mItem.manufacturerCode);
        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        this.adapter = new SimpleItemRecyclerViewAdapter(
                AircraftDatabase.AircraftManufacturer.TYPES, this.mTwoPane);
        recyclerView.setAdapter(this.adapter);
    }

    /**
     * Tells adapter there has been a change
     */
    protected static void notifyChange(){
        adapter.notifyDataSetChanged();
    }

    /**
     * Custom adapter for Aircraft Type data
     */
    public class SimpleItemRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>{

        private final List<AircraftDatabase.AircraftManufacturer.AircraftType> mValues;
        private final boolean mTwoPane;
        private AircraftDatabase.AircraftManufacturer.AircraftType newAc;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newAc = (AircraftDatabase.AircraftManufacturer.AircraftType) view.getTag();

                // launch dialog with aircraft info from api prefilled
                final Context activity = ManufacturerDetailFragment.this.getActivity();

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        activity, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                builder.setTitle(getString(R.string.title_add_aircraft_alert_dialog));

                LinearLayout layout = new LinearLayout(activity);
                layout.setOrientation(LinearLayout.VERTICAL);

                // Aircraft Model Name
                final TextView textModel = new TextView(activity); // create view
                textModel.setText(getString(R.string.text_ac_model_label)); // modify view
                layout.addView(textModel); // add view to layout
                final EditText inputTitle = new EditText(activity);
                inputTitle.setInputType(InputType.TYPE_CLASS_TEXT);
                inputTitle.setText(newAc.modelName);
                layout.addView(inputTitle);

                // Aircraft Serial Number
                final TextView textSerialNumber = new TextView(activity); // create view
                textSerialNumber.setText(getString(R.string.text_ac_serial_number_label)); // modify view
                layout.addView(textSerialNumber); // add view to layout
                final EditText inputSerialNumber = new EditText(activity);
                inputSerialNumber.setInputType(InputType.TYPE_CLASS_TEXT);
                inputSerialNumber.setHint("42.118");
                layout.addView(inputSerialNumber);

                // Aircraft Year of Manufacture
                final TextView textManufactureYear = new TextView(activity); // create view
                textManufactureYear.setText(getString(R.string.text_ac_year_manufactured_label)); // modify view
                layout.addView(textManufactureYear); // add view to layout
                final EditText inputManufactureYear = new EditText(activity);
                inputManufactureYear.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputManufactureYear.setHint("2006");
                layout.addView(inputManufactureYear);

                // Aircraft Tachometer Time
                final TextView textTachTime = new TextView(activity); // create view
                textTachTime.setText(getString(R.string.text_ac_tach_time_label)); // modify view
                layout.addView(textTachTime); // add view to layout
                final EditText inputTachTime = new EditText(activity);
                inputTachTime.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputTachTime.setHint("340.7");
                layout.addView(inputTachTime);

                // Aircraft Fuel Level
                final TextView textFuelLevel = new TextView(activity); // create view
                textFuelLevel.setText(getString(R.string.text_ac_fuel_level_label)); // modify view
                layout.addView(textFuelLevel); // add view to layout
                final EditText inputFuelLevel = new EditText(activity);
                inputFuelLevel.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputFuelLevel.setHint("76.0");
                layout.addView(inputFuelLevel);



                DialogInterface.OnClickListener dialogPositiveClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Random rand = new Random();
                                AircraftContent.AircraftItem newAircraft =
                                        new AircraftContent.AircraftItem(
                                                rand.nextInt(5000),
                                                newAc.modelName,
                                                inputSerialNumber.getText().toString(),
                                                Integer.parseInt(inputManufactureYear.getText().toString()),
                                                Double.parseDouble(inputTachTime.getText().toString()),
                                                Double.parseDouble(inputFuelLevel.getText().toString())
                                        );
                                AircraftContent.addAircraftToUserProfile(newAircraft);
                                // redirect user to AircraftListActivity to view new aircraft
                                Intent intent = new Intent(activity, AircraftListActivity.class);
                                startActivity(intent);
                            }
                        };

                DialogInterface.OnClickListener dialogNegativeClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        };

                // Add Buttons
                builder.setPositiveButton(getString(R.string.button_text_add_aircraft), dialogPositiveClickListener);
                builder.setNegativeButton(getString(R.string.button_text_cancel), dialogNegativeClickListener);
                builder.setView(layout); // place view in LinearLayout
                builder.show(); // show the alert dialog
            }
        };

        /**
         * Constructor
         * @param items
         * @param twoPane
         */
        SimpleItemRecyclerViewAdapter(List<AircraftDatabase.AircraftManufacturer.AircraftType> items,
                                      boolean twoPane){
            this.mValues = items;
            this.mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.type_list_content, parent, false);
            return new ViewHolder(view);
        }

        /**
         * Called by RecyclerView to display the data at a specified position. Updates the contents
         * of the itemView to reflect the item at the given position
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position){
            AircraftDatabase.AircraftManufacturer.AircraftType type = mValues.get(position);
            holder.mTypeName.setText(type.modelName);
            holder.mTypeDescription.setText(""); // TODO unused at present
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(this.mOnClickListener);
        }

        @Override
        public int getItemCount(){
            return mValues.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mTypeName;
            final TextView mTypeDescription;
            ViewHolder(View view) {
                super(view);
                mTypeName = (TextView) view.findViewById(R.id.type_name);
                mTypeDescription = (TextView) view.findViewById(R.id.type_description);
            }
        }
    }

    private void volleySetup(String manufacturer){

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
            .authority("v4p4sz5ijk.execute-api.us-east-1.amazonaws.com")
            .appendPath("anbdata")
            .appendPath("aircraft")
            .appendPath("designators")
            .appendPath("type-list")
            .appendQueryParameter("api_key", getString(R.string.ICAO_ISTARS_API_KEY))
            .appendQueryParameter("format","json")
            .appendQueryParameter("manufacturer",manufacturer);

        String url = builder.build().toString();

        Response.Listener<JSONArray> respListener = new Response.Listener<JSONArray>(){
            public void onResponse(JSONArray resp){
                AircraftDatabase.AircraftManufacturer.AircraftType.parseTypeJSON(resp);
            }
        };

        Response.ErrorListener errListener = new Response.ErrorListener(){
            public void onErrorResponse(VolleyError err){
                Log.e(TAG, err.toString());
            }
        };

        Request<JSONArray> request = new JsonArrayRequest(Request.Method.GET, url, null, respListener, errListener);

        // obtain request queue from Volley
        RequestQueue requestQueue = RequestSingleton.getInstance(getActivity()).getRequestQueue();
        requestQueue.add(request);
    }
}
