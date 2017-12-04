package edu.uw.leeds.peregrine;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by saksi on 11/28/17.
 */

public class PilotPhysicalContent {
    /**
     * An array of pilot physical items.
     */
    public static final List<PilotPhysicalItem> ITEMS = new ArrayList<PilotPhysicalItem>();

    public static String TAG = "PilotPhysicalContent";

    /**
     * A map of pilot physical items, by ID.
     */
    public static final Map<String, PilotPhysicalItem> ITEM_MAP = new HashMap<String, PilotPhysicalItem>();

    private static final int COUNT = 25;

    // TODO: Get actual content @Benjamin Leeds
    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(PilotPhysicalItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static PilotPhysicalItem createDummyItem(int position) {
        return new PilotPhysicalItem(
                String.valueOf(position),
                "Pilot Physical",
                "This is an inspection item",
                "You are required to inspect this thing",
                "These are you resources",
                new Date(),
                "Name of the image");
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }


    /**
     * Created by saksi on 11/27/17.
     *
     * One inspection task for pilot physical checks.
     */
    public static class PilotPhysicalItem {
        public final String id;

        public final String title;
        public final String description;
        public final String requirements;
        public final String resources;
        public final Date dueNext;
        public final String imageName;

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

        public String toString() {
            return title + " due at " + dueNext.toString();
        }
    }
}
