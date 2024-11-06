package com.example.pygmyhippo.common;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.pygmyhippo.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * LotteryNotificationHelper listens for updates in the Entrants collection
 * and sends notifications based on entrant status changes.
 */
public class LotteryNotificationHelper {
    private FirebaseFirestore db;
    private Context context;

    // Constructor to initialize the helper with a context
    public LotteryNotificationHelper(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance(); // Initialize Firestore
    }

    /**
     * Sets up a listener on the "Entrants" collection to detect status changes
     */
    public void listenForEntrantUpdates() {
        // Start listening to the "Entrants" collection for changes
        db.collection("Entrants")
            .addSnapshotListener((snapshots, e) -> {
                // Check for errors
                if (e != null) {
                    e.printStackTrace(); // Log the error
                    return; // Exit if there's an error
                }

                // Check if there are changes in the snapshots
                if (snapshots != null) {
                    // Loop through each change in the snapshots
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        // Get the entrant's status and event name
                        String entrantStatus = dc.getDocument().getString("entrantStatus");
                        String eventName = dc.getDocument().getString("eventID");
                        String entrantName = dc.getDocument().getString("name");

                        // Show notification only if status is "accepted" or "rejected"
                        if ("invited".equals(entrantStatus) || "cancelled".equals(entrantStatus)) {
                            showNotification(entrantName, eventName, entrantStatus);
                        }
                    }
                }
            });
    }

    /**
     * Displays a notification to the user with entrant status information.
     * @param entrantName - Name of the entrant
     * @param eventName - Name of the event
     * @param status - Status of the entrant ("accepted" or "rejected")
     */
    private void showNotification(String entrantName, String eventName, String status) {
        // Create a message to display in the notification
        String message = "accepted".equals(status) ?
                entrantName + " has been selected for " + eventName :
                entrantName + " was not selected for " + eventName;

        // Show a toast message (only visible if the app is open)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        // Set up the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "LotteryNotificationChannel")
                .setSmallIcon(R.drawable.profilepichippo) 
                .setContentTitle("Lottery Result")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority for pop-up
                .setAutoCancel(true); // Remove notification when clicked

        // Get the NotificationManager system service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android 8.0+ (Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "LotteryNotificationChannel", // Channel ID
                    "Lottery Notifications", // Channel name
                    NotificationManager.IMPORTANCE_HIGH // Channel importance
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Display the notification
        notificationManager.notify(entrantName.hashCode(), builder.build());
    }
}
