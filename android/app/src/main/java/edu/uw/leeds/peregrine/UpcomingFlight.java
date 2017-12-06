package edu.uw.leeds.peregrine;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UpcomingFlight extends AppCompatActivity {

    private ArrayList<AirportData> mData = new ArrayList<>();
    private MyAdapter myAdapter;

    private static final int FINE_LOCATION_REQUEST_CODE = 199;
    private static final String TAG = "UpcomingFlightActivity";
    private static final String SAVED_AIRPORT_DATA_KEY = "saved_airport_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upcoming_flight);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myAdapter = new MyAdapter(this, mData);
        ListView listView = findViewById(R.id.upcoming_flight_list_view);
        listView.setAdapter(myAdapter);

        //creates and registers broadcast receiver to receive intents from AirportService
        MyReceiver mBroadcastReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AirportService.PROCESS_AIRPORT);
        /*
        filter.addAction(CurrentLocationService.PROCESS_LOCATION);
        filter.addAction(CurrentLocationService.REQUEST_LOCATION_PERM);
        filter.addAction(WeatherService.PROCESS_WEATHER);
        */
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.registerReceiver(mBroadcastReceiver, filter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Drawable fabIcon = ContextCompat.getDrawable(this, R.drawable.ic_add_black_24dp);
        fab.setImageDrawable(fabIcon);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpcomingFlight.this, R.style.Theme_AppCompat_Dialog_Alert);
                builder.setTitle("Input Airport IATA Code");

                // Set up the input
                final EditText input = new EditText(getApplicationContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //UpcomingFlightObject upcoming = new UpcomingFlightObject(null, input.getText().toString(), null, null);
                        //mData.add(upcoming);
                        Intent airportIntent = new Intent(UpcomingFlight.this, AirportService.class);
                        airportIntent.putExtra(AirportService.AIRPORT_CODE_KEY, input.getText().toString());
                        startService(airportIntent);
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.v(TAG, "saving data");
        savedInstanceState.putParcelableArrayList(SAVED_AIRPORT_DATA_KEY, mData);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(TAG, "restoring");
        if(savedInstanceState != null) {
            Log.v(TAG, "have data");
            ArrayList<AirportData> stored_airports = savedInstanceState.getParcelableArrayList(SAVED_AIRPORT_DATA_KEY);
            if (stored_airports != null) {
                Log.v(TAG, "adding");
                mData.addAll(stored_airports);
            }
            myAdapter.notifyDataSetChanged();
        }

    }

    public static class MyAdapter extends ArrayAdapter<AirportData> {

        public MyAdapter(Context context, ArrayList<AirportData> ufs) {
            super(context, R.layout.upcoming_flight_content, ufs);
        }

        @Override
        @TargetApi(21)
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            AirportData airportData = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.upcoming_flight_content, parent, false);
            }
            // Lookup view for data population
            TextView airport = (TextView) convertView.findViewById(R.id.uf_airport);
            TextView restriction = (TextView) convertView.findViewById(R.id.uf_restrictions);
            ImageView delay = (ImageView) convertView.findViewById(R.id.uf_urgency);
            TextView delayInfo = (TextView) convertView.findViewById(R.id.uf_delay_info);

            // Populate the data into the template view using the data object
            airport.setText(airportData.name);
            restriction.setText(airportData.weather);
            //sets delay information based on whether airport is experiencing delays
            if(airportData.delayed) {
                delay.setImageResource(R.drawable.ic_warning_black_24dp);
                delayInfo.setText(airportData.delayType + ": " + airportData.delayReason);
            } else {
                delay.setImageResource(R.drawable.ic_check_circle_black_24dp);
                delayInfo.setText("");
            }
//            }
            // Return the completed view to render on screen
            return convertView;
        }
    }

    public class UpcomingFlightObject {
        public String icon;
        public String airport;
        public String restriction;
        public String urgency;

        // Constructor to convert JSON object into a Java class instance
        public UpcomingFlightObject(String icon, String airport, String restriction, String urgency) {
            this.icon = icon;
            this.airport = airport;
            this.restriction = restriction;
            this.urgency = urgency;
        }
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            /*
            if (intent.getAction().equals(CurrentLocationService.REQUEST_LOCATION_PERM)) {
                int permissionCheck = ContextCompat.checkSelfPermission(getParent(), Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    Intent locationIntent = new Intent(UpcomingFlight.this, CurrentLocationService.class);
                    startService(locationIntent);
                } else {
                    ActivityCompat.requestPermissions(getParent(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
                }
            } else if (intent.getAction().equals(CurrentLocationService.PROCESS_LOCATION)) {
                Log.v(TAG, "location received");
                Intent weatherIntent = new Intent(UpcomingFlight.this, WeatherService.class);
                weatherIntent.putExtra(WeatherService.LATITUDE_KEY, intent.getDoubleExtra(CurrentLocationService.LOCATION_LATITUDE_KEY, 0));
                weatherIntent.putExtra(WeatherService.LONGITUDE_KEY, intent.getDoubleExtra(CurrentLocationService.LOCATION_LONGITUDE_KEY, 0));
                startService(weatherIntent);
            } else if (intent.getAction().equals(WeatherService.PROCESS_WEATHER)) {
                Log.v(TAG, ((ForecastData) intent.getParcelableExtra(WeatherService.WEATHER_KEY)).weather);
            }*/
            if(intent.getAction().equals(AirportService.PROCESS_AIRPORT)) {
                Log.v(TAG, "something received");
                AirportData airportData = intent.getParcelableExtra(AirportService.AIRPORT_KEY);
                mData.add(airportData);
                myAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent locationIntent = new Intent(UpcomingFlight.this, CurrentLocationService.class);
                startService(locationIntent);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}


