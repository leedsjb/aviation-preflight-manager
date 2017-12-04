package edu.uw.leeds.peregrine;

/**
 * Created by jessicalibman on 12/3/17.
 */

public class NotificationMessage {
    public String message;
    public String timeline;
    public String urgency;

    public NotificationMessage(String message, String timeline, String urgency) {
        this.message = message;
        this.timeline = timeline;
        this.urgency = urgency;
    }
}

