package edu.uw.leeds.peregrine;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Source credits to Joel Ross/INFO448
 */

public class RequestSingleton {
    //the single instance of this singleton
    private static RequestSingleton instance;

    private RequestQueue requestQueue = null; //the singleton's RequestQueue

    //private constructor; cannot instantiate directly
    private RequestSingleton(Context ctx) {
        //create the requestQueue
        this.requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
    }

    //call this "factory" method to access the Singleton
    public static RequestSingleton getInstance(Context ctx) {
        //only create the singleton if it doesn't exist yet
        if (instance == null) {
            instance = new RequestSingleton(ctx);
        }

        return instance; //return the singleton object
    }

    //get queue from singleton for direct action
    public RequestQueue getRequestQueue() {
        return this.requestQueue;
    }

    //convenience wrapper method
    public <T> void add(Request<T> req) {
        requestQueue.add(req);
    }

}