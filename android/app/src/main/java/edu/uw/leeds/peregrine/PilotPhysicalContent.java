package edu.uw.leeds.peregrine;

import android.util.Log;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author saksi
 * Created: 11/28/17
 * Modified: 12/4/17
 */
public class PilotPhysicalContent {
    /**
     * An array of pilot physical items.
     */
    public static final List<PilotPhysicalItem> ITEMS = new ArrayList<>();

    /**
     * A map of pilot physical items, by ID.
     */
    public static final Map<String, PilotPhysicalItem> ITEM_MAP = new HashMap<String, PilotPhysicalItem>();

    public static final String TAG = "PilotPhysContent";

    private static ChildEventListener childEventListener;

    private static final String firebasePathString = "pilot-items-list";

    private static final DatabaseReference pilotDbReference =
            MainActivity.mDatabaseRef.child(firebasePathString);

    // TODO write JDoc
    static void initializeData(){

        // clear data to prevent duplicates
        ITEMS.clear(); // clear data from local ArrayList<>
        ITEM_MAP.clear(); // clear data from local Map

        // listen for changes to the aircraft node and its children in Firebase
        ChildEventListener aircraftEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildName) {

                // un-marshal pilot object from Firebase snapshot
                PilotPhysicalItem pilotItem = dataSnapshot.getValue(PilotPhysicalItem.class);

                addItem(pilotItem); // add item from Firebase to local data store

                int intId = Integer.decode(pilotItem.getId()); // convert String id to int

                PilotPhysicalListActivity.notifyChange(intId); // notify RecyclerView of data change

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

        childEventListener = pilotDbReference.addChildEventListener(aircraftEventListener);

    }

    /**
     * removes ChildEventListener from DatabaseReference when activity is paused
     */
    protected static void removeEvListener(){
        if(childEventListener != null){
            pilotDbReference.removeEventListener(childEventListener);
        }
    }

    /**
     * Adds the user selected aircraft to the users list of aircraft
     * @param itemToAdd the aircraft to add
     */
    protected static void addPilotItemToProfile(PilotPhysicalItem itemToAdd){

        String key = pilotDbReference.push().getKey(); // get key for new PilotPhysicalItem

        Map<String, Object> physicalItemValues = itemToAdd.toMap(); // marshal object to map

        // create empty hashmap to contain updates to Firebase
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/" + firebasePathString + "/" + key, physicalItemValues); // load Map

        MainActivity.mDatabaseRef.updateChildren(childUpdates); // send to Firebase

    }

    static void addItem(PilotPhysicalItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * Created by saksi on 11/27/17.
     *
     * One inspection task for pilot physical checks.
     */
    public static class PilotPhysicalItem {
        public String id;
        public String title;
        public String description;
        public String requirements;
        public String resources;
        public Date dueNext;
        public String imageName;

        public PilotPhysicalItem(String id,
                              String title,
                              String description,
                              String requirements,
                              String resources,
                              Date dueNext,
                              String imageName) {

            this.id = id;

            this.title = title;
            this.description = description;
            this.requirements = requirements;
            this.resources = resources;
            this.dueNext = dueNext;
            this.imageName = imageName;
        }

        // empty constructor for Firebase
        public PilotPhysicalItem(){};

        @Exclude
        public String getId(){
            return this.id;
        }

        @Exclude
        public Map<String, Object> toMap(){
            HashMap<String, Object> result = new HashMap<>();
            result.put("id", this.id);
            result.put("title", this.title);
            return result;
        }

        @Exclude
        public String toString() {
            return title + " due at " + dueNext.toString();
        }
    }
}
