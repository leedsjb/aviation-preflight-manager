package edu.uw.leeds.peregrine;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private static final String firebasePilotPathString = "pilot-items-list";

    private static final DatabaseReference pilotDbReference =
            MainActivity.mDatabaseRef.child(firebasePilotPathString);

    public static Set<String> listeningKeys = new HashSet<>();
    private static boolean addNewListener = true;

    // TODO write JDoc
    static void initializeData(){

        // clear data to prevent duplicates
        ITEMS.clear(); // clear data from local ArrayList<>
        ITEM_MAP.clear(); // clear data from local Map
        listeningKeys.clear(); //clear registered keys
        addNewListener = true; //allow registering new listeners

        // listen for changes to the pilot item list for this user
        ChildEventListener pilotEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildName) {
                String inspectionKey = dataSnapshot.getKey();
                if(addNewListener && !listeningKeys.contains(inspectionKey)) {
                    listeningKeys.add(inspectionKey);

                    //add listener for this specific pilot item
                    ChildEventListener pilotInspectionListener = new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Log.v(TAG, "received inspection key: " + dataSnapshot.getKey());
                            // un-marshal pilot object from Firebase snapshot
                            PilotPhysicalItem pilotItem = dataSnapshot.getValue(PilotPhysicalItem.class);

                            addItem(pilotItem); // add item from Firebase to local data store

                            int intId = Integer.decode(pilotItem.getId()); // convert String id to int

                            PilotPhysicalListActivity.notifyChange(intId); // notify RecyclerView of data change
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            PilotPhysicalItem pilotItem = dataSnapshot.getValue(PilotPhysicalItem.class);
                            ITEM_MAP.put(pilotItem.id, pilotItem);
                            for(int i = 0; i < ITEMS.size(); i++) {
                                if(ITEMS.get(i).id.equals(pilotItem.id)) {
                                    ITEMS.remove(i);
                                    ITEMS.add(i, pilotItem);

                                    break;
                                }

                            }
                            int intId = Integer.decode(pilotItem.getId());
                            PilotPhysicalListActivity.notifyChange(intId);
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };

                    DatabaseReference pilotInspectionReference =
                            pilotDbReference.child(inspectionKey);
                    pilotInspectionReference.addChildEventListener(pilotInspectionListener);
                }
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

        DatabaseReference userReference = MainActivity.mDatabaseRef
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("pilot-items");
        childEventListener = userReference.addChildEventListener(pilotEventListener);

    }

    /**
     * removes ChildEventListener from DatabaseReference when activity is paused
     */
    protected static void removeEvListener(){
        if(childEventListener != null){
            pilotDbReference.removeEventListener(childEventListener);
        }
        addNewListener = false;
    }

    /**
     * Adds the user selected aircraft to the users list of aircraft
     * @param itemToAdd the aircraft to add
     */
    protected static void addPilotItemToProfile(PilotPhysicalItem itemToAdd){

        //use key as unique identifier for this object
        String key = pilotDbReference.push().getKey(); // get key for new PilotPhysicalItem
        itemToAdd.id = "" + key.hashCode();
        Map<String, Object> physicalItemValues = itemToAdd.toMap(); // marshal object to map

        //write data to both users pilot item database and the overall pilot item database
        // create empty hashmap to contain updates to Firebase
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/" + firebasePilotPathString + "/" + key + "/abstraction/", physicalItemValues); // load Map

        Map<String, Object> userUpdates = new HashMap<>();

        userUpdates.put("/users/" + itemToAdd.owner + "/pilot-items/" + key, true);

        MainActivity.mDatabaseRef.updateChildren(childUpdates); // send to Firebase
        MainActivity.mDatabaseRef.updateChildren(userUpdates);

    }

    static void addItem(PilotPhysicalItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
        Log.v(TAG, new Date(item.getDate()).toString());
    }

    /**
     * Created by saksi on 11/27/17.
     *
     * One inspection task for pilot physical checks.
     */

    public static class PilotPhysicalItem implements MainActivity.ToDoItem{
        public String id;
        public String title;
        public String description;
        public String requirements;
        public String resources;
        public long dueNext;
        public String imageName;
        public String owner;

        public PilotPhysicalItem(String id,
                              String title,
                              String description,
                              String requirements,
                              String resources,
                              long dueNext,
                              String imageName,
                                 String owner) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.requirements = requirements;
            this.resources = resources;
            this.dueNext = dueNext;
            this.imageName = imageName;
            this.owner = owner;
        }

        // empty constructor for Firebase
        public PilotPhysicalItem(){};

        @Exclude
        public String getId(){
            return this.id;
        }

        @Override
        public String getTitle() {
            return this.title;
        }

        @Override
        public long getDate() {
            return this.dueNext;
        }

        public String getDescription() { return this.description; }

        public String getOwner() {
            return this.owner;
        }

        public String getRequirements() {
            return this.requirements;
        }

        public String getResources() {
            return this.resources;
        }

        @Exclude
        public Map<String, Object> toMap(){
            HashMap<String, Object> result = new HashMap<>();
            result.put("id", this.id);
            result.put("title", this.title);
            result.put("dueNext", this.dueNext);
            result.put("description", this.description);
            result.put("requirements", this.requirements);
            result.put("resources", this.resources);
            result.put("owner", this.owner);
            return result;
        }

        @Exclude
        public String toString() {
            return title + " due at " + dueNext;
        }
    }
}
