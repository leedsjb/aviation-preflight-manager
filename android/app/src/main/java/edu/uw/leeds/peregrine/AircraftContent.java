package edu.uw.leeds.peregrine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by saksi on 11/28/17.
 *
 * Store all the content for aircrafts
 */

public class AircraftContent {
    /**
     * An array of aircraft items.
     */
    public static final List<AircraftItem> ITEMS = new ArrayList<AircraftItem>();

    /**
     * A map of aircraft items, by ID.
     */
    public static final Map<String, AircraftItem> ITEM_MAP = new HashMap<String, AircraftItem>();

    private static final int COUNT = 25;

    // TODO: Get actual content @Benjamin Leeds
    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(AircraftItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static AircraftItem createDummyItem(int position) {
        return new AircraftItem(String.valueOf(position),
                "Cessna Citation",
                "123456",
                "2016",
                "Seven days",
                "78%");
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
     * One inspection task for aircraft airworthiness.
     */
    public static class AircraftItem {
        public final String id;

        public final String planeName;
        public final String serialNumber;
        public final String yearOfManufacture;
        public final String tachometerTime;
        public final String fuelLevel;

        public AircraftItem(String id, String planeName, String serialNumber, String yearOfManufacture, String tachometerTime, String fuelLevel) {
            this.id = id;
            this.planeName = planeName;
            this.serialNumber = serialNumber;
            this.yearOfManufacture = yearOfManufacture;
            this.tachometerTime = tachometerTime;
            this.fuelLevel = fuelLevel;
        }

        public String toString() {
            return "";
        }
    }
}
