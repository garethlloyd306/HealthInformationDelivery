package honoursproject.garethlloyd.healthinformationdelivery;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.Date;
import java.text.SimpleDateFormat;

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
            db.addData(data);
        }
        db.readData(date);
        buildFitnessClient();
        readDataFromDB();
    }

    public void buildFitnessClient() {
        mClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.CONFIG_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                getStepTotal();
                                getActivityTime();
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

    private void getActivityTime() {

        time = (int) 0;
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        db.updateActivity(time, date);
        readDataFromDB();
        adapter.notifyDataSetChanged();

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
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(view1);
        TextView title = new TextView(this);
        title.setText("Well Done!");
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
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
            if (view.getTag().toString() == "Water") {
                int result = values[1] + 1;
                db.updateWater(result, date);
                readDataFromDB();
                adapter.notifyDataSetChanged();
                if(result == 4) {
                    text1.setText("Bronze Award!");
                    imageAward.setImageResource(R.drawable.bronze_star);
                    dialog.show();
                }else  if(result == 6) {
                    text1.setText("Silver Award!");
                    imageAward.setImageResource(R.drawable.silver_star);
                    dialog.show();
                }else if(result == 8) {
                    text1.setText("Gold Award!");
                    imageAward.setImageResource(R.drawable.gold_star);
                    dialog.show();
                }
            } else if (view.getTag() == "Fruit") {
                int result = values[2] + 1;
                db.updateFruit(result, date);
                readDataFromDB();
                adapter.notifyDataSetChanged();
                if(result == 2) {
                    text1.setText("Bronze Award!");
                    imageAward.setImageResource(R.drawable.bronze_star);
                    dialog.show();
                }else  if(result == 4) {
                    text1.setText("Silver Award!");
                    imageAward.setImageResource(R.drawable.silver_star);
                    dialog.show();
                }else if(result == 5) {
                    text1.setText("Gold Award!");
                    imageAward.setImageResource(R.drawable.gold_star);
                    dialog.show();
                }
            }
        }
    }
}
