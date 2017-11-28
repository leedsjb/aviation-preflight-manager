package edu.uw.leeds.peregrine;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by sekam_000 on 11/27/2017.
 */

public class CurrentLocationService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
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
        }
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
        } else {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(CurrentLocationService.REQUEST_LOCATION_PERM);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(CurrentLocationService.PROCESS_LOCATION);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(LOCATION_LONGITUDE_KEY, location.getLongitude());
        broadcastIntent.putExtra(LOCATION_LATITUDE_KEY, location.getLatitude());
        sendBroadcast(broadcastIntent);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest();
        request.setInterval(1000);
        request.setFastestInterval(500);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(TAG, "Connection to Google Play Services failed");
    }
}
