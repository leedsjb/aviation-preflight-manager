package edu.uw.leeds.peregrine;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    protected static DatabaseReference mDatabaseRef; // single DB ref for entire app
    private static final String NOTIF_CHANNEL_ID = "edu.uw.leeds.peregrine.channel";

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
                notifyUser(new NotificationMessage("hello", "2 days", "high"));

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
                Intent i = new Intent(MainActivity.this, AircraftListActivity.class);
                startActivity(i);
            }
        });

        // TODO: Populate upcoming listview.
        // Connect to Firebase
        FirebaseDatabase dbInstance = FirebaseDatabase.getInstance();
        this.mDatabaseRef = dbInstance.getReference();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
        setupDrawer();
        Log.v(TAG, "Created drawer");

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
            Intent i = new Intent(this, PilotPhysicalDetailActivity.class);
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

    public void notifyUser(NotificationMessage nm) {
        // Specify where tapping this notification will navigate user
        Intent notifyIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notifyIntent, 0);

        // Construct the Big View
        // View Message
        Intent notifyViewIntent = new Intent(getApplicationContext(), MainActivity.class);
//                notifyViewIntent.setAction(CommonConstants.ACTION_DISMISS) // to dismiss on press
        PendingIntent pendIntView = PendingIntent.getActivity(getApplicationContext(), 0, notifyViewIntent, 0);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //Oreo
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            Notification.Builder builder = new Notification.Builder(getApplicationContext(), NOTIF_CHANNEL_ID)
                    .setContentTitle(nm.message)
                    .setContentText(nm.timeline)
                    .setSmallIcon(R.drawable.ic_menu_camera)//TODO change
                    .setContentIntent(pendingIntent) // set destination when notification is tapped
                    .addAction(0, "View", pendIntView);

            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, "General Notification", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Test");
            channel.enableLights(true);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(1, builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(nm.message)
                    .setContentText(nm.timeline)
                    .setSmallIcon(R.drawable.ic_menu_camera) //TODO change
                    .setContentIntent(pendingIntent)
                    .addAction(0, "View", pendIntView);

            // Post Notification
            notificationManager.notify(1, builder.build()); // Post notification

        }
    }
}
// TODO: Landing page
// TODO: Nav drawer @Jessica
// TODO: Upcoming flights + Enter new airport @Jessica
// TODO: Notifications: @Jessica
// TODO: Pilot physical requirements Master/Detail @Ishan
// TODO: Aircraft Airworthiness Master/Detail @Ishan
// TODO: Aircraft Master/Detail @Ishan
//aircraft related todos