package edu.uw.leeds.peregrine;

import android.util.Log;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author saksi
 * Created: 11/28/17
 * Modified: 12/4/17
 */
public class AircraftContent {
    /**
     * An array of aircraft items.
     */
    public static final List<AircraftItem> ITEMS = new ArrayList<>();

    /**
     * A map of aircraft items, by ID.
     */
    public static final Map<Integer, AircraftItem> ITEM_MAP = new HashMap<>();

    private static final String TAG = "AircraftContent";

    private static ChildEventListener childEventListener;

    private static final String firebasePathString = "aircraft-list";

    private static DatabaseReference aircraftDbReference = MainActivity.mDatabaseRef.child(firebasePathString);

    /**
     * Called by onCreate() in AircraftListActivity
     * Attaches to Firebase database and initializes ITEMS with cloud data
     */
    protected static void initializeData(){
        // reference to the aircraft node in Firebase

        // clear data to prevent duplicates
        ITEMS.clear(); // clear data from local ArrayList<>
        ITEM_MAP.clear(); // clear data from local Map

        // listen for changes to the aircraft node and its children in Firebase
        ChildEventListener aircraftEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildName) {

                // un-marshal aircraft object from Firebase snapshot
                AircraftItem acItem = dataSnapshot.getValue(AircraftItem.class);

                addItem(acItem); // add item from Firebase to local data store

                int intId = acItem.getId(); // convert String id to int TODO delete if not needed

                AircraftListActivity.notifyChange(intId); // notify RecyclerView of data change

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildChanged){
                Log.e(TAG,"child changed");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot){
                Log.e(TAG,"child removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildName){
                Log.e(TAG,"child moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "database error");
            }
        };

        childEventListener = aircraftDbReference.addChildEventListener(aircraftEventListener); // add event listener

    }

    /**
     * removes ChildEventListener from DatabaseReference when activity is paused
     */
    protected static void removeEvListener(){
        if(childEventListener != null){
            aircraftDbReference.removeEventListener(childEventListener);
        }
    }

    /**
     * Adds the user selected aircraft to the users list of aircraft
     * stored in Firebase
     * @param aircraftToAdd the aircraft to add
     */
    protected static void addAircraftToUserProfile(AircraftItem aircraftToAdd){

        String key = aircraftDbReference.push().getKey(); // get key for new AircraftItem

        Map<String, Object> aircraftValues = aircraftToAdd.toMap(); // marshal object to map

        // create empty hashmap to contain updates to Firebase
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/" + firebasePathString +"/" + key, aircraftValues); // load Map

        MainActivity.mDatabaseRef.updateChildren(childUpdates); // send to Firebase

    }

    /**
     * Adds an AircraftItem to the list of aircraft
     * @param item the item to add to the aircraft list and map
     */
    private static void addItem(AircraftItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * Created by saksi on 11/27/17.
     *
     * One inspection task for aircraft airworthiness.
     */
    public static class AircraftItem {
        public int id;
        public String planeName;
        public String serialNumber;
        public int yearOfManufacture;
        public double tachometerTime;
        public double fuelLevel;

        public AircraftItem(int id, String planeName, String serialNumber, int yearOfManufacture,double tachometerTime, double fuelLevel) {
            this.id = id;
            this.planeName = planeName;
            this.serialNumber = serialNumber;
            this.yearOfManufacture = yearOfManufacture;
            this.tachometerTime = tachometerTime;
            this.fuelLevel = fuelLevel;
        }

        // empty constructor for Firebase
        public AircraftItem(){};

        @Exclude
        public int getId(){
            return this.id;
        }

        @Exclude
        public String getPlaneName(){
            return this.planeName;
        }

        @Exclude
        public String getSerialNumber(){
            return this.serialNumber;
        }

        @Exclude
        public int getYearOfManufacture(){
            return this.yearOfManufacture;
        }

        @Exclude
        public double getTachometerTime(){
            return this.tachometerTime;
        }

        @Exclude
        public double getFuelLevel(){
            return this.fuelLevel;
        }

        @Exclude
        public Map<String, Object> toMap(){
            HashMap<String, Object> result = new HashMap<>();
            result.put("id", this.id);
            result.put("planeName", this.planeName);
            result.put("serialNumber", this.serialNumber);
            result.put("yearOfManufacture", this.yearOfManufacture);
            result.put("tachometerTime", this.tachometerTime);
            result.put("fuelLevel", this.fuelLevel);

            return result;
        }

        @Exclude // excluded for FireBase purposes
        public String toString() {
            return this.planeName;
        }
    }
}
