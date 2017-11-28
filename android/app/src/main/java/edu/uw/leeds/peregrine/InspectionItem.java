package edu.uw.leeds.peregrine;

import android.media.Image;

import java.util.Date;

/**
 * Created by saksi on 11/27/17.
 *
 * One inspection task for aircraft airworthiness.
 */

public class InspectionItem {
    public String title;
    public String description;
    public String requirements;
    public String resources;
    public Date dueNext;
    public Image logo;

    public InspectionItem(String title,
                          String description,
                          String requirements,
                          String resources,
                          Date dueNext,
                          Image logo) {

        this.title = title;
        this.description = description;
        this.requirements = requirements;
        this.resources = resources;
        this.dueNext = dueNext;
        this.logo = logo;
    }
}
