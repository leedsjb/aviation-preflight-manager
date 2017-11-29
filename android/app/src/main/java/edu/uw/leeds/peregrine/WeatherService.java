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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Service for getting weather data based on given coordinates.
 */

public class WeatherService extends IntentService {

    private final String TAG = "WeatherService";

    //keys for intents sent to service
    public static final String LATITUDE_KEY = "WeatherLatKey";
    public static final String LONGITUDE_KEY = "WeatherLonKey";

    //keys for broadcasting data from service
    public static final String PROCESS_WEATHER = "PROCESS_WEATHER";
    public static final String WEATHER_KEY = "WeatherForecastKey";

    public WeatherService() {
        super("WeatherService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Double lat = intent.getDoubleExtra(LATITUDE_KEY, 0);
        Double lon = intent.getDoubleExtra(LONGITUDE_KEY, 0);

        downloadWeatherData(lat, lon);
    }

    //download weather data from OpenWeather
    private void downloadWeatherData(final double latitude, final double longitude) {

        //base URL with format options
        String urlString = "http://api.openweathermap.org/data/2.5/weather?" +
                "format=json&units=imperial&lat=" + latitude +
                "&lon=" + longitude +
                "&APPID=" + getString(R.string.OPEN_WEATHER_MAP_API_KEY);

        Request request = new StringRequest(Request.Method.GET, urlString,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Log.v(TAG, response);

                        JSONObject resp;


                        //get weather data from within JSON object
                        try {
                            resp = new JSONObject(response);
                        } catch (JSONException err) {
                            Log.v(TAG, "Error parsing JSON", err);
                            return;
                        }

                        ForecastData temp = new ForecastData();
                        //parse forecast data into temporary forecast object
                        try {
                            temp.time = new Date(resp.getLong("dt") * 1000);
                            temp.temperature = resp.getJSONObject("main").getString("temp");
                            temp.weather = resp.getJSONArray("weather").getJSONObject(0).getString("main");
                            temp.icon = resp.getJSONArray("weather").getJSONObject(0).getString("icon");
                            temp.icon = "icon" + temp.icon;
                        } catch (JSONException err) {
                            Log.v(TAG, "Error parsing JSON", err);
                            return;
                        }

                        temp.latitude = latitude;
                        temp.longitude = longitude;
                        broadcastWeather(temp);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        });

        RequestSingleton.getInstance(this).add(request);
    }

    public void broadcastWeather(ForecastData forecast) {
        Log.v(TAG, "broadcasting weather");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(WeatherService.PROCESS_WEATHER);
        //broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(WEATHER_KEY, forecast);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }
}