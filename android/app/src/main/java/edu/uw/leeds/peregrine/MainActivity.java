package edu.uw.leeds.peregrine;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    protected DatabaseReference mDatabaseRef; // single DB ref for entire app

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Onclick Listeners
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        Button prepareButton = (Button) findViewById(R.id.prepare_button);
        prepareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, InspectionItemListActivity.class);
                startActivity(i);
            }
        });

        Button statusButton = (Button) findViewById(R.id.status_button);
        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AircraftListActivity.class);
                startActivity(i);
            }
        });

        // TODO: Move to where we need medical requirements.
//        Intent i = new Intent(MainActivity.this, PilotPhysicalListActivity.class);
//        startActivity(i);

        // TODO: Populate upcoming listview.
        // connect to Firebase
        FirebaseDatabase dbInstance = FirebaseDatabase.getInstance();
        this.mDatabaseRef = dbInstance.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
// TODO: Landing page
// TODO: Nav drawer @Jessica
// TODO: Upcoming flights + Enter new airport @Jessica
// TODO: Notifications: @Jessica
// TODO: Pilot physical requirements Master/Detail @Ishan
// TODO: Aircraft Airworthiness Master/Detail @Ishan
// TODO: Aircraft Master/Detail @Ishan