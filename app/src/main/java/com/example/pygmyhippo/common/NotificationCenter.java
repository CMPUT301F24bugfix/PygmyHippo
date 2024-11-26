package com.example.pygmyhippo.common;

/*
 * Handles notification logic for PygmHippo app.
 *
 * Implementation:
 * A snapshot listener is installed onto the Events collection. Whenever the collection is modified,
 * the snapshot listener will be invoked and all the events will be scanned for entrants with the
 * same accountID as the accountID used in making NotificationCenter. If an entrant is found,
 * and the entrantStatus != notifiedStatus, then a notification will be posted and the corresponding
 * entrant's notifiedStatus will update to match the entrantStatus.
 *
 * TODO: Improvements
 * Several improvements can be made to improve this class:
 *  1. NotificationCenter can be made a Singleton
 *  2. Snapshot listeners query on a "entrants" collectionGroup instead of entire Events Collection.
 *  3. Notifications can be pushed/show-up when posted instead of being sent to tray.
 */

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.database.EventDB;

import java.util.ArrayList;
import java.util.Objects;



/**
 * Notification center for handling permissions and posting notifications. Used solely in
 * MainActivity. Two things must happen for NotificationCenter to work:
 *  1. init() must be called before using any other methods.
 *  2. onRequestPermissionsResult() must be called in the same overwritten function in the activity
 *     NotificationCenter is instantiated.
 *
 * See MainActivity for example of usage.
 */
public class NotificationCenter implements DBOnCompleteListener<EventEntrant> {
    private final int PERMISSION_REQUEST_CODE = 112;
    private Activity activity;
    private Context context;
    private EventDB dbHandler;
    private String accountID;
    private int nextNotificationId;

    public NotificationCenter(Activity activity, String accountID) {
        this.activity = activity;
        this.accountID = accountID;
        this.context = activity.getApplicationContext();
        this.dbHandler = new EventDB();
        this.nextNotificationId = 0;
    }

    /**
     * Initialization function. Asks for permissions and creates notification channels if necessary.
     */
    public void init() {
        getPermission();
        createNotificationChannel();
    }

    /**
     * Asks for post notification permissions.
     *
     * This is only necessary in SDK versions > 33.
     */
    public void getPermission() {
        try {
            Log.d("NotificationCenter", "Init");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            }
        }catch (Exception e){
            Log.d("NotificationCenter", "Exception when requesting notification permissions");
        }
    }

    /**
     * Creates a notification channel for posting notifications.
     *
     * This is necessary to post notifications in SDK versions >= 26
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("MainActivity", "Creating notification channel");

            CharSequence name = context.getString(R.string.notification_channel_name);
            String description = context.getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String channel_id = context.getString(R.string.notification_channel_ID);

            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Posts a notification.
     * @param title - Title of notification.
     * @param content - Body of the notification.
     * @return notification ID to be used elsewhere.
     */
    private int postNotification(String title, String content) {
        String channel_id = context.getString(R.string.notification_channel_ID);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, channel_id);
        builder.setContentTitle(title)
                .setSmallIcon(R.drawable.hippoparty)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            manager.notify(nextNotificationId, builder.build());
            return nextNotificationId++;
        } else {
            Log.d("NotificationCenter", "Do not have adequate permissions to post notification");
            return -1;
        }
    }

    /**
     * Callback function when user grants/denies permissions.
     *
     * This must be called in the same overwritten function in the activity NotificationCenter is
     * instantiated in. Check the same named function in MainActivity for an example of application.
     *
     * @param requestCode Check google docs.
     * @param permissions Check google docs.
     * @param grantResults Check google docs.
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("NotificationCenter", "Notification permissions callback");
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("NotificationCenter", "Permission allowed by user");
                    Log.d("NotificationCenter", String.format("Querying entrants for accountID %s", accountID));
//                    dbHandler.getAccountNotifications(accountID, this);
                    dbHandler.addAccountNotificationsSnapshotListener(accountID, this);
                }  else {
                    Log.d("NotificationCenter", "Permission denied by user");

                }
        }
    }

    @Override
    public void OnCompleteDB(@NonNull ArrayList<EventEntrant> docs, int queryID, int flags) {
        switch (queryID) {
            case 7: // addAccountNotificationsSnapshotListener()
                if (flags == DBOnCompleteFlags.SUCCESS.value) {
                    docs.forEach(eventEntrant -> {
                        Event event = eventEntrant.getEvent();
                        Log.d("NotificationCenter", String.format("Retrieved Entrant %s in EventID %s in snapshot listener", eventEntrant.getEntrant().getAccountID(), event.getEventID()));
                        String status = eventEntrant.getEntrant().getEntrantStatus().value;
                        postNotification(String.format("%s", status), String.format("You have been %s for a Event (%s)!", eventEntrant.getEntrant().getEntrantStatus(), event.getEventID()));

                        Entrant newEntrant = eventEntrant.getEntrant();
                        newEntrant.setNotifiedStatus(newEntrant.getEntrantStatus());
                        ArrayList<Entrant> newEntrants = event.getEntrants();
                        for (int i = 0; i < event.getEntrants().size(); i++) {
                            if (Objects.equals(newEntrants.get(i).getAccountID(), eventEntrant.getEntrant().getAccountID())) {
                                newEntrants.set(i, newEntrant);
                                break;
                            }
                        }
                        event.setEntrants(newEntrants);
                        dbHandler.updateEvent(event, (docs1, queryID1, flags1) -> {});
                    });
                }
                break;
            default:
                break;
        }
    }
}
