import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;

import honoursproject.garethlloyd.healthinformationdelivery.R;

import static android.support.v7.app.NotificationCompat.*;

/**
 * Created by admin on 12/20/2015.
 */
public class NotificationManager extends AppCompatActivity {
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mNotifyBuilder;
    int numMessages;
    String currentText;

    private void showNotification(Context context) {
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    // Sets an ID for the notification, so it can be updated
    int notifyID = 1;
    mNotifyBuilder = new NotificationCompat.Builder(this)
            .setContentTitle("New Message")
            .setContentText("You've received new messages.")
            .setSmallIcon(R.drawable.heart_icon);
        numMessages=0;
            mNotifyBuilder.setContentText(currentText).setNumber(++numMessages);
    }
}

