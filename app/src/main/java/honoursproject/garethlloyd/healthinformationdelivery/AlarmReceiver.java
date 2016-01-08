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

import java.util.Calendar;
import java.util.Date;

/**
 * Created by admin on 12/28/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    int MID;
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "recieved", Toast.LENGTH_LONG).show();
       NotificationManager notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        // Set the icon, scrolling text and timestamp

        int notifyID = 1;
       android.support.v4.app.NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("You have not used your health app today!")
                .setContentText("You've received new messages.")
                .setSmallIcon(R.drawable.heart_icon);
        int numMessages = 0;

        mNotifyBuilder.setContentText("Please input your results for today! ")
                .setNumber(++numMessages);
        // Because the ID remains unchanged, the existing notification is
        // updated.
        notificationManager.notify(
                notifyID,
                mNotifyBuilder.build());
    }
}
