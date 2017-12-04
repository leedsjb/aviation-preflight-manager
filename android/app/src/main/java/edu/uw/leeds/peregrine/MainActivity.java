package edu.uw.leeds.peregrine;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    protected static DatabaseReference mDatabaseRef; // single DB ref for entire app

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
                Intent i = new Intent(MainActivity.this, UpcomingFlight.class);
                startActivity(i);
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
                Intent i = new Intent(MainActivity.this, UpcomingFlight.class);
                startActivity(i);
            }
        });

        Button planesButton = (Button) findViewById(R.id.planes_button);
        planesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AircraftListActivity.class);
                startActivity(i);
            }
        });

        Button medicalButton = (Button) findViewById(R.id.medical_button);
        medicalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, PilotPhysicalListActivity.class);
                startActivity(i);
            }
        });

        // Connect to Firebase
        FirebaseDatabase dbInstance = FirebaseDatabase.getInstance();
        this.mDatabaseRef = dbInstance.getReference();

        // TODO: Populate upcoming and due listview.
        ListView upcomingListView = (ListView) findViewById(R.id.upcoming_list_view);
        List<InspectionContent.InspectionItem> InspectionData = InspectionContent.ITEMS;
        List<PilotPhysicalContent.PilotPhysicalItem> PhysicalData = PilotPhysicalContent.ITEMS;

        List<String> myStringArray = new ArrayList<>();
        myStringArray.add("First");
        myStringArray.add("Second");
        myStringArray.add("Third");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.upcoming_and_due_list_content,
                R.id.upcoming_item_title,
                myStringArray);
        upcomingListView.setAdapter(adapter);

//        upcomingListView.setAdapter(upcomingAdapter);


        // Set up Navigation Drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
        setupDrawer();

        // Set details of header in nav drawer.
        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.pilot_name);
        TextView nav_email = (TextView)hView.findViewById(R.id.pilot_email);
        // TODO: Get user shared preferences
        // TODO: Set image
        nav_user.setText("Piloty McPilotFace");
        nav_email.setText("pilot@plane.com");
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.v(TAG, "Navigation Item selected");
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.my_planes) {
            Intent i = new Intent(this, AircraftListActivity.class);
            startActivity(i);
        } else if (id == R.id.prep_for_flight) {
            Intent i = new Intent(this, UpcomingFlight.class);
            startActivity(i);
        } else if (id == R.id.maintenance_airworthiness) {
            Intent i = new Intent(this, InspectionItemListActivity.class);
            startActivity(i);
        } else if (id == R.id.medical_requirements) {
            Intent i = new Intent(this, PilotPhysicalListActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawer,
                (Toolbar) findViewById(R.id.toolbar),
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }
}
// TODO: Landing page
// TODO: Nav drawer @Jessica
// TODO: Upcoming flights + Enter new airport @Jessica
// TODO: Notifications: @Jessica
// TODO: Pilot physical requirements Master/Detail @Ishan
// TODO: Aircraft Airworthiness Master/Detail @Ishan
// TODO: Aircraft Master/Detail @Ishan