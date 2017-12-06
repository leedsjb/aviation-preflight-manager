package edu.uw.leeds.peregrine;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.uw.leeds.peregrine.dummy.DummyContent;

import java.util.List;

/**
 * An activity representing a list of Manufacturers. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ManufacturerDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ManufacturerListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private static final String TAG = "ManufactureListActivity";
    private SimpleItemRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "ManufacturerListActivity onCreate() callback executed");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manufacturer_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.manufacturer_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        volleySetup();

        View recyclerView = findViewById(R.id.manufacturer_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void volleySetup(){

        String url = "https://v4p4sz5ijk.execute-api.us-east-1.amazonaws.com/anbdata/aircraft/designators/manufacturer-list?api_key=5f5a7e20-bdf6-11e7-a841-f125d0e359d9&format=json&manufacturer=";

        Response.Listener<JSONArray> respListener = new Response.Listener<JSONArray>(){
            public void onResponse(JSONArray resp){
                AircraftDatabase.AircraftManufacturer.parseManufacturerJSON(resp);
                adapter.notifyDataSetChanged(); // update adapter with new data
            }
        };

        Response.ErrorListener errListener = new Response.ErrorListener(){
            public void onErrorResponse(VolleyError err){
                Log.e(TAG, err.toString());

            }
        };

        Request<JSONArray> request = new JsonArrayRequest(Request.Method.GET, url, null, respListener, errListener);

        // obtain request queue from Volley
        RequestQueue requestQueue = RequestSingleton.getInstance(this).getRequestQueue();
        requestQueue.add(request);

    }

    /**
     * Sets up the Recycler View with content
     * The data to populated the view is retrieved from a class static instance field pointing to a list
     * @param recyclerView
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        this.adapter = new SimpleItemRecyclerViewAdapter(this, AircraftDatabase.MANUFACTURERS, mTwoPane);
        recyclerView.setAdapter(this.adapter);
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ManufacturerListActivity mParentActivity;
        private final List<AircraftDatabase.AircraftManufacturer> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AircraftDatabase.AircraftManufacturer item = (AircraftDatabase.AircraftManufacturer) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putInt(ManufacturerDetailFragment.ARG_ITEM_ID, item.id);
                    ManufacturerDetailFragment fragment = new ManufacturerDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.manufacturer_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ManufacturerDetailActivity.class);
                    intent.putExtra(ManufacturerDetailFragment.ARG_ITEM_ID, item.id);

                    context.startActivity(intent);
                }
            }
        };

        /**
         * Cosntructor
         * @param parent
         * @param items
         * @param twoPane
         */
        SimpleItemRecyclerViewAdapter(ManufacturerListActivity parent,
                                      List<AircraftDatabase.AircraftManufacturer> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.manufacturer_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            AircraftDatabase.AircraftManufacturer manufacturer = mValues.get(position);

            holder.mIdView.setText("");
            holder.mContentView.setText(manufacturer.manufacturerCode);
            holder.mNumTypes.setText("");

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        /**
         * ViewHolder Class
         */
        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;
            final TextView mNumTypes;

            /**
             * Constructor
             * @param view
             */
            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
                mNumTypes = (TextView) view.findViewById(R.id.num_types);
            }
        }
    }
}
