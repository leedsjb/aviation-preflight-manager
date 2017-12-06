package edu.uw.leeds.peregrine;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    protected static DatabaseReference mDatabaseRef; // single DB ref for entire app
    private static final String NOTIF_CHANNEL_ID = "edu.uw.leeds.peregrine.channel";

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check if user is authenticated.
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // If not, present log in activity.
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());
            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }



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
        List<ToDoItem> upcomingItems = new ArrayList<ToDoItem>();
        List<InspectionContent.InspectionItem> InspectionData = InspectionContent.ITEMS;
        List<PilotPhysicalContent.PilotPhysicalItem> PhysicalData = PilotPhysicalContent.ITEMS;

        upcomingItems.addAll(InspectionData);
        upcomingItems.addAll(PhysicalData);

        UpcomingAdapter upcomingAdapter = new UpcomingAdapter(this, upcomingItems);
        upcomingListView.setAdapter(upcomingAdapter);

        // Set up Navigation Drawer
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

    /**
     * Inspection items and Physical check items populated on the landing page.
     */
    public interface ToDoItem {
        public String getId();

        // Text to show in listview
        public String getTitle();

        public Date getDate();
    }

    private class UpcomingAdapter extends ArrayAdapter<ToDoItem> {
        public UpcomingAdapter(Context context, List<ToDoItem> upcomingItems) {
            super(context, 0, upcomingItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            ToDoItem item = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getContext()).inflate(R.layout.upcoming_and_due_list_content,
                        parent,
                        false);
            }

            TextView upcomingItemTitle = (TextView) convertView.findViewById(R.id.upcoming_item_title);
            TextView upcomingItemDate = (TextView) convertView.findViewById(R.id.upcoming_item_date);

            DateFormat df = new SimpleDateFormat("MM.dd");
            String dueDate = df.format(item.getDate());

            upcomingItemTitle.setText(item.getTitle());
            upcomingItemDate.setText("Due: " + dueDate);

            return convertView;
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(TAG, "Signed in ayayayayaooooo");
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == ResultCodes.OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            } else {
                // Sign in failed, check response for error code

            }
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