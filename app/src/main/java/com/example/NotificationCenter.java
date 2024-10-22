import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.widget.RemoteViews;

public class NotificationCenter {

    private static final String CHANNEL_ID = "lottery_channel"; // this will be modified using the Database
    private static final String CHANNEL_NAME = "Lottery Notifications";  // same
    
    // method to show the notification
    public static void showLotteryNotification(Context context, String status) {
        // the xml file for the notification
        RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.notification_center);

        // Set title and message based on whether the user is a winner or not
        if ("winner".equals(status)) {
            notificationLayout.setTextViewText(R.id.notificationTitle, "Congratulations!");
            notificationLayout.setTextViewText(R.id.notificationMessage, "You've won the lottery and have been selected for the event!");
        } else {
            notificationLayout.setTextViewText(R.id.notificationTitle, "Sorry!");
            notificationLayout.setTextViewText(R.id.notificationMessage, "Better luck next time! You haven't been selected for the event.");
        }

        // This feature will allow the user to open the app when the notification pops up as a upper banner, just like a normal notifiaction, reference: Android Developers Website 
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // building the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)  // We still have to decide what icon to go with
                .setCustomContentView(notificationLayout)  // Use the custom layout
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)  // High priority for heads-up notification
                .setDefaults(NotificationCompat.DEFAULT_ALL)  // This feature will give the user a sound and vibration on the device for the notification 
                .setContentIntent(pendingIntent);  // opens the app when notification is tapped

        // get the NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // For Android, we need to create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);  // High importance for heads-up
            channel.setDescription("Lottery event result notifications");
            notificationManager.createNotificationChannel(channel);
        }

        // Show the notification
        notificationManager.notify(0, notificationBuilder.build());
    }
}
