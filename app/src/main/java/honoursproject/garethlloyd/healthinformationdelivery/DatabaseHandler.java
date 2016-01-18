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

    // Contacts Table Columns names
    private static final String KEY_DATE = "date";
    private static final String KEY_STEPS = "steps";
    private static final String KEY_FRUIT = "fruit";
    private static final String KEY_WATER = "water";
    private static final String KEY_ACTIVITY = "activity";
    private static final String KEY_LAST = "last_active";
    private static final String KEY_STREAK ="days_streak";
    private static final String KEY_ID ="userid";

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
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAILY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
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
        values.put(KEY_STEPS, steps);
        db.update(TABLE_DAILY, values, KEY_DATE + " = ?", new String[]{date});
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
        values.put(KEY_ACTIVITY, activity);
        db.update(TABLE_DAILY, values, KEY_DATE + " = ?", new String[]{date});

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
            values.put(KEY_ID,1);
            values.put(KEY_LAST,date);
            values.put(KEY_STREAK, 1);
            db.insert(TABLE_USERS,null,values);
            cursor.close();
            return null;
        }else{
            int streak = cursor.getInt(cursor.getColumnIndex(KEY_STREAK));
            String lastDate = cursor.getString(cursor.getColumnIndex(KEY_LAST));
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR,-1);
            String yesterday = new SimpleDateFormat("dd-MM-yyyy").format(calendar);
            if(lastDate==yesterday){
                streak++;
            }else{
                streak=1;
            }
            values.put(KEY_LAST,date);
            values.put(KEY_STREAK,streak);
            cursor.close();
            return lastDate;
        }
    }
}
