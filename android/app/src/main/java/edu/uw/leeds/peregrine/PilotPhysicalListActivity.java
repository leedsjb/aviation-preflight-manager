package edu.uw.leeds.peregrine;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * An activity representing a list of PilotPhysicals. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PilotPhysicalDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PilotPhysicalListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private static RecyclerView.Adapter adapter;

    private static final String TAG = "PilotPhyListAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilotphysical_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Drawable fabIcon = ContextCompat.getDrawable(this, R.drawable.ic_add_black_24dp);
        fab.setImageDrawable(fabIcon);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PilotPhysicalListActivity.this, R.style.Theme_AppCompat_Dialog_Alert);
                builder.setTitle("Add New Physical Inspection");

                LinearLayout layout = new LinearLayout(PilotPhysicalListActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                // Set up the input
                final TextView textTitle = new TextView(getApplicationContext());
                textTitle.setText("Title");
                layout.addView(textTitle);

                final EditText inputTitle = new EditText(getApplicationContext());
                inputTitle.setInputType(InputType.TYPE_CLASS_TEXT);
                inputTitle.setHint("title");
                layout.addView(inputTitle);

                final TextView textDescription = new TextView(getApplicationContext());
                textDescription.setText("Description");
                layout.addView(textDescription);

                final EditText inputDescription = new EditText(getApplicationContext());
                inputDescription.setInputType(InputType.TYPE_CLASS_TEXT);
                inputDescription.setHint("description");
                layout.addView(inputDescription);

                final TextView textRequirements = new TextView(getApplicationContext());
                textRequirements.setText("Requirements");
                layout.addView(textRequirements);

                final EditText inputRequirements = new EditText(getApplicationContext());
                inputRequirements.setInputType(InputType.TYPE_CLASS_TEXT);
                inputRequirements.setHint("requirements");
                layout.addView(inputRequirements);

                final TextView textResources = new TextView(getApplicationContext());
                textResources.setText("Resources");
                layout.addView(textResources);

                final EditText inputResources = new EditText(getApplicationContext());
                inputResources.setInputType(InputType.TYPE_CLASS_TEXT);
                inputResources.setHint("resources");
                layout.addView(inputResources);

                final TextView textNextDue = new TextView(getApplicationContext());
                textNextDue.setText("Next Due Date");
                layout.addView(textNextDue);

                final EditText inputDueNext = new EditText(getApplicationContext());
                inputDueNext.setInputType(InputType.TYPE_CLASS_DATETIME);
                inputDueNext.setHint("MM-dd-YYYY");
                layout.addView(inputDueNext);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
                        Date dueDate = null;
                        try {
                            dueDate = df.parse(inputDueNext.getText().toString());
                        } catch (ParseException err) {
                            Log.v(TAG, "invalid date sumbitted");
                        }
                        PilotPhysicalContent.PilotPhysicalItem newPilotItem = new PilotPhysicalContent.PilotPhysicalItem(
                                null,
                                inputTitle.getText().toString(),
                                inputDescription.getText().toString(),
                                inputRequirements.getText().toString(),
                                inputResources.getText().toString(),
                                dueDate,
                                "none"
                        );
                        PilotPhysicalContent.addPilotItemToProfile(newPilotItem);

                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.setView(layout);
                builder.show();
            }
        });

        if (findViewById(R.id.pilotphysical_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.pilotphysical_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        // TODO remove this temporary way of creating a new object
        // TODO and replace with an activity to do so
        PilotPhysicalContent.PilotPhysicalItem sample = new PilotPhysicalContent.PilotPhysicalItem(
            "97",
            "Pilot Physical",
            "This is an inspection item",
            "You are required to inspect this thing",
            "These are you resources",
            new Date(),
            "Name of the image");

//        PilotPhysicalContent.addItem(sample);
//        PilotPhysicalContent.addPilotItemToProfile(sample);

        // initialize pilot data for this user
        PilotPhysicalContent.initializeData();

    }

    @Override
    public void onPause(){
        super.onPause();
        PilotPhysicalContent.removeEvListener();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new SimpleItemRecyclerViewAdapter(this, PilotPhysicalContent.ITEMS, mTwoPane);
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

        private final PilotPhysicalListActivity mParentActivity;
        private final List<PilotPhysicalContent.PilotPhysicalItem> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PilotPhysicalContent.PilotPhysicalItem item = (PilotPhysicalContent.PilotPhysicalItem) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(PilotPhysicalDetailFragment.ARG_ITEM_ID, item.id);
                    PilotPhysicalDetailFragment fragment = new PilotPhysicalDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.pilotphysical_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, PilotPhysicalDetailActivity.class);
                    intent.putExtra(PilotPhysicalDetailFragment.ARG_ITEM_ID, item.id);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(PilotPhysicalListActivity parent,
                                      List<PilotPhysicalContent.PilotPhysicalItem> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pilotphysical_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).title);

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
