import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NotificationCenter {

    // Method to show the custom toast notification
    public static void showLotteryToast(Context context, String status) {
        
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.notification_center, null);

        // Get references to the TextViews and ImageView in the layout
        TextView toastTitle = layout.findViewById(R.id.notificationTitle);
        TextView toastMessage = layout.findViewById(R.id.notificationMessage);
        ImageView toastIcon = layout.findViewById(R.id.notificationIcon);

        // Customize the title and message based on the lottery status
        if ("winner".equals(status)) {
            toastTitle.setText("Congratulations!");
            toastMessage.setText("You've won the lottery and have been selected for the event!");
        } else {
            toastTitle.setText("Sorry!");
            toastMessage.setText("Better luck next time! You haven't been selected for the event.");
        }

        
        toastIcon.setImageResource(R.mipmap.ic_launcher);  // Replace this with our icon from the database

        // Create the Toast object and set the custom layout
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);  // Set the custom layout to the toast
        toast.show();  // Show the toast notification
    }
}
