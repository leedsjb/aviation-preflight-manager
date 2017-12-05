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

                // un-marshal aircraft object from Firebase snapshot
                InspectionItem inspectionItem = dataSnapshot.getValue(InspectionItem.class);

                addItem(inspectionItem); // add item from Firebase to local data store

                int intId = Integer.decode(inspectionItem.getId()); // convert String id to int

                InspectionItemListActivity.notifyChange(intId); // notify RecyclerView of data change

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildChanged) {
                Log.e(TAG, "child changed");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.e(TAG, "child removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildName) {
                Log.e(TAG, "child moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "database error");
            }
        };

        // save event listener so it can be removed later
        childEventListener = inspectionDbReference.addChildEventListener(inspectionEventListener);
    }

    /**
     * removes ChildEventListener from DatabaseReference when activity is paused
     */
    protected static void removeEvListener(){
        if(childEventListener != null){
            inspectionDbReference.removeEventListener(childEventListener);
        }
    }

    // called when the user adds an item that needs to be sent to firebase
    static void addInspectionToUserProfile(InspectionItem inspectionToAdd){

        String key = inspectionDbReference.push().getKey();
        Map<String, Object> inspectionValues = inspectionToAdd.toMap();
        Map<String,Object> childUpdates = new HashMap<>();
        childUpdates.put("/"+firebasePathString+"/"+key, inspectionValues);
        MainActivity.mDatabaseRef.updateChildren(childUpdates);
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
    public static class InspectionItem {
        public String id;
        public String title;
        public String description;
        public String requirements;
        public String resources;
        public Date dueNext;
        public String imageName;

        public InspectionItem(String id,
                              String title,
                              String description,
                              String requirements,
                              String resources,
                              Date dueNext,
                              // TODO need to account for TachTime ( in decimal hours)
                              String imageName) {

            this.id = id;
            this.title = title;
            this.description = description;
            this.requirements = requirements;
            this.resources = resources;
            this.dueNext = dueNext;
            this.imageName = imageName;
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

        @Exclude
        public Map<String, Object> toMap(){
            HashMap<String, Object> result = new HashMap<>();
            result.put("id", this.id);
            result.put("title", this.title);
            result.put("dueNextDateTime", this.dueNext);
            return result;
        }

        @Exclude
        public String toString() {
            return this.title + " due at " + this.dueNext.toString();
        }
    }
}

