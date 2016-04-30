package honoursproject.garethlloyd.healthinformationdelivery;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Main Activity class
 * Written by Gareth Lloyd
 * This is the home screen of the application - it initialises the view pager
 * connects with the google fit account, reads results for steps and activity time
 * Stores results inputted by the user in the database, and pop up dialog when a new reward has been received
 * <p/>
 * Used Tutorials from  https://www.developers.google.com/fit/?hl=en
 * http://www.androidbegin.com/tutorial/android-jake-wharton-viewpager-indicator-tutorial/
 * http://www.mkyong.com/android/android-custom-dialog-example/
 */

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    TextView text;
    ViewPager viewPager;
    PagerAdapter adapter;
    DatabaseHandler db;
    int[] icons;
    int[] values;
    String[] titles;
    int[] targets;
    int[] images;
    String[] texts;
    TabPageIndicator mIndicator;
    UnderlinePageIndicator mIndicator1;
    String lastActive;
    private GoogleApiClient mClient = null;
    private boolean authInProgress = false;
    private String TAG = "Log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.text);
        values = new int[]{0, 0, 0, 0};
        icons = new int[]{R.drawable.walk_small, R.drawable.water_small, R.drawable.fruit_small, R.drawable.clock_small};
        images = new int[]{R.drawable.walk_large, R.drawable.water_large, R.drawable.fruit_and_carrot, R.drawable.clock_large};
        titles = new String[]{"Steps", "Water", " Food", " Activity"};
        targets = new int[]{10000, 8, 5, 30};
        texts = new String[]{"Steps", "Glasses Of Water", "Fruit and Vegetables", "Activity Time"};
        // Initialising the view pager, setting up the adaptor and setting up the indicators and setting the adapters for these
        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(MainActivity.this, values, icons, targets, titles, images, texts);
        viewPager.setAdapter(adapter);
        mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);
        mIndicator1 = (UnderlinePageIndicator) findViewById(R.id.indicator1);
        mIndicator1.setFades(false);
        mIndicator1.setViewPager(viewPager);
        db = new DatabaseHandler(this); //initialising the database handler class
        DailyStore data = new DailyStore();
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        boolean exists = db.rowExists(date); //checking if there is a row in the database for today , if not create an empty row with today's date as the key
        if (!exists) {
            data.setDate(date);
            data.setActivityTime(0);
            data.setFruitAndVeg(0);
            data.setSteps(0);
            data.setWater(0);
            db.addHealthData(data);
        }
        db.readDailyResults(date); //read the daily results stored in the database for the current date
        lastActive = db.updateLastActive(date); //read the last active date and store this in a variable
        buildFitnessClient(); //connect to the google fit account
        readDataFromDB(); //connecting to google fit will update the daily results - re read the daily results for the current date
        /*
        Alarm Manager - initialling and setting the intent for this to be the alarm receiver class to send notification
        Alarm is set to repeat at 3pm daily
         */
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class); //set intent to the alarm receiver when alarm is set off
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 15); //set time of alarm to 3pm
        calendar.set(Calendar.MINUTE, 00);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent); //repeat the alarm daily
        db.addTrophies(); //add the trophies table to the db (this checks if it exists first before adding so there
        //are not multiple rows.
    }

    /*
     Method taken and altered to fit needs from https://developers.google.com/fit/android/get-started#step_5_connect_to_the_fitness_service
     */
    public void buildFitnessClient() {
        mClient = new GoogleApiClient.Builder(MainActivity.this) //builds the google API client
                .addApi(Fitness.HISTORY_API) //requests access to the history api
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE)) //requests the ability to read and write fitness data
                .addConnectionCallbacks( //connection callbacks set up - when the api returns a connection
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) { //when app gets connected to google fit
                                Log.i(TAG, "Connected!!!");
                                Date today = new Date(); //sets a variable date - to today's date
                                Calendar active = Calendar.getInstance(); //creates new date for the last active
                                try {
                                    active.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(lastActive)); //parses the last active and sets it to the active variable
                                } catch (Exception e) {
                                    Log.e("Error", e.toString());
                                }
                                //the code below updates the results from google fit from the last active date to the current date to ensure accurate results
                                while (today.compareTo(active.getTime()) >= 0) { //while the active date is not after today's date
                                    GetSteps getSteps = new GetSteps();  //get the results for the step count for that date
                                    getSteps.execute(new SimpleDateFormat("dd-MM-yyyy").format(active.getTime()));
                                    GetActivityTime activityTime = new GetActivityTime(); //gets the results for activity time for that date
                                    activityTime.execute(new SimpleDateFormat("dd-MM-yyyy").format(active.getTime()));
                                    checkTrophies(new SimpleDateFormat("dd-MM-yyyy").format(active.getTime()));
                                    active.add(Calendar.DAY_OF_YEAR, +1); //adds one day to the last active date
                                }
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            private ConnectionResult connectionResult;

                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.i(TAG, "Connection failed. Cause: " + result.toString());
                                if (!result.hasResolution()) {
                                    // Show the localized error dialog
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            MainActivity.this, 0).show();
                                    Toast.makeText(MainActivity.this, "Connection Failed, make sure you have internet connection and try again!", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                // authorization dialog is displayed to the user.
                                if (!authInProgress) {
                                    try {
                                        Log.i(TAG, "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(MainActivity.this,
                                                REQUEST_OAUTH);
                                    } catch (IntentSender.SendIntentException e) {
                                        Log.e(TAG,
                                                "Exception while starting resolution activity", e);
                                    }
                                }
                            }
                        }
                )
                .build();
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Connect to the Fitness API
        Log.i(TAG, "Connecting...");
        mClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mClient.isConnected()) {
            mClient.disconnect();
        }
    }

    public void readDataFromDB() { //method to read data from the db and update the values on the view pager correctly
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        DailyStore data = db.readDailyResults(date); //read the results from the database and set results to the daily store
        //set the values array to updated values
        values[0] = data.getSteps();
        values[1] = data.getWater();
        values[2] = data.getFruitAndVeg();
        values[3] = data.getActivityTime();
        adapter.notifyDataSetChanged(); //notify view pager adapter the values have changed so they can be updated
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mClient.isConnecting() && !mClient.isConnected()) {
                    mClient.connect();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    /*
        This method is called when the user presses either the plus or minus button on the water
        or the fruit and veg page this method is called. This updates the value depending on the button pressed
        This is determined using tags. It also associates a positive or negative beep depending on the button pressed.
        If new award is achieved this is shown by a pop up dialog which is initialised and displayed here.
     */
    public void updateData(View view) {
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        // code to build the alert dialog and change its attributes
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(view1);
        TextView title = new TextView(this);
        title.setText("Well Done!");
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        title.setPadding(10, 10, 10, 10);
        builder.setCustomTitle(title);
        TextView text1 = (TextView) view1.findViewById(R.id.textAward);
        ImageView imageAward = (ImageView) view1.findViewById(R.id.image);
        Button close = (Button) view1.findViewById(R.id.close);
        final AlertDialog dialog = builder.create(); //creates the dialog with the attributes changed above
        final MediaPlayer mpPositive = MediaPlayer.create(this, R.raw.positive); //creates the positive beep noise
        final MediaPlayer mpNegative = MediaPlayer.create(this, R.raw.negative); //creates the negative beep noise
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //when the close button is click dismiss the dialog
                dialog.dismiss();
            }
        });
        if (view.getTag() != null) { //checks if the tag is null or not - the buttons should always have a tag
            int result;
            if (view.getTag().toString() == "plusWater" || view.getTag().toString() == "minusWater") { //checks if its the water page the button was pressed on
                if (view.getTag().toString() == "plusWater") { //if tag is plus water
                    result = values[1] + 1; //add one to the value stored currently - save this in local variable
                    mpPositive.start(); //sound the positive beep sound
                    Toast.makeText(this, "Well Done!", Toast.LENGTH_SHORT).show(); //show well done toast message
                } else { //if its not plus water - it must be negative
                    if ((values[1] - 1) > 0) { //if the result will be greater than 0
                        result = values[1] - 1; //take one away from the value stored currently and save this in a local variable
                        mpNegative.start(); //play the negative beep sound
                    } else { //if the result ill be less than 1
                        result = 0; //set it to zero
                        if ((values[1] - 1) < 0) { //if result would have been less than zero then provide error message
                            Toast.makeText(this, "Water can't be a minus value", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                db.updateWater(result, date); //update result in the database
                readDataFromDB(); //update the results show
                adapter.notifyDataSetChanged(); //notify the view pager adapter of the changed values
                if (view.getTag().toString() != "minusWater") { //if has achieved a new reward and not going back to a previously earned reward
                    //check if they have achieved a new award - if so display the correct star and text and show the dialog
                    if (result == 4) {
                        text1.setText("Bronze Award!");
                        imageAward.setImageResource(R.drawable.bronze_star);
                        dialog.show();
                    } else if (result == 6) {
                        text1.setText("Silver Award!");
                        imageAward.setImageResource(R.drawable.silver_star);
                        dialog.show();
                    } else if (result == 8) {
                        text1.setText("Gold Award!");
                        imageAward.setImageResource(R.drawable.gold_star);
                        dialog.show();
                    }
                }
            } else if (view.getTag().toString() == "minusFruit" || view.getTag().toString() == "plusFruit") { //checks if the button was on the fruit page
                if (view.getTag().toString() == "plusFruit") { //checks if it was the plus fruit button pressed
                    result = values[2] + 1; //adds one to the current fruit value
                    mpPositive.start(); //plays positive beep sound
                    Toast.makeText(this, "Well Done!", Toast.LENGTH_SHORT).show(); //shows well done message
                } else { //if not it had to be the minus fruit button
                    if ((values[2] - 1) > 0) { // checks if taking one away from the result will be greater than zero
                        result = values[2] - 1; //takes one away from the current fruit value
                        mpNegative.start(); //plays negative beep
                    } else { //if the result will be equal to or less than zero
                        result = 0;//sets the result to zero
                        if ((values[1] - 1) < 0) { //if result would have been less than zero then provide error message
                            Toast.makeText(this, "Fruit and Veg can't be a minus value", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                db.updateFruit(result, date); //updates the fruit value in the database
                readDataFromDB(); //reads the updated results from the database
                adapter.notifyDataSetChanged(); //notify adapters that the values have been updated
                if (view.getTag().toString() != "minusFruit") { //checks that the award will not have already be previously earned before
                    //check if they have achieved a new award - if so display the correct star and text and show the dialog
                    if (result == 2) {
                        text1.setText("Bronze Award!");
                        imageAward.setImageResource(R.drawable.bronze_star);
                        dialog.show();
                    } else if (result == 4) { //if silver award has been achieved - show the dialog for silver award
                        text1.setText("Silver Award!");
                        imageAward.setImageResource(R.drawable.silver_star);
                        dialog.show();
                    } else if (result == 5) {
                        text1.setText("Gold Award!");
                        imageAward.setImageResource(R.drawable.gold_star);
                        dialog.show();
                    }
                }
            }
        }
    }

    /*
        Code below has been altered to fit needs and taken from https://developers.google.com/fit/android/history#read_detailed_and_aggregate_data
     */

    private void dumpDataSet(DataSet dataSet) { //the data from the activity request is passed in here
        long totalDuration = 0; //variable to store total duration
        Date date = new Date();
        for (DataPoint dp : dataSet.getDataPoints()) { //for loop through all data points
            int activity = dp.getValue(Field.FIELD_ACTIVITY).asInt();//gets the activity code and stores this as a variable
            date.setTime(dp.getStartTime(TimeUnit.MILLISECONDS));
            Date endDate = new Date();
            endDate.setTime(dp.getEndTime(TimeUnit.MILLISECONDS));
            if (activity != 3 && activity != 0) { //if the activity is not active then exclude - ie 3 = Still(not moving) 0 = In Vehicle
                long duration = TimeUnit.MILLISECONDS.toMinutes(dp.getValue(Field.FIELD_DURATION).asInt()); //converted the duration from milliseconds to minutes
                totalDuration += duration; //this duration is added to the total
                date.setTime(dp.getStartTime(TimeUnit.MILLISECONDS)); //sets the date to the time of the start of the datapoint - this will always be in one day

                final int finalDuration = (int) totalDuration;
                final Date finalDate = date;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String date = new SimpleDateFormat("dd-MM-yyyy").format(finalDate);
                        if (finalDuration >= 0) {
                            db.updateActivity(finalDuration, date);  //updates the duration of activity for today's date
                        }
                        readDataFromDB(); //reads the updated data from the database
                        adapter.notifyDataSetChanged(); //notify view pagers adapter of the updated data
                    }
                });
            }
        }
    }

    private void dumpStepDataSet(DataSet dataSet) {
        int steps = 0; //variable to store the step count
        Date date = new Date();
        for (DataPoint dp : dataSet.getDataPoints()) { //for loop through all datapoints
            steps = dp.getValue(Field.FIELD_STEPS).asInt(); //gets value of steps from the datapoint
            date.setTime(dp.getStartTime(TimeUnit.MILLISECONDS)); //sets the date to the start date of the data point
            final int finalSteps = steps;
            final Date finalDate = date;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String date = new SimpleDateFormat("dd-MM-yyyy").format(finalDate);
                    if (finalSteps >= 0) {
                        db.updateSteps(finalSteps, date); //updates the data in the database for steps for date passed in
                    }
                    readDataFromDB(); //reads updated data from the db
                    adapter.notifyDataSetChanged(); //notify adapter that the values have been updated
                }
            });
        }

    }

    /*
        Method to check to see if the user has earned any trophies
        This is checked by for looping through the days to check if they have matched the requirements for the trophies
        Parameter: The date to check the trophies from
     */

    public void checkTrophies(String date) {
        TrophyStore currentTrophies = db.readTrophies(); //read the current trophies earned by the users
        Log.d("Trophies", "Streak:   " + db.getStreak());

        if (currentTrophies.getUsage().equals("N")) { //if they currently haven't earned a trophy for usage
            if (db.getStreak() >= 2) { //check if their streak is greater than or equal to 2
                db.updateTrophies("Usage", "B"); //if it is then update usage trophy to bronze
            }
        } else if (currentTrophies.getUsage().equals("B")) { //if they have a bronze award for usage
            if (db.getStreak() >= 7) { //check if their streak is greater than or equal to 7 ie a week in a row
                db.updateTrophies("Usage", "S"); //if it is then update usage trophy to silver
            }
        } else if (currentTrophies.getUsage().equals("S")) { //if they have a silver award for usage
            if (db.getStreak() >= 14) { //check if their streak is greater than or equal to 14 ie two weeks in a row
                db.updateTrophies("Usage", "G"); //if it is update usage trophy to gold
            }
        }

        if (currentTrophies.getEverything().equals("N")) { //if they have no trophy for everything area yet
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(date)); //set the date to the date being checked for trophies
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            boolean result = true; //if all requirements are met this will not be changed
            for (int i = 0; i < 7; i++) { //for loop for 7 ( 7 days ie for one week)
                String updatedDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
                DailyStore data = db.readDailyResults(updatedDate); //read the results for that date
                //check if there is data in the row, if there is check if the results are over the bronze targets
                //if not break the for loop and set result to be false;
                if (data == null) {
                    result = false;
                    break;
                } else if (data.getSteps() < 5000) {
                    result = false;
                    break;
                } else if (data.getActivityTime() < 10) {
                    result = false;
                    break;
                } else if (data.getFruitAndVeg() < 2) {
                    result = false;
                    break;
                } else if (data.getWater() < 4) {
                    result = false;
                    break;
                }
                cal.add(Calendar.DAY_OF_YEAR, -1); //take one day away from the current date - to check the previous days results - for loop means this is repeated 7 times
            }
            if (result) {
                db.updateTrophies("Everything", "B"); //if this meets the requirements for a bronze trophy - update the everything trophy to bronze
            }
        } else if (currentTrophies.getEverything().equals("B")) { //if the user currently has a bronze trophy for everything
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(date)); //set the date to the date being checked
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            boolean result = true; //if all requirements are met this will not be changed
            for (int i = 0; i < 7; i++) { //for loop for the 7 days being checked
                String updatedDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
                DailyStore data = db.readDailyResults(updatedDate); //reads the data for the day for that date from the db
                //check if there is data in the row, if there is check if the results are over the silver targets
                //if not break the for loop and set result to be false;
                if (data == null) {
                    result = false;
                    break;
                } else if (data.getSteps() < 7500) {
                    result = false;
                    break;
                } else if (data.getActivityTime() < 20) {
                    result = false;
                    break;
                } else if (data.getFruitAndVeg() < 4) {
                    result = false;
                    break;
                } else if (data.getWater() < 6) {
                    result = false;
                    break;
                }
                cal.add(Calendar.DAY_OF_YEAR, -1);  //take one day away from the current date - to check the previous days results - for loop means this is repeated 7 times
            }
            if (result) {
                db.updateTrophies("Everything", "S");//if this meets the requirements for a bronze trophy - update the everything trophy to silver
            }
        } else if (currentTrophies.getEverything().equals("S")) { //checks if the user currently has a silver trophy for everything
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(date)); //sets the date to the date being checkked
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            boolean result = true; //if all requirements are met this will not be changed
            for (int i = 0; i < 7; i++) { // for loop through the seven days being checked
                String updatedDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
                DailyStore data = db.readDailyResults(updatedDate); //reads the results from the db for that date
                //check if there is data in the row, if there is check if the results are over the gold targets
                //if not break the for loop and set result to be false;
                if (data == null) {
                    result = false;
                    break;
                } else if (data.getSteps() < 10000) {
                    result = false;
                    break;
                } else if (data.getActivityTime() < 30) {
                    result = false;
                    break;
                } else if (data.getFruitAndVeg() < 5) {
                    result = false;
                    break;
                } else if (data.getWater() < 8) {
                    result = false;
                    break;
                }
                cal.add(Calendar.DAY_OF_YEAR, -1); //take one day away from the current date - to check the  previous days results - for loop means this is repeated 7 times
            }
            if (result) {
                db.updateTrophies("Everything", "G"); //if the result is still true - all requirements are met - update everything trophy to gold award
            }
        }

        if (currentTrophies.getSteps().equals("N")) { //checks if the user has no current rewarded trophy for steps
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(date)); //sets the date to the date being check from for trophies
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            boolean result = true; // if all requirements are met then this will not change
            for (int i = 0; i < 7; i++) { //loops through 7 times (ie for 7 days - one week)
                String updatedDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
                DailyStore data = db.readDailyResults(updatedDate); //reads the results for the date being checked
                if (data == null) {
                    result = false;
                    break;
                } else if (data.getSteps() < 5000) { //checks if the results are less than the bronze target (5000)
                    result = false; //if it is then returns false and breaks from for loop
                    break;
                }
                cal.add(Calendar.DAY_OF_YEAR, -1);//take one day away from the current date - to check the  previous days results - for loop means this is repeated 7 times
            }
            if (result) {
                db.updateTrophies("Steps", "B"); //if result is still true - requirements have been met so update step trophy to bronze
            }
        } else if (currentTrophies.getSteps().equals("B")) { //if the current trophy for steps is bronze
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(date)); //set date to the date being checked for trophies from
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            boolean result = true; //if all requirements are met this will not change
            for (int i = 0; i < 7; i++) { //loops through 7 times - (ie for 7 days - one week)
                String updatedDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
                DailyStore data = db.readDailyResults(updatedDate); //reads the results for the date being checked
                if (data.getSteps() < 7500) { //if the step count for the date is less than the silver target
                    result = false; //set result to be false and break from the for loop
                    break;
                }
                cal.add(Calendar.DAY_OF_YEAR, -1); ///take one day away from the current date - to check the previous days results - for loop means this is repeated 7 times
            }
            if (result) {
                db.updateTrophies("Steps", "S"); //if result is true then requirements have been met so update step trophy to be silver
            }
        } else if (currentTrophies.getSteps().equals("S")){ //checks whether the user current trophy for steps is silver
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(date)); //sets the date to be the date being checked from
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            boolean result = true; //if all requirements are met this shouldnt change - false means requirements have not been met
            for (int i = 0; i < 7; i++) { //for loop through 7 times - ie 7 days
                String updatedDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
                DailyStore data = db.readDailyResults(updatedDate); //reads the results for the current date being checked
                if (data == null) {
                    result = false; //if there is no data then set result to false and break from for loop
                    break;
                } else if (data.getSteps() < 10000) { //if the step count for the date is less than the gold tareet
                    result = false; //set result to be false and break from the for loop
                    break;
                }
                cal.add(Calendar.DAY_OF_YEAR, -1);//take one day away from the current date - to check the  previous days results - for loop means this is repeated 7 times
            }
            if (result) {
                db.updateTrophies("Steps", "G"); //if result is true then requirements have been met - update the step trophy to gold
            }
        }

        if (currentTrophies.getActive().equals("N")) { //if the user has not earned a trophy for active time
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(date)); //set the date to the date being check
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            boolean result = true; //if the requirements are met this will not change
            for (int i = 0; i < 7; i++) { //for loop 7 times - ie 7 days
                String updatedDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
                DailyStore data = db.readDailyResults(updatedDate); //reads the data for the current date
                if (data == null) { //if the data is null, the row is empty , set result to false and break from the for loop
                    result = false;
                    break;
                } else if (data.getActivityTime() < 10) { //if the result is less than the bronze target, set result to false and break from the for loop
                    result = false;
                    break;
                }
                cal.add(Calendar.DAY_OF_YEAR, -1);//take one day away from the current date - to check the previous days results - for loop means this is repeated 7 tim
            }
            if (result) {
                db.updateTrophies("Active", "B"); //if result is true, requirements have been met - update the active trophy to bronze
            }
        } else if (currentTrophies.getActive().equals("B")) { //if the user current trophy for active is bronze
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(date)); //set the date to the date being checked
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            boolean result = true; //if requirements are met this will not be changed
            for (int i = 0; i < 7; i++) { // for loop 7 times - ie 7 days
                String updatedDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
                DailyStore data = db.readDailyResults(updatedDate); //reads the data from the db for the current date
                if (data == null) { //if the data is null, the row is empty , set result to be false and break from the for loop
                    result = false;
                    break;
                } else if (data.getActivityTime() < 20) { //if the active time is less than the silver target, set result to be false and break from for loop
                    result = false;
                    break;
                }
                cal.add(Calendar.DAY_OF_YEAR, -1); //take one day away from the current date - to check the previous days results - for loop means this is repeated 7 times
            }
            if (result) {
                db.updateTrophies("Active", "S"); //if result is true - requirements have been met - update active trophy to silver
            }
        } else if (currentTrophies.getActive().equals("S")) { // if the users current trophy for active is silver
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(date)); //set the current date to be the one being checked
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            boolean result = true; // if requirements are met this should not be changed
            for (int i = 0; i < 7; i++) { //for loop 7 times - ie 7 days
                String updatedDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
                DailyStore data = db.readDailyResults(updatedDate); //read the results from the database for the current date
                if (data == null) { //if the data is null then no data exists so requiremnets have not been met - set result to false and break from for loop
                    result = false;
                    break;
                } else if (data.getActivityTime() < 30) { //if the active time is less than the gold target - set results to false and break from the for loop
                    result = false;
                    break;
                }
                cal.add(Calendar.DAY_OF_YEAR, -1);//take one day away from the current date - to check the  previous days results - for loop means this is repeated 7 times
            }
            if (result) {
                db.updateTrophies("Active", "G"); //if result is true - requirements have been met - update the active trophy to gold.
            }
        }

        if (currentTrophies.getFruit().equals("N")) { //if the user has no current trophy for fruit
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(date)); //set the date to the date being checked
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            boolean result = true; //if requirements are met this should not change
            for (int i = 0; i < 7; i++) { //for loop 7 times -ie 7 days
                String updatedDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
                DailyStore data = db.readDailyResults(updatedDate);
               //if data is null or the result is less than the bronze target  set result to false and break from the for loop
                if (data == null) {
                    result = false;
                    break;
                } else if (data.getFruitAndVeg() < 2) {
                    result = false;
                    break;
                }
                cal.add(Calendar.DAY_OF_YEAR, -1); //take one day away from the current date - to check the  previous days results - for loop means this is repeated 7 times
            }
            if (result) {
                db.updateTrophies("Fruit", "B"); //if results is true - then update  the fruit trophy to bronze
            }
        } else if (currentTrophies.getFruit().equals("B")) { //if the current trophy achieved is bronze for fruit
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(date)); //set the date to the current date being checked
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            boolean result = true; //if requiements are met this should not change
            for (int i = 0; i < 7; i++) { //for loop 7 times - ie 7 days
                String updatedDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
                DailyStore data = db.readDailyResults(updatedDate); //read the results from the db for the current date
                //if data is null or the result is less than the silver target  set result to false and break from the for loop
                if (data == null) {
                    result = false;
                    break;
                } else if (data.getFruitAndVeg() < 4) {
                    result = false;
                    break;
                }
                cal.add(Calendar.DAY_OF_YEAR, -1); //take one day away from the current date - to check the  previous days results - for loop means this is repeated 7 times
            }
            if (result) {
                db.updateTrophies("Fruit", "S"); //if result is true - update the fruit trophy to silver
            }
        } else if (currentTrophies.getFruit().equals("S")) { //if the users current active trophy is silver
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(date)); //set the date to the date being checked
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            boolean result = true; //if requirements are met this should not be changed
            for (int i = 0; i < 7; i++) { //for loop for 7 times - ie 7 times
                String updatedDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
                DailyStore data = db.readDailyResults(updatedDate); //read the results for the current date
                //if data is null or the result is less than the gold target  set result to false and break from the for loop
                if (data == null) {
                    result = false;
                    break;
                } else if (data.getFruitAndVeg() < 5) {
                    result = false;
                    break;
                }
                cal.add(Calendar.DAY_OF_YEAR, -1); //take one day away from the current date - to check the  previous days results - for loop means this is repeated 7 times
            }
            if (result) {
                db.updateTrophies("Fruit", "G"); //if result is true update the fruit trophy to gold
            }
        }

        if (currentTrophies.getWater().equals("N")) { //if the user has no current trophy for water
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(date)); //set the date to the date being checked
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            boolean result = true; //if requirements are met this should not change
            for (int i = 0; i < 7; i++) { //for loop for 7 times - ie 7 days
                String updatedDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
                Log.d("Dates", "Water:  " + updatedDate);
                DailyStore data = db.readDailyResults(updatedDate); //read the results for the current day
                //if data is null or the result is less than the bronze target  set result to false and break from for loop
                if (data == null) {
                    result = false;
                    break;
                } else if (data.getWater() < 4) {
                    result = false;
                    break;
                }
                cal.add(Calendar.DAY_OF_YEAR, -1); //take one day away from the current date - to check the  previous days results - for loop means this is repeated 7 times
            }
            if (result) {
                db.updateTrophies("Water", "B"); //if result is true update the water trophy to bronze
            }
        } else if (currentTrophies.getWater().equals("B")) { //if the current trophy for water is bronze
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(date)); //set the date to be the date being checked
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            boolean result = true; //if requirements are met this should not change
            for (int i = 0; i < 7; i++) { //for loop 7 times - ie 7 days
                String updatedDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
                DailyStore data = db.readDailyResults(updatedDate); //read the data from the db for the current date
                //if data is null or the result is less than the silver target  set result to false and break from the for loop
                if (data == null) {
                    result = false;
                    break;
                } else if (data.getWater() < 6) {
                    result = false;
                    break;
                }
                cal.add(Calendar.DAY_OF_YEAR, -1); //take one day away from the current date - to check the  previous days results - for loop means this is repeated 7 times
            }
            if (result) {
                db.updateTrophies("Water", "S"); //if result is true - update the water trophy to silver
            }
        } else if (currentTrophies.getWater().equals("S")) { //if the users current trophy for water is silver
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(date)); //set the date to the date being checked
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            boolean result = true; //if requirements are met this should not change
            for (int i = 0; i < 7; i++) { //for loop 7 times - ie 7 days
                String updatedDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
                DailyStore data = db.readDailyResults(updatedDate); //read the results for the current date
                //if data is null or the result is less than the gold target  set result to false and break from the for loop

                if (data == null) {
                    result = false;
                    break;
                } else if (data.getWater() < 8) {
                    result = false;
                    break;
                }
                cal.add(Calendar.DAY_OF_YEAR, -1); //take one day away from the current date - to check the  previous days results - for loop means this is repeated 7 times
            }
            if (result) {
                db.updateTrophies("Water", "G"); //if the result is true then update the water trophy to gold
            }
        }
    }

    /*
        Code below has been altered to fit needs and taken from https://developers.google.com/fit/android/history#read_detailed_and_aggregate_data
     */

    private class GetSteps extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... args) {
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(args[0].toString())); //read the first argument passed in and set this to the date
            } catch (Exception e) {

            }
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            long endTime = cal.getTimeInMillis();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            long startTime = cal.getTimeInMillis();
            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                    .bucketByTime(1, TimeUnit.DAYS) //split into daily buckets (this should only return one bucket)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS) //sets the time range to days results from midnight to 11.59pm
                    .build(); //builds the request

            DataReadResult dataReadResult =
                    Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES); //makes request to read data

            for (Bucket bucket : dataReadResult.getBuckets()) { //for every bucket of data (should only be one in this case as we request a range of a day)
                DataSet data = bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA);
                dumpStepDataSet(data); //dumpStepDataSet method called and passes in the step data
            }
            return null;
        }
    }

    private class GetActivityTime extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... args) {
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(args[0].toString()));//read the first argument passed in and set this to the date
            } catch (Exception e) {

            }
            //set the start time to be midnight and the end time to be 11.59pm - ie one day results
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            long endTime = cal.getTimeInMillis();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            long startTime = cal.getTimeInMillis();

            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                    .bucketByTime(1, TimeUnit.DAYS) //split into daily buckets (this should only return one bucket)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS) //sets the time range to days results from midnight to 11.59pm
                    .build();

            DataReadResult dataReadResult =
                    Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES); //makes request to read data and stores result

            for (Bucket bucket : dataReadResult.getBuckets()) { //for every bucket of data (should only be one in this case as we request a range of a day)
                // Process the session
                DataSet data = bucket.getDataSet(DataType.AGGREGATE_ACTIVITY_SUMMARY);
                dumpDataSet(data); //calls the dumpDataSet method and passes in activity data

            }
            return null;
        }
    }
}