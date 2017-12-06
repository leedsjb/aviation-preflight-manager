package edu.uw.leeds.peregrine;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import android.support.v4.app.NotificationCompat;
/**
 * Created by jessicalibman on 12/3/17.
 */

public class NotificationMessage extends IntentService {
    private static final String NOTIF_CHANNEL_ID = "edu.uw.leeds.peregrine.channel";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NotificationMessage(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
//        notifyUser(intent.);
    }

    public void notifyUser(InspectionContent.InspectionItem ii) {
        // Specify where tapping this notification will navigate user
        Intent notifyIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notifyIntent, 0);

        // Construct the Big View
        // View Message
        Intent notifyViewIntent = new Intent(getApplicationContext(), MainActivity.class);
//        notifyViewIntent.setAction(CommonConstants.ACTION_DISMISS); // to dismiss on press
        PendingIntent openPendIntView = PendingIntent.getActivity(getApplicationContext(), 0, notifyViewIntent, 0);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //Oreo
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            Notification.Builder builder = new Notification.Builder(getApplicationContext(), NOTIF_CHANNEL_ID)
                    .setContentTitle(ii.title)
                    .setContentText((CharSequence) (ii.dueNext + ""))
                    .setSmallIcon(R.drawable.ic_menu_camera)//TODO change
                    .setContentIntent(pendingIntent) // set destination when notification is tapped
                    .addAction(R.drawable.ic_menu_camera, "View", openPendIntView);

            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, "General Notification", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Test");
            channel.enableLights(true);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(1, builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(ii.title)
                    .setContentText((CharSequence) (ii.dueNext + ""))
                    .setSmallIcon(R.drawable.ic_menu_camera) //TODO change
                    .setContentIntent(pendingIntent)
                    .addAction(0, "View", openPendIntView);

            // Post Notification
            notificationManager.notify(1, builder.build()); // Post notification

        }
    }
}

