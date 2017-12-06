package edu.uw.leeds.peregrine;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

/**
 * An activity representing a list of Aircrafts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link AircraftDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class AircraftListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private static final String TAG = "AircraftListActivity";
    private static SimpleItemRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aircraft_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        // use ContextCompat due to getDrawable() changes in SDK 21
        Drawable fabIcon = ContextCompat.getDrawable(this, R.drawable.ic_add_black_24dp);
        fab.setImageDrawable(fabIcon);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AircraftListActivity.this, ManufacturerListActivity.class);
                startActivity(intent);
            }
        });

        if (findViewById(R.id.aircraft_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        // temp: create new aircraft for db, will eventually come from user selectable ac db
//        AircraftContent.AircraftItem newAc = new AircraftContent.AircraftItem(
//                "99", "DA-40", "40.179",
//                "2003", "100", "full"
//        );

        // add ac to user profile
        // TODO implement activity for this method to be called from
//        AircraftContent.addAircraftToUserProfile(newAc);

        // initialize ac data for this user
        AircraftContent.initializeData();

        View recyclerView = findViewById(R.id.aircraft_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new SimpleItemRecyclerViewAdapter(this, AircraftContent.ITEMS, mTwoPane);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Tells adapter there has been a change
     * @param id the id of the item that has changed
     */
    protected static void notifyChange(int id){
        adapter.notifyItemChanged(id);
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final AircraftListActivity mParentActivity;
        private final List<AircraftContent.AircraftItem> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AircraftContent.AircraftItem item = (AircraftContent.AircraftItem) view.getTag();
                if (mTwoPane) { // wide-screen layout, open detail fragment alongside list
                    Bundle arguments = new Bundle();
                    arguments.putInt(AircraftDetailFragment.ARG_ITEM_ID, item.id);
                    AircraftDetailFragment fragment = new AircraftDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.aircraft_detail_container, fragment)
                            .commit();
                } else { // narrow-screen layout, open details fragment in new activity
                    Context context = view.getContext();
                    Intent intent = new Intent(context, AircraftDetailActivity.class);
                    intent.putExtra(AircraftDetailFragment.ARG_ITEM_ID, item.id);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(AircraftListActivity parent,
                                      List<AircraftContent.AircraftItem> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.aircraft_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).serialNumber);
            holder.mContentView.setText(mValues.get(position).planeName);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
}
