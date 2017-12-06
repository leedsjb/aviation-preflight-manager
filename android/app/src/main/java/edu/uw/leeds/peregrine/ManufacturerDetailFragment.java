package edu.uw.leeds.peregrine;

import android.app.Activity;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;


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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ManufacturerDetailFragment() {
    }

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.manufacturer_detail, container, false);

        // Show the manufacturer name as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.manufacturer_detail)).setText(Integer.toString(mItem.numOfTypes));
        }

        // setup volley
        volleySetup(mItem.manufacturerCode);

        return rootView;
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
        RequestQueue requestQueue = RequestSingleton.getInstance(getActivity()).getRequestQueue(); // TODO getActivity() appropriate here?
        requestQueue.add(request);

    }
}
