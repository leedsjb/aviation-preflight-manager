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
 * Store a list of inspection items the user has added and the details for those inspection items.
 */
public class InspectionContent {
    /**
     * An array of Inspection items.
     */
    public static final List<InspectionItem> ITEMS = new ArrayList<>();

    /**
     * A map of inspection items, by ID.
     */
    static final Map<String, InspectionItem> ITEM_MAP = new HashMap<String, InspectionItem>();
    private static final String TAG = "InspectionContent";
    private static ChildEventListener childEventListener;
    private static final String firebasePathString = "inspection-list";
    private static final DatabaseReference inspectionDbReference = MainActivity.mDatabaseRef.child(firebasePathString);

    private static Set<String> listeningKeys = new HashSet<>();

    /**
     * TODO add JDoc
     */
    static void initializeData(){

        // clear data to prevent duplicates
        ITEMS.clear(); // clear data from local ArrayList<>
        ITEM_MAP.clear(); // clear data from local Map

        // listen for changes to the aircraft node and its children in Firebase
        ChildEventListener inspectionEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildName) {
                String inspectionKey = dataSnapshot.getKey();
                if(!listeningKeys.contains(inspectionKey)) {
                    listeningKeys.add(inspectionKey);

                    ChildEventListener inspectionListener = new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            // un-marshal aircraft object from Firebase snapshot
                            InspectionItem inspectionItem = dataSnapshot.getValue(InspectionItem.class);

                            addItem(inspectionItem); // add item from Firebase to local data store

                            int intId = Integer.decode(inspectionItem.getId()); // convert String id to int

                            InspectionItemListActivity.notifyChange(intId); // notify RecyclerView of data change
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

                    DatabaseReference inspectionReference =
                            inspectionDbReference.child(inspectionKey);
                    inspectionReference.addChildEventListener(inspectionListener);
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
                .child("inspections");

        // save event listener so it can be removed later
        childEventListener = userReference.addChildEventListener(inspectionEventListener);
    }

    /**
     * removes ChildEventListener from DatabaseReference when activity is paused
     */
    protected static void removeEvListener(){
        if(childEventListener != null){
            inspectionDbReference.removeEventListener(childEventListener);
        }
        listeningKeys.clear();
    }

    // called when the user adds an item that needs to be sent to firebase
    static void addInspectionToUserProfile(InspectionItem inspectionToAdd){

        String key = inspectionDbReference.push().getKey();
        inspectionToAdd.id = "" + key.hashCode();
        Map<String, Object> inspectionValues = inspectionToAdd.toMap();

        Map<String,Object> childUpdates = new HashMap<>();
        childUpdates.put("/"+firebasePathString+"/"+key + "/abstraction/", inspectionValues);

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("/users/" + inspectionToAdd.owner + "/inspections/" + key, true);

        MainActivity.mDatabaseRef.updateChildren(childUpdates);
        MainActivity.mDatabaseRef.updateChildren(userUpdates);
    }

    // called when a new item is added to firebase
    static void addItem(InspectionItem item) {
        Log.e(TAG, "add item called");
        Log.e(TAG, "the item.id is: " + item.id);
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * Created by saksi on 11/27/17.
     *
     * One inspection task for aircraft airworthiness.
     */
    public static class InspectionItem implements MainActivity.ToDoItem{
        public String id;
        public String title;
        public String description;
        public String requirements;
        public String resources;
        public Date dueNext;
        public String imageName;
        public String owner;

        public InspectionItem(String id,
                              String title,
                              String description,
                              String requirements,
                              String resources,
                              Date dueNext,
                              // TODO need to account for TachTime ( in decimal hours)
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

        public InspectionItem(){};

        @Exclude
        public String getId(){
            return this.id;
        }

        @Exclude
        public String getTitle(){
            return this.title;
        }

        @Override
        public Date getDate() {
            return this.dueNext;
        }

        public String getOwner() {
            return this.owner;
        }

        @Exclude
        public Map<String, Object> toMap(){
            HashMap<String, Object> result = new HashMap<>();
            result.put("id", this.id);
            result.put("title", this.title);
            result.put("dueNextDateTime", this.dueNext);
            result.put("owner", this.owner);
            return result;
        }

        @Exclude
        public String toString() {
            return this.title + " due at " + this.dueNext.toString();
        }
    }
}

