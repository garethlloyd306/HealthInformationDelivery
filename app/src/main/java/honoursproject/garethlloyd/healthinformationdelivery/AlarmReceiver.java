package honoursproject.garethlloyd.healthinformationdelivery;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by admin on 12/28/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    int MID;

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHandler db = new DatabaseHandler(context);
       if (!db.rowExists(new SimpleDateFormat("dd-MM-yyyy").format(new Date()))) {
            Random r = new Random();
            int i = r.nextInt(3);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            // Set the icon, scrolling text and timestamp
            int notifyID = 1;
            android.support.v4.app.NotificationCompat.Builder mNotifyBuilder;
            if (i == 0) {
                mNotifyBuilder = new NotificationCompat.Builder(context)
                        .setContentTitle("Have you had a glass of water today?")
                        .setContentText("Remember to log this in the app")
                        .setSmallIcon(R.drawable.heart_icon);
                int numMessages = 0;
            } else if (i == 1) {
                mNotifyBuilder = new NotificationCompat.Builder(context)
                        .setContentTitle("Have you eaten a vegetable today?")
                        .setContentText("Remember to log this in the app")
                        .setSmallIcon(R.drawable.heart_icon);
            } else if (i == 2) {
                mNotifyBuilder = new NotificationCompat.Builder(context)
                        .setContentTitle("Have you eaten a piece of fruit today?!")
                        .setContentText("Remember to log this in the app")
                        .setSmallIcon(R.drawable.heart_icon);
            } else {
                mNotifyBuilder = new NotificationCompat.Builder(context)
                        .setContentTitle("You haven't used the app today!")
                        .setContentText("Remember to log your daily results")
                        .setSmallIcon(R.drawable.heart_icon);
            }

            int numMessages = 0;

            mNotifyBuilder.setContentText("Please input your results for today! ")
                    .setNumber(++numMessages);
            // Because the ID remains unchanged, the existing notification is
            // updated.

            Intent resultIntent = new Intent(context, MainActivity.class);

// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mNotifyBuilder.setContentIntent(resultPendingIntent);
            notificationManager.notify(
                    notifyID,
                    mNotifyBuilder.build());
        }
    }
}
