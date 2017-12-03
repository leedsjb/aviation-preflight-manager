package edu.uw.leeds.peregrine;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UpcomingFlight extends AppCompatActivity {

    private ArrayList<UpcomingFlightObject> mData = new ArrayList<UpcomingFlightObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_flight);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Add airport name");

                // Set up the input
                final EditText input = new EditText(getApplicationContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpcomingFlightObject upcoming = new UpcomingFlightObject(null, input.getText().toString(), null, null);
                        mData.add(upcoming);
                    }
                });
                builder.show();
            }
        });
    }

    public static class MyAdapter extends ArrayAdapter<UpcomingFlight> {

        public MyAdapter(Context context, ArrayList<UpcomingFlight> ufs) {
            super(context, R.layout.upcoming_flight_content, ufs);
        }

        @Override @TargetApi(21)
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
//            UpcomingFlightObject uf = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.upcoming_flight_content, parent, false);
            }
            // Lookup view for data population
            ImageView icon = (ImageView) convertView.findViewById(R.id.uf_icon);
            TextView airport = (TextView) convertView.findViewById(R.id.uf_airport);
            TextView restriction = (TextView) convertView.findViewById(R.id.uf_restrictions);
            ImageView urgency = (ImageView) convertView.findViewById(R.id.uf_urgency);

            // Populate the data into the template view using the data object
//            if(uf!=null) {
//                icon.setImage(uf.icon);
                airport.setText("airp");
//                restriction.setText(uf.restriction);
//                urgency.setImage(uf.urgency);
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
}
