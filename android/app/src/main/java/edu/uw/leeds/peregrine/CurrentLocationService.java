package edu.uw.leeds.peregrine;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Location service for getting the current user location
 */

public class CurrentLocationService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private final String TAG = "CurrentLocationService";

    public static final String PROCESS_LOCATION = "PROCESS_LOCATION";
    public static final String REQUEST_LOCATION_PERM = "REQUEST_LOCATION";

    public static final String LOCATION_LONGITUDE_KEY = "LONGITUDE";
    public static final String LOCATION_LATITUDE_KEY = "LATITUDE";

    private LocationRequest request;
    private GoogleApiClient mGoogleApiClient;

    public CurrentLocationService() {
        super("MapSavingService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        } else {
            //startLocationRequest();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationRequest();
    }

    private void startLocationRequest() {
        Log.v(TAG, "starting location request");
        request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "requesting location");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
        } else {
            Log.v(TAG, "need location perms");
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(CurrentLocationService.REQUEST_LOCATION_PERM);
            //broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(TAG, "Connection to Google Play Services failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "location change");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(CurrentLocationService.PROCESS_LOCATION);
        //broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(LOCATION_LONGITUDE_KEY, location.getLongitude());
        broadcastIntent.putExtra(LOCATION_LATITUDE_KEY, location.getLatitude());
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
        mGoogleApiClient = null;
    }
}

/*
private class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "something recieved");
        if(intent.getAction().equals(CurrentLocationService.REQUEST_LOCATION_PERM)) {
            int permissionCheck = ContextCompat.checkSelfPermission(getParent(), Manifest.permission.ACCESS_FINE_LOCATION);
            if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
                Intent locationIntent = new Intent(MainActivity.this, CurrentLocationService.class);
                startService(locationIntent);
            } else {
                ActivityCompat.requestPermissions(getParent(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
            }
        } else if(intent.getAction().equals(CurrentLocationService.PROCESS_LOCATION)) {
            Log.v(TAG, "location received");
            Intent weatherIntent = new Intent(MainActivity.this, WeatherService.class);
            weatherIntent.putExtra(WeatherService.LATITUDE_KEY, intent.getDoubleExtra(CurrentLocationService.LOCATION_LATITUDE_KEY, 0));
            weatherIntent.putExtra(WeatherService.LONGITUDE_KEY, intent.getDoubleExtra(CurrentLocationService.LOCATION_LONGITUDE_KEY, 0));
            startService(weatherIntent);
        }
        else if (intent.getAction().equals(WeatherService.PROCESS_WEATHER)) {
            Log.v(TAG, ((ForecastData)intent.getParcelableExtra(WeatherService.WEATHER_KEY)).weather);
        }
    }
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == FINE_LOCATION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent locationIntent = new Intent(MainActivity.this, CurrentLocationService.class);
                startService(locationIntent);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


        MyReceiver mBroadcastReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CurrentLocationService.PROCESS_LOCATION);
        filter.addAction(CurrentLocationService.REQUEST_LOCATION_PERM);
        filter.addAction(WeatherService.PROCESS_WEATHER);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.registerReceiver(mBroadcastReceiver, filter);
*/