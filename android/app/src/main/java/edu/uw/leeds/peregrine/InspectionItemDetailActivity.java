package edu.uw.leeds.peregrine;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import java.util.HashMap;
import java.util.Map;

/**
 * An activity representing a single InspectionItem detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link InspectionItemListActivity}.
 */
public class InspectionItemDetailActivity extends AppCompatActivity {

    private final String ACTIVITY_TITLE_KEY = "titleKey";
    public final String TAG = "InspectionDetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspectionitem_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(InspectionItemDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(InspectionItemDetailFragment.ARG_ITEM_ID));
            InspectionItemDetailFragment fragment = new InspectionItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.inspectionitem_detail_container, fragment)
                    .commit();

            FloatingActionButton fab = findViewById(R.id.fab);
            Drawable fabIcon = ContextCompat.getDrawable(this, R.drawable.ic_check_circle_black_24dp);
            fab.setImageDrawable(fabIcon);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InspectionContent.InspectionItem curr =
                            InspectionContent.ITEM_MAP.get(getIntent().getStringExtra(InspectionItemDetailFragment.ARG_ITEM_ID));
                    Long yearInMilli = Long.parseLong("31556952000");
                    curr.dueNext = curr.dueNext + yearInMilli; //number of seconds in a year

                    for(String key : InspectionContent.listeningKeys) {
                        if(curr.id.equals(key.hashCode() + "")) {
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/inspection-list/" + key + "/abstraction/", curr);
                            MainActivity.mDatabaseRef.updateChildren(childUpdates);
                        }
                    }
                }
            });
        } else {
            setTitle(savedInstanceState.getString(ACTIVITY_TITLE_KEY));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, InspectionItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
