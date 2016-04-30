package honoursproject.garethlloyd.healthinformationdelivery;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Alarm Receiver Class
 * <p/>
 * Written by Gareth Lloyd
 * <p/>
 * This Class is the receiver for the alarm, on receive it checks whether there is a row in the database
 * for the current day, if there is not a row in the database then this means the user hasn't accessed
 * the application today so a notification reminder is sent.
 * <p/>
 * Used Tutorial at http://androidideasblog.blogspot.co.uk/2011/07/alarmmanager-and-notificationmanager.html
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHandler db = new DatabaseHandler(context);
        //checks to see if the user has accessed the app today by checking if there is a row in the database for the current day
        if (!db.rowExists(new SimpleDateFormat("dd-MM-yyyy").format(new Date()))) {
            Random r = new Random();
            int i = r.nextInt(3);//randomly generated number between 0 and 2
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            int notifyID = 1;
            android.support.v4.app.NotificationCompat.Builder mNotifyBuilder;
            //sets the title of the notification to one of 3 different titles which is randomly selected
            if (i == 0) {
                mNotifyBuilder = new NotificationCompat.Builder(context)
                        .setContentTitle("Have you had a glass of water today?"); //sets the title of the notification to the randomly selected one
            } else if (i == 1) {
                mNotifyBuilder = new NotificationCompat.Builder(context)
                        .setContentTitle("Have you eaten a vegetable today?");//sets the title of the notification to the randomly selected one
            } else if (i == 2) {
                mNotifyBuilder = new NotificationCompat.Builder(context)
                        .setContentTitle("Have you eaten a piece of fruit today?!");//sets the title of the notification to the randomly selected one
            } else {
                mNotifyBuilder = new NotificationCompat.Builder(context)
                        .setContentTitle("You haven't used the app today!");//sets the title of the notification to the randomly selected one
            }
            int numMessages = 0;
            long[] vibration = {500, 1000}; //pattern for the vibration

            //sets everything else for the notification that will be consistent
            mNotifyBuilder.setContentText("Please input your results for today! ")
                    .setNumber(++numMessages)
                    .setVibrate(vibration)//sets the notification vibrate to the set pattern
                    .setSmallIcon(R.drawable.heart_icon);//sets the icon beside the notification to be the apps logo - the heart

            Intent resultIntent = new Intent(context, MainActivity.class); //creates intent to the main activity
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mNotifyBuilder.setContentIntent(resultPendingIntent); //sets the intent when the notification is pressed - ie this will take the user to the main activity
            notificationManager.notify(notifyID, mNotifyBuilder.build()); //sends the notification to the user
        }
    }
}
