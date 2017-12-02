package edu.uw.leeds.peregrine;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to describe aircraft data from the ICAO database
 * https://www.icao.int/safety/iStars/Pages/API-Data-Service.aspx
 * @author benjaminleeds
 * @version Revised: 12/1/17
 * Created: Friday December 1, 2017
 */

class AircraftDatabase {

    private static final String TAG = "AircraftDatabase";

    // Array of aircraft manufacturers from ICAO API
    protected static final List<AircraftManufacturer> MANUFACTURERS = new ArrayList<>();

    // MAp of aircraft manufacturers from ICAO API by ID
    protected static final Map<Integer, AircraftManufacturer> MANUFACTURER_MAP = new HashMap<>();


    /**
     * An individual aircraft manufacturer.
     */
    protected static class AircraftManufacturer{
        protected final Integer id; // id for RecyclerView
        protected final int numOfTypes; // how many types manufacturer owns
        protected final String manufacturerCode; // the name of the manufacturer in DOC8643 format

        /**
         * Constructor
         * @param id
         * @param numOfTypes
         * @param manufacturerCode
         */
        public AircraftManufacturer(Integer id, int numOfTypes, String manufacturerCode){
            this.id = id;
            this.numOfTypes = numOfTypes;
            this.manufacturerCode = manufacturerCode;
        }

        /**
         * Parses the list of manufacturers from a JSONArray of JSONObjects to Java objects
         * @param data
         */
        protected static void parseManufacturerJSON(JSONArray data){
            try {
                int numManufacturers = data.length();
                for (int i = 0; i < numManufacturers; i++) { // iterate over manufacturer JSON objs
                    JSONObject manufacturer = data.getJSONObject(i);
                    Integer numTypes = Integer.parseInt(manufacturer.getString("types"));
                    String manufacturerCode = manufacturer.getString("manufacturer_code");
                    AircraftManufacturer manufacturerObj = new AircraftManufacturer(i, numTypes, manufacturerCode);
                    MANUFACTURERS.add(manufacturerObj);
                }
            } catch (JSONException e){
                Log.e(TAG, e.toString());
            }
        }
    }

    /**
     * An individual aircraft type.
     */
//    protected static class AircraftType{
//        protected final String id;
//        public final String manufacturer;
//        protected final String modelName;
//
//    }
}
