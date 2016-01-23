package honoursproject.garethlloyd.healthinformationdelivery;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView text;
    private GoogleApiClient mClient = null;
    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private String TAG = "Log";
    ViewPager viewPager;
    PagerAdapter adapter;
    DatabaseHandler db;
    int steps;
    int water;
    int fruit;
    int time;
    int[] icons;
    int[] values;
    String[] titles;
    int[] targets;
    int[] images;
    String[] texts;
    TabPageIndicator mIndicator;
    UnderlinePageIndicator mIndicator1;
    String SAMPLE_SESSION_NAME;
    String lastActive;

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
        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(MainActivity.this, values, icons, targets, titles, images, texts);
        viewPager.setAdapter(adapter);
        mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);
        mIndicator1 = (UnderlinePageIndicator) findViewById(R.id.indicator1);
        mIndicator1.setFades(false);
        mIndicator1.setViewPager(viewPager);
        db = new DatabaseHandler(this);
        DataModel data = new DataModel();
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        boolean exists = db.rowExists(date);
        if (!exists) {
            data.setDate(date);
            data.setActivityTime(0);
            data.setFruitAndVeg(0);
            data.setSteps(0);
            data.setWater(0);
            db.addHealthData(data);
        }
        db.readData(date);
        buildFitnessClient();
        readDataFromDB();
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 06);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
        SAMPLE_SESSION_NAME = "Time";

        lastActive = db.updateLastActive(date);
        db.addTrophies();


    }

    public void buildFitnessClient() {
        mClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.CONFIG_API)
                .addApi(Fitness.SESSIONS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                //getStepTotal();
                                Date today = new Date();
                                Calendar active = Calendar.getInstance();
                                try {
                                    Log.i("ACTIVE TIME, ", "Active: " + new SimpleDateFormat("dd-MM-yyyy").parse(lastActive));
                                    active.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(lastActive));
                                } catch (Exception e) {
                                    Log.e("Error", e.toString());
                                }

                                Log.i("Active: ", "Today: " + today.compareTo(active.getTime()));
                                    while (today.compareTo(active.getTime())>=0) {
                                        GetSteps getSteps = new GetSteps();
                                        Log.i("ACTIVE", new SimpleDateFormat("dd-MM-yyyy").format(active.getTime()));
                                        getSteps.execute(new SimpleDateFormat("dd-MM-yyyy").format(active.getTime()));
                                        GetActivityTime activityTime = new GetActivityTime();
                                        activityTime.execute(new SimpleDateFormat("dd-MM-yyyy").format(active.getTime()));
                                        active.add(Calendar.DAY_OF_YEAR, +1);
                                        Log.i("Active: ", "Today: " + today.compareTo(active.getTime()));
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

    private void getStepTotal() {
        PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.TYPE_STEP_COUNT_DELTA);
        result.setResultCallback(new ResultCallback<DailyTotalResult>() {
            @Override
            public void onResult(DailyTotalResult dailyTotalResult) {
                DataSet totalSet = dailyTotalResult.getTotal();
                long total = totalSet.isEmpty()
                        ? 0
                        : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                steps = (int) total;
                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                db.updateSteps(steps, date);
                readDataFromDB();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private class GetSteps extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String...args) {
            Calendar cal = Calendar.getInstance();
            try{
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(args[0].toString()));
            }catch (Exception e){

            }
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            long endTime = cal.getTimeInMillis();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE,0);
            long startTime = cal.getTimeInMillis();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
            Log.i(TAG, "Range End: " + dateFormat.format(endTime));

            DataReadRequest readRequest = new DataReadRequest.Builder()
                    // The data request can specify multiple data types to return, effectively
                    // combining multiple data queries into one call.
                    // In this example, it's very unlikely that the request is for several hundred
                    // datapoints each consisting of a few steps and a timestamp.  The more likely
                    // scenario is wanting to see how many steps were walked per day, for 7 days.
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                            // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                            // bucketByTime allows for a time span, whereas bucketBySession would allow
                            // bucketing by "sessions", which would need to be defined in code.
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build();

            DataReadResult dataReadResult =
                    Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES);

// Get a list of the sessions that match the criteria to check the result.
            Log.i(TAG, "Session read was successful. Number of returned sessions is: "
                    + dataReadResult.getBuckets().size());


            for (Bucket bucket: dataReadResult.getBuckets()){
                // Process the session
                DataSet data = bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA);
                dumpStepDataSet(data);

            }
            return null;
        }
    }


    private class GetActivityTime extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String...args) {
            Calendar cal = Calendar.getInstance();

            Log.i("DATE", args[0].toString());
            try{
                cal.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(args[0].toString()));
            }catch (Exception e){

            }
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            long endTime = cal.getTimeInMillis();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE,0);
            long startTime = cal.getTimeInMillis();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
            Log.i(TAG, "Range End: " + dateFormat.format(endTime));

            DataReadRequest readRequest = new DataReadRequest.Builder()
                    // The data request can specify multiple data types to return, effectively
                    // combining multiple data queries into one call.
                    // In this example, it's very unlikely that the request is for several hundred
                    // datapoints each consisting of a few steps and a timestamp.  The more likely
                    // scenario is wanting to see how many steps were walked per day, for 7 days.
                    .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                            // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                            // bucketByTime allows for a time span, whereas bucketBySession would allow
                            // bucketing by "sessions", which would need to be defined in code.
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build();

            DataReadResult dataReadResult =
                    Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES);

// Get a list of the sessions that match the criteria to check the result.
            Log.i(TAG, "Session read was successful. Number of returned sessions is: "
                    + dataReadResult.getBuckets().size());


            for (Bucket bucket: dataReadResult.getBuckets()){
                // Process the session
                DataSet data = bucket.getDataSet(DataType.AGGREGATE_ACTIVITY_SUMMARY);
                dumpDataSet(data);

            }
            return null;
        }
    }

    public void readDataFromDB() {
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        DataModel data = db.readData(date);
        Log.d("Data", "Steps: " + db + data.getSteps() + "Water " + data.getWater() + "Fruit " + data.getFruitAndVeg() + "Activity" + data.getActivityTime());
        values[0] = data.getSteps();
        values[1] = data.getWater();
        values[2] = data.getFruitAndVeg();
        values[3] = data.getActivityTime();
        adapter.notifyDataSetChanged();
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

    public void updateData(View view) {
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
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
        final AlertDialog dialog = builder.create();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (view.getTag() != null) {
            int result;
            if (view.getTag().toString() == "plusWater" || view.getTag().toString()=="minusWater") {
                if (view.getTag().toString() == "plusWater"){
                    result = values[1] + 1;
                }else{
                    result = values[1]-1;
                }
                db.updateWater(result, date);
                readDataFromDB();
                adapter.notifyDataSetChanged();
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
            } else if (view.getTag().toString() == "minusFruit" || view.getTag().toString()=="plusFruit") {
                if (view.getTag().toString() == "plusFruit"){
                    result = values[2] + 1;
                }else{
                    result = values[2]- 1;
                }
                db.updateFruit(result, date);
                readDataFromDB();
                adapter.notifyDataSetChanged();
                if (result == 2) {
                    text1.setText("Bronze Award!");
                    imageAward.setImageResource(R.drawable.bronze_star);
                    dialog.show();
                } else if (result == 4) {
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


    private void dumpDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy   HH:mm");
        long totalDuration =0;
        Date date = new Date();


        for (DataPoint dp : dataSet.getDataPoints()) {
            long duration1 = TimeUnit.MILLISECONDS.toMinutes(dp.getValue(Field.FIELD_DURATION).asInt());
            //totalDuration += duration;
            int activity = dp.getValue(Field.FIELD_ACTIVITY).asInt();
            date.setTime(dp.getStartTime(TimeUnit.MILLISECONDS));
            Date endDate = new Date();
            endDate.setTime(dp.getEndTime(TimeUnit.MILLISECONDS));
            Log.i("GarethTest" ,new SimpleDateFormat("dd-MM-yyyy").format(date));
           //Log.i("GarethTest", "HEREEEEREREREREREREREREREREREREREREEEEEEEEEEEEE :           " + duration1);
            //Log.i("GarethTest", "Start : " + new SimpleDateFormat("dd-MM-yyyy").format(date) + "   End: " + new SimpleDateFormat("dd-MM-yyyy").format(endDate) + "  Activity: " + dp.getValue(Field.FIELD_ACTIVITY).asInt() + "   Duration:  " +TimeUnit.MILLISECONDS.toMinutes(dp.getValue(Field.FIELD_DURATION).asInt()));
            if(activity != 3 && activity !=0) {
                long duration = TimeUnit.MILLISECONDS.toMinutes(dp.getValue(Field.FIELD_DURATION).asInt());
                totalDuration += duration;
                date.setTime(dp.getStartTime(TimeUnit.MILLISECONDS));

                final int test = (int) totalDuration;
                final Date dateTest = date;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String date = new SimpleDateFormat("dd-MM-yyyy").format(dateTest);
                        Log.i("GarethTest", "Start : " + date + "  Activity: " + " Duration:  " + test);
                        if(test!=0){
                            db.updateActivity(test, date);
                        }
                        readDataFromDB();
                        adapter.notifyDataSetChanged();


                    }
                });
            }

        }
    }

    private void dumpStepDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy   HH:mm");
        int steps =0;
        Date date = new Date();


        for (DataPoint dp : dataSet.getDataPoints()) {
            steps = dp.getValue(Field.FIELD_STEPS).asInt();
            //totalDuration += duration
            date.setTime(dp.getStartTime(TimeUnit.MILLISECONDS));
            Date endDate = new Date();
            endDate.setTime(dp.getEndTime(TimeUnit.MILLISECONDS));
            Log.i("GarethTest" ,new SimpleDateFormat("dd-MM-yyyy").format(date));
            //Log.i("GarethTest", "HEREEEEREREREREREREREREREREREREREREEEEEEEEEEEEE :           " + duration1);
            //Log.i("GarethTest", "Start : " + new SimpleDateFormat("dd-MM-yyyy").format(date) + "   End: " + new SimpleDateFormat("dd-MM-yyyy").format(endDate) + "  Activity: " + dp.getValue(Field.FIELD_ACTIVITY).asInt() + "   Duration:  " +TimeUnit.MILLISECONDS.toMinutes(dp.getValue(Field.FIELD_DURATION).asInt()));

                final int test = (int) steps;
                final Date dateTest = date;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String date = new SimpleDateFormat("dd-MM-yyyy").format(dateTest);
                        Log.i("GarethTest", "Start : " + date + "  Activity: " + " Duration:  " + test);
                        if(test!=0){
                            db.updateSteps(test, date);
                        }
                        readDataFromDB();
                        adapter.notifyDataSetChanged();


                    }
                });
            }

        }
    }
