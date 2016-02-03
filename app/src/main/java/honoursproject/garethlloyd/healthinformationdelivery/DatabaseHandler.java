package honoursproject.garethlloyd.healthinformationdelivery;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.speech.RecognitionService;
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
    private static final String KEY_FRUIT ="fruit";
    private static final String KEY_WATER = "water";
    private static final String KEY_ACTIVITY = "activity";


    private static final String KEY_LAST = "last_active";
    private static final String KEY_STREAK ="days_streak";
    private static final String KEY_ID ="userid";

    private static final String KEY_USAGE ="usage";
    private static final String KEY_EVERYTHING ="everything";


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
                + KEY_ID + " TEXT PRIMARY KEY," + KEY_USAGE+ " TEXT,"
                + KEY_EVERYTHING + " TEXT," +  KEY_STEPS + " TEXT,"
                + KEY_ACTIVITY + " TEXT," +  KEY_WATER+ " TEXT,"
                + KEY_FRUIT + " TEXT)" ;

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
            if(cursor.moveToFirst()) {
                DataModel data = new DataModel();
                data.setSteps(cursor.getInt(cursor.getColumnIndex(KEY_STEPS)));
                data.setWater(cursor.getInt(cursor.getColumnIndex(KEY_WATER)));
                data.setFruitAndVeg(cursor.getInt(cursor.getColumnIndex(KEY_FRUIT)));
                data.setActivityTime(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVITY)));
                return data;
            }
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
            if(lastDate.equals(yesterday)){
                streak++;
            }else{
                streak=1;
            }
            values.put(KEY_LAST, yesterday);
            values.put(KEY_STREAK, streak);
          int test = db.update(TABLE_USERS, values, KEY_ID + " = 1" , null);
            Log.i("TestingUpdate", String.valueOf(test));
            cursor.close();
            return lastDate;
        }
    }

    public int getStreak(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        if (cursor.getCount()!=0) {
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(KEY_STREAK));
        }
        return 0;

    }


    public void addTrophies(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TROPHIES, null);
        ContentValues values = new ContentValues();
        if(cursor.getCount() == 0) {
            values.put(KEY_USAGE, "N");
            values.put(KEY_EVERYTHING, "N");
            values.put(KEY_STEPS, "N");
            values.put(KEY_FRUIT, "N");
            values.put(KEY_WATER, "N");
            values.put(KEY_ACTIVITY, "N");
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
            trophy.setUsage(cursor.getString(cursor.getColumnIndex(KEY_USAGE)));
            trophy.setEverything(cursor.getString(cursor.getColumnIndex(KEY_EVERYTHING)));
            trophy.setSteps(cursor.getString(cursor.getColumnIndex(KEY_STEPS)));
            trophy.setWater(cursor.getString(cursor.getColumnIndex(KEY_WATER)));
            trophy.setFruit(cursor.getString(cursor.getColumnIndex(KEY_FRUIT)));
            trophy.setActive(cursor.getString(cursor.getColumnIndex(KEY_ACTIVITY)));
            return trophy;
        }
        return null;
    }

    public void updateTrophies(String trophy, String result){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        ContentValues values = new ContentValues();
        if(trophy.equals("Usage")){
            values.put(KEY_USAGE, result);
            db.update(TABLE_TROPHIES, values, null, null);
        }else if(trophy.equals("Everything")){
            values.put(KEY_EVERYTHING,result);
            db.update(TABLE_TROPHIES, values, null,null);
        }else if(trophy.equals("Steps")){
            values.put(KEY_STEPS, result);
            db.update(TABLE_TROPHIES, values, null,null);
        }else if(trophy.equals("Water")){
            values.put(KEY_WATER, result);
            db.update(TABLE_TROPHIES, values, null, null);
        }else if(trophy.equals("Fruit")){
            values.put(KEY_FRUIT, result);
            db.update(TABLE_TROPHIES, values, null, null);
        }else if(trophy.equals("Active")){
            values.put(KEY_ACTIVITY,result);
            db.update(TABLE_TROPHIES, values, null,null);
        }

    }


}
