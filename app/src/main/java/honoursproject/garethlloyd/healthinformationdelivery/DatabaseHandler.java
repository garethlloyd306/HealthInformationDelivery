package honoursproject.garethlloyd.healthinformationdelivery;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "healthInfo";

    // Contacts table name
    private static final String TABLE_DAILY = "daily";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_TROPHIES =" trophies";

    // Contacts Table Columns names
    private static final String KEY_DATE = "date";
    private static final String KEY_STEPS = "steps";
    private static final String KEY_FRUIT = "fruit";
    private static final String KEY_WATER = "water";
    private static final String KEY_ACTIVITY = "activity";


    private static final String KEY_LAST = "last_active";
    private static final String KEY_STREAK ="days_streak";
    private static final String KEY_ID ="userid";

    private static final String KEY_ONE ="one_day";
    private static final String KEY_TWO ="two_day";
    private static final String KEY_WEEK ="week";
    private static final String KEY_MONTH ="month";
    private static final String KEY_GDAY ="gold_day";
    private static final String KEY_GTWO ="gold_two_day";
    private static final String KEY_GWEEK ="gold_week";
    private static final String KEY_GMONTH ="gold_month";
    private static final String KEY_GSTEP ="gold_step";
    private static final String KEY_SSTEP ="silver_step";
    private static final String KEY_BSTEP ="bronze_step";
    private static final String KEY_GWATER ="gold_water";
    private static final String KEY_SWATER ="silver_water";
    private static final String KEY_BWATER ="bronze_water";
    private static final String KEY_GFRUIT ="gold_fruit";
    private static final String KEY_SFRUIT ="silver_fruit";
    private static final String KEY_BFRUIT ="bronze_fruit";
    private static final String KEY_GACTIVE ="gold_active";
    private static final String KEY_SACTIVE ="silver_active";
    private static final String KEY_BACTIVE ="bronze_active";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_HEALTH_TABLE = "CREATE TABLE " + TABLE_DAILY + "("
                + KEY_DATE + " TEXT PRIMARY KEY," + KEY_STEPS + " INTEGER,"
                + KEY_FRUIT + " INTEGER," + KEY_WATER + " INTEGER,"
                + KEY_ACTIVITY + " INTEGER" + ")";
        db.execSQL(CREATE_HEALTH_TABLE);

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS+ "("
                + KEY_ID + " TEXT PRIMARY KEY," + KEY_LAST + " TEXT,"
                + KEY_STREAK + " INTEGER" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_TROPHIES_TABLE = "CREATE TABLE " + TABLE_TROPHIES+ "("
                + KEY_ID + " TEXT PRIMARY KEY," + KEY_ONE+ " INTEGER,"
                + KEY_TWO + " INTEGER," +  KEY_WEEK + " INTEGER,"
                + KEY_MONTH + " INTEGER," +  KEY_GDAY+ " INTEGER,"
                + KEY_GTWO + " INTEGER," +  KEY_GWEEK + " INTEGER,"
                + KEY_GMONTH + " INTEGER,"  +   KEY_GSTEP+ " INTEGER,"
                + KEY_SSTEP + " INTEGER," +  KEY_BSTEP + " INTEGER,"
                + KEY_GFRUIT + " INTEGER," +   KEY_SFRUIT+ " INTEGER,"
                + KEY_BFRUIT + " INTEGER," +  KEY_GWATER + " INTEGER,"
                + KEY_SWATER + " INTEGER," +  KEY_BWATER+ " INTEGER,"
                + KEY_GACTIVE + " INTEGER," +  KEY_SACTIVE + " INTEGER,"
                + KEY_BACTIVE + " INTEGER)";

        Log.i("garethtest", CREATE_TROPHIES_TABLE);
        db.execSQL(CREATE_TROPHIES_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAILY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TROPHIES);
        // Create tables again
        onCreate(db);
    }

    public void addHealthData(DataModel data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, data.getDate()); // Contact Name
        values.put(KEY_STEPS, data.getSteps());
        values.put(KEY_ACTIVITY, data.getActivityTime());
        values.put(KEY_FRUIT, data.getFruitAndVeg());
        values.put(KEY_WATER, data.getWater());
        db.insert(TABLE_DAILY, null, values);
        db.close(); // Closing database connection
    }

    public void updateSteps(int steps, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(rowExists(date)){
            values.put(KEY_STEPS, steps);
            db.update(TABLE_DAILY, values, KEY_DATE + " = ?", new String[]{date});
        }else{
            values.put(KEY_DATE, date); // Contact Name
            values.put(KEY_STEPS, steps);
            values.put(KEY_ACTIVITY, 0);
            values.put(KEY_FRUIT, 0);
            values.put(KEY_WATER, 0);
            db.insert(TABLE_DAILY, null, values);
            db.close(); //
        }
    }

    public void updateFruit(int fruit, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FRUIT, fruit);
        db.update(TABLE_DAILY, values, KEY_DATE + " = ?", new String[]{date});
    }

    public void updateWater(int water, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_WATER, water);

        db.update(TABLE_DAILY, values, KEY_DATE + " = ?", new String[]{date});

    }

    public void updateActivity(int activity, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(rowExists(date)){
            values.put(KEY_ACTIVITY, activity);
            db.update(TABLE_DAILY, values, KEY_DATE + " = ?", new String[]{date});
        }else{
            values.put(KEY_DATE, date); // Contact Name
            values.put(KEY_STEPS, 0);
            values.put(KEY_ACTIVITY, activity);
            values.put(KEY_FRUIT, 0);
            values.put(KEY_WATER, 0);
            db.insert(TABLE_DAILY, null, values);
            db.close(); //
        }

    }

    public boolean rowExists(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DAILY + " WHERE " + KEY_DATE + " =?", new String[]{date});
        if(cursor.getCount() > 0){
            cursor.close();
            return true ;
        }
        cursor.close();
        return false;
    }

    public DataModel readData(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        //todo update query

        Cursor cursor = db.query(TABLE_DAILY, new String[]{KEY_DATE,
                        KEY_STEPS, KEY_WATER, KEY_FRUIT, KEY_ACTIVITY}, KEY_DATE + "=?",
                new String[]{date}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            DataModel data = new DataModel();
            data.setSteps(cursor.getInt(cursor.getColumnIndex(KEY_STEPS)));
            data.setWater(cursor.getInt(cursor.getColumnIndex(KEY_WATER)));
            data.setFruitAndVeg(cursor.getInt(cursor.getColumnIndex(KEY_FRUIT)));
            data.setActivityTime(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVITY)));
            return data;
        }
        return null;
    }

    public String updateLastActive(String date){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        ContentValues values = new ContentValues();
        if(cursor.getCount() == 0){
            values.put(KEY_ID,"1");
            values.put(KEY_LAST,date);
            values.put(KEY_STREAK, 1);
            db.insert(TABLE_USERS,null,values);
            cursor.close();
            return null;
        }else{
            cursor.moveToFirst();
            int streak = cursor.getInt(cursor.getColumnIndex(KEY_STREAK));
            String lastDate = cursor.getString(cursor.getColumnIndex(KEY_LAST));
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            String yesterday = new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime());
            if(lastDate==yesterday){
                streak++;
            }else{
                streak=1;
            }
            values.put(KEY_LAST,yesterday);
            values.put(KEY_STREAK,streak);
          int test = db.update(TABLE_USERS, values, KEY_ID + " = 1" , null);
            Log.i("TestingUpdate", String.valueOf(test));
            cursor.close();
            return lastDate;
        }
    }

    public void addTrophies(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TROPHIES, null);
        ContentValues values = new ContentValues();
        if(cursor.getCount() == 0) {
            values.put(KEY_ONE, 1);
            values.put(KEY_TWO, 0);
            values.put(KEY_WEEK, 0);
            values.put(KEY_MONTH, 0);
            values.put(KEY_GDAY, 0);
            values.put(KEY_GTWO, 0);
            values.put(KEY_GWEEK, 0);
            values.put(KEY_GMONTH, 0);
            values.put(KEY_GSTEP, 0);
            values.put(KEY_SSTEP, 0);
            values.put(KEY_BSTEP, 0);
            values.put(KEY_GFRUIT, 0);
            values.put(KEY_SFRUIT, 0);
            values.put(KEY_BFRUIT, 0);
            values.put(KEY_GWATER, 0);
            values.put(KEY_SWATER, 0);
            values.put(KEY_BWATER, 0);
            values.put(KEY_GACTIVE, 0);
            values.put(KEY_SACTIVE, 0);
            values.put(KEY_BACTIVE, 0);
            db.insert(TABLE_TROPHIES, null, values);
        }
        cursor.close();
    }

    public TrophyModel readTrophies(){
        SQLiteDatabase db = this.getReadableDatabase();
       Cursor cursor= db.rawQuery("Select * FROM " + TABLE_TROPHIES, null);
        if (cursor != null) {
            cursor.moveToFirst();
            TrophyModel trophy = new TrophyModel();
            trophy.setOne(cursor.getInt(cursor.getColumnIndex(KEY_ONE)));
            trophy.setTwo(cursor.getInt(cursor.getColumnIndex(KEY_TWO)));
            trophy.setWeek(cursor.getInt(cursor.getColumnIndex(KEY_WEEK)));
            trophy.setMonth(cursor.getInt(cursor.getColumnIndex(KEY_MONTH)));
            trophy.setgOne(cursor.getInt(cursor.getColumnIndex(KEY_GDAY)));
            trophy.setgTwo(cursor.getInt(cursor.getColumnIndex(KEY_GTWO)));
            trophy.setgWeek(cursor.getInt(cursor.getColumnIndex(KEY_GWEEK)));
            trophy.setgMonth(cursor.getInt(cursor.getColumnIndex(KEY_GMONTH)));
            trophy.setgSteps(cursor.getInt(cursor.getColumnIndex(KEY_GSTEP)));
            trophy.setgWater(cursor.getInt(cursor.getColumnIndex(KEY_GWATER)));
            trophy.setgFruit(cursor.getInt(cursor.getColumnIndex(KEY_GFRUIT)));
            trophy.setgActive(cursor.getInt(cursor.getColumnIndex(KEY_GACTIVE)));
            trophy.setsSteps(cursor.getInt(cursor.getColumnIndex(KEY_SSTEP)));
            trophy.setsWater(cursor.getInt(cursor.getColumnIndex(KEY_SWATER)));
            trophy.setsFruit(cursor.getInt(cursor.getColumnIndex(KEY_SFRUIT)));
            trophy.setsActive(cursor.getInt(cursor.getColumnIndex(KEY_SACTIVE)));
            trophy.setbSteps(cursor.getInt(cursor.getColumnIndex(KEY_BSTEP)));
            trophy.setbWater(cursor.getInt(cursor.getColumnIndex(KEY_BWATER)));
            trophy.setbFruit(cursor.getInt(cursor.getColumnIndex(KEY_BFRUIT)));
            trophy.setbActive(cursor.getInt(cursor.getColumnIndex(KEY_BACTIVE)));
            return trophy;
        }
        return null;
    }

    public void updateTrophies(int trophy, int value){

    }


}
