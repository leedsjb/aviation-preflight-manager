package edu.uw.leeds.peregrine;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Airport Service for getting information about airports based on a code
 * @author Jakersnorth
 * @version Wednesday December 6, 2017
 */
public class AirportService extends IntentService {

    private final String TAG = "WeatherService";

    //keys for intents sent to service
    public static final String AIRPORT_CODE_KEY = "AirportCodeKey";

    //keys for broadcasting data from service
    public static final String PROCESS_AIRPORT = "PROCESS_AIRPORT";
    public static final String AIRPORT_KEY = "AirportKey";

    public AirportService() {
        super("AirportService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String airportCode = intent.getStringExtra(AIRPORT_CODE_KEY);
        downloadAirportData(airportCode);
    }

    //download weather data from OpenWeather
    private void downloadAirportData(final String airportCode) {

        // TODO build with URI.Builder
        String urlString = "http://services.faa.gov/airport/status/"
                + airportCode + "?format=application/json";

        Request request = new StringRequest(Request.Method.GET, urlString,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        JSONObject resp;

                        //get weather data from within JSON object
                        try {
                            resp = new JSONObject(response);
                        } catch (JSONException err) {
                            Log.v(TAG, "Error parsing JSON", err);
                            return;
                        }

                        AirportData temp = new AirportData();
                        //parse forecast data into temporary forecast object
                        try {
                            temp.code = resp.getString("IATA");
                            temp.name = resp.getString("name");
                            temp.weather = "Weather: " + resp.getJSONObject("weather").getString("weather");
                            temp.delayed = resp.getBoolean("delay");
                            temp.delayReason = resp.getJSONObject("status").getString("reason");
                            temp.delayType = resp.getJSONObject("status").getString("type");
                        } catch (JSONException err) {
                            Log.v(TAG, "Error parsing JSON", err);
                            return;
                        }
                        broadcastAirport(temp);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
            }
        });

        RequestSingleton.getInstance(this).add(request); // add request to Volley queue
    }

    /**
     * Sends AirportData as a broadcast
     * @param airport the airport to broadcast
     */
    public void broadcastAirport(AirportData airport) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(AirportService.PROCESS_AIRPORT);
        broadcastIntent.putExtra(AIRPORT_KEY, airport);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }
}
