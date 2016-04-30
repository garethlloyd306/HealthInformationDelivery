package honoursproject.garethlloyd.healthinformationdelivery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Database Handler Class
 * <p/>
 * Written by Gareth Lloyd
 * <p/>
 * This Class creates the database tables, and has the method for reading and updating the data stored in the database.
 * <p/>
 * http://developer.android.com/training/basics/data-storage/databases.html
 * http://www.tutorialspoint.com/android/android_sqlite_database.htm
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "healthInfo";
    private static final String TABLE_DAILY = "daily";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_TROPHIES = " trophies";
    private static final String KEY_DATE = "date";
    private static final String KEY_STEPS = "steps";
    private static final String KEY_FRUIT = "fruit";
    private static final String KEY_WATER = "water";
    private static final String KEY_ACTIVITY = "activity";
    private static final String KEY_LAST = "last_active";
    private static final String KEY_STREAK = "days_streak";
    private static final String KEY_ID = "userid";
    private static final String KEY_USAGE = "usage";
    private static final String KEY_EVERYTHING = "everything";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create daily table which stores the daily health results of the user.
        String CREATE_DAILY_TABLE = "CREATE TABLE " + TABLE_DAILY + "("
                + KEY_DATE + " TEXT PRIMARY KEY," + KEY_STEPS + " INTEGER,"
                + KEY_FRUIT + " INTEGER," + KEY_WATER + " INTEGER,"
                + KEY_ACTIVITY + " INTEGER" + ")";
        db.execSQL(CREATE_DAILY_TABLE);

        //Creates the user table which stores the last active date and the current usage streak
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " TEXT PRIMARY KEY," + KEY_LAST + " TEXT,"
                + KEY_STREAK + " INTEGER" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        //Creates the trophies table which stores the trophy currently earned for each section  - ie "B" for bronze trophy and "S" for silver
        String CREATE_TROPHIES_TABLE = "CREATE TABLE " + TABLE_TROPHIES + "("
                + KEY_ID + " TEXT PRIMARY KEY," + KEY_USAGE + " TEXT,"
                + KEY_EVERYTHING + " TEXT," + KEY_STEPS + " TEXT,"
                + KEY_ACTIVITY + " TEXT," + KEY_WATER + " TEXT,"
                + KEY_FRUIT + " TEXT)";
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

    /*
        Method to insert new row into database
        Parameters: DailyStore data - data to be inserted
     */

    public void addHealthData(DailyStore data) { //inserting new row into the database
        SQLiteDatabase db = this.getWritableDatabase(); //getting database connection
        //Creates new contentValues which is an empty set of values.
        ContentValues values = new ContentValues();
        //adds all the values from the parameter passed in onto the set
        values.put(KEY_DATE, data.getDate());
        values.put(KEY_STEPS, data.getSteps());
        values.put(KEY_ACTIVITY, data.getActivityTime());
        values.put(KEY_FRUIT, data.getFruitAndVeg());
        values.put(KEY_WATER, data.getWater());
        db.insert(TABLE_DAILY, null, values); //inserts the results into the table
        db.close(); // Closing database connection
    }

    /*
        Method to update the step column in the database
        Parameters: int steps - the new steps value that is to be updated to
                    String date - the date of the row that needs to be updated
     */
    public void updateSteps(int steps, String date) {
        SQLiteDatabase db = this.getWritableDatabase(); //gets database connection
        //Creates new contentValues which is an empty set of values.
        ContentValues values = new ContentValues();
        if (rowExists(date)) { //checks to see if the row for that date exists
            values.put(KEY_STEPS, steps); //if it does then it adds the steps value to the set
            db.update(TABLE_DAILY, values, KEY_DATE + " = ?", new String[]{date}); //updates the row in the database that matches the date passed in
        } else { //if it doesn't exist, then a new row is inserted
            //adds the steps and date value to the set, the rest are set to 0 as their is no value for them yet
            values.put(KEY_DATE, date);
            values.put(KEY_STEPS, steps);
            values.put(KEY_ACTIVITY, 0);
            values.put(KEY_FRUIT, 0);
            values.put(KEY_WATER, 0);
            db.insert(TABLE_DAILY, null, values); //inserts the results into the table
        }
        db.close(); //closes database connection
    }

    /*
      Method to update the fruit column in the database
      Parameters: int fruit - the new fruit value that is to be updated to
                  String date - the date of the row that needs to be updated
   */
    public void updateFruit(int fruit, String date) {
        SQLiteDatabase db = this.getWritableDatabase(); //gets the database connection
        //Creates new contentValues which is an empty set of values.
        ContentValues values = new ContentValues();
        if (rowExists(date)) { //checks to see if the row for that date exists
            values.put(KEY_FRUIT, fruit); //if it does then adds the fruit value to the set
            db.update(TABLE_DAILY, values, KEY_DATE + " = ?", new String[]{date}); //updates the row in the database that matches the date passed in
        } else { //if it doesn't exist, then a new row is inserted
            //adds the fruit and date value to the set, the rest are set to 0 as their is no value for them yet
            values.put(KEY_DATE, date);
            values.put(KEY_STEPS, 0);
            values.put(KEY_ACTIVITY, 0);
            values.put(KEY_FRUIT, fruit);
            values.put(KEY_WATER, 0);
            db.insert(TABLE_DAILY, null, values); //inserts the results into the table
        }
        db.close(); //closes database connection
    }

    /*
      Method to update the water column in the database
      Parameters: int water - the new water value that is to be updated to
                  String date - the date of the row that needs to be updated
   */
    public void updateWater(int water, String date) {
        SQLiteDatabase db = this.getWritableDatabase();//gets the database connection
        //Creates new contentValues which is an empty set of values.
        ContentValues values = new ContentValues();
        if (rowExists(date)) { //checks to see if the row for that date exists
            values.put(KEY_WATER, water); //if it does then adds the water value to the set
            db.update(TABLE_DAILY, values, KEY_DATE + " = ?", new String[]{date}); //updates the row in the database that matches the date passed in
        } else { //if it doesn't exist, then a new row is inserted
            //adds the water and date value to the set, the rest are set to 0 as their is no value for them yet
            values.put(KEY_DATE, date);
            values.put(KEY_STEPS, 0);
            values.put(KEY_ACTIVITY, 0);
            values.put(KEY_FRUIT, 0);
            values.put(KEY_WATER, water);
            db.insert(TABLE_DAILY, null, values); //inserts the results into the table
        }
        db.close(); //closes database connection
    }

    /*
      Method to update the activity time column in the database
      Parameters: int activity - the new activity value that is to be updated to
                  String date - the date of the row that needs to be updated
   */
    public void updateActivity(int activity, String date) {
        SQLiteDatabase db = this.getWritableDatabase();//gets the database connection
        //creates new content values which is an empty set of values
        ContentValues values = new ContentValues();
        if (rowExists(date)) { //checks to see if the row for that date exists
            values.put(KEY_ACTIVITY, activity); //if it does then adds the activity value to the set
            db.update(TABLE_DAILY, values, KEY_DATE + " = ?", new String[]{date}); //updates the row in the database that matches the date passed in
        } else { //if it doesn't exist, then a new row is inserted
            //adds the activity and date value to the set, the rest are set to 0 as their is no value for them yet
            values.put(KEY_DATE, date);
            values.put(KEY_STEPS, 0);
            values.put(KEY_ACTIVITY, activity);
            values.put(KEY_FRUIT, 0);
            values.put(KEY_WATER, 0);
            db.insert(TABLE_DAILY, null, values); //inserts the results into the table
        }
        db.close(); //closes the database connection
    }

    /*
        Method to check whether a row exists in the database or not
        Returns: true if exists, false if it doesn't
        Parameter: String date - the date to be checked to see if that row existed
     */
    public boolean rowExists(String date) {
        SQLiteDatabase db = this.getReadableDatabase(); //opens readable database
        //SQL query to select all rows in the daily table where the date is the date parameter passed in
        //results are stored in the cursor
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DAILY + " WHERE " + KEY_DATE + " =?", new String[]{date});
        if (cursor.getCount() > 0) {
            cursor.close(); //close the cursor
            return true; //if the cursor count is greater than 0 then the row exists - returns true
        }
        cursor.close(); //close the cursor
        db.close(); //close the database
        return false; //if the cursor count is 0 then the row does not exist - returns false
    }

    /*
        Method to read the results for a day
        Returns: Data Store filled with the days results
        Parameter: String date - date of the results to be returned
     */
    public DailyStore readDailyResults(String date) {
        SQLiteDatabase db = this.getReadableDatabase(); //opens readable database
        //SQL QUERY to select all rows in the daily table where the date is the date parameter passed in
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DAILY + " WHERE " + KEY_DATE + " =?", new String[]{date});
        if (cursor != null) {
            if (cursor.moveToFirst()) { //moving to the first row
                DailyStore data = new DailyStore(); //new data store
                //set the data store to the value stored in the database
                data.setSteps(cursor.getInt(cursor.getColumnIndex(KEY_STEPS)));
                data.setWater(cursor.getInt(cursor.getColumnIndex(KEY_WATER)));
                data.setFruitAndVeg(cursor.getInt(cursor.getColumnIndex(KEY_FRUIT)));
                data.setActivityTime(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVITY)));
                cursor.close(); //closes the cursor
                db.close(); //closes the database
                return data; //returns the data store filled with the days results
            }
        }
        db.close(); //closes the database
        return null; //returns null if there is no results for the date
    }

    /*
       Method to update the last active and the current usage Streak
       Returns: Data Store filled with the days results
       Parameter: String date - date of the results to be returned
    */
    public String updateLastActive(String date) {
        SQLiteDatabase db = this.getReadableDatabase(); //get the readable database connection
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);  //select all from the users table
        ContentValues values = new ContentValues();
        if (cursor.getCount() == 0) { //if the table is empty - insert a new row with the current date and streak of one day
            values.put(KEY_ID, "1");
            values.put(KEY_LAST, date);
            values.put(KEY_STREAK, 1);
            db.insert(TABLE_USERS, null, values);
            cursor.close();
            return null;
        } else {
            cursor.moveToFirst(); //move the cursor to the first row
            int streak = cursor.getInt(cursor.getColumnIndex(KEY_STREAK)); //get the current streak usage and store this in local variable
            String lastDate = cursor.getString(cursor.getColumnIndex(KEY_LAST));//get the last active date and store this to be returned
            Calendar calendar = Calendar.getInstance(); //get today's date
            String today = new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime()); //convert this to string
            calendar.add(Calendar.DAY_OF_YEAR, -1); //take one day away for yesterdays date
            String yesterday = new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime()); //convert this to string
            if (lastDate.equals(yesterday)) { //if last active date is equal to yesterday
                streak++; //add one to the streak
            } else if (!lastDate.equals(today)) { //if the last active date is not equal to today or yesterday then the streak is finished and need to be reset
                streak = 1;
            }
            values.put(KEY_LAST, date);
            values.put(KEY_STREAK, streak);
            db.update(TABLE_USERS, values, KEY_ID + " = 1", null); //update the users tables with the new album
            cursor.close();
            db.close();
            return lastDate;// return the last active string
        }
    }

    /*Method to get the current streak of the user that is stored in the database
    Return: int of current streak
     */
    public int getStreak() {
        SQLiteDatabase db = this.getReadableDatabase(); //get readable database connection
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null); //select all from the users table
        if (cursor.getCount() != 0) { //if the user table is not empty
            cursor.moveToFirst(); //move to the first row
            int streak = cursor.getInt(cursor.getColumnIndex(KEY_STREAK)); //get the streak from the cursor and store in variable to be returned
            cursor.close();
            db.close();
            return streak;
        }
        return 0;
    }

    /*
        Method to insert new row of trophies
        This is only needed to have one row so this is checked to see if its empty
        If its not empty then no row is added
     */
    public void addTrophies() {
        SQLiteDatabase db = this.getReadableDatabase(); //get readable database
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TROPHIES, null); //select all rows from the trophies table
        ContentValues values = new ContentValues();
        if (cursor.getCount() == 0) { //if the table is empty then insert new row (to ensure there is only one row)
            //"N" set to all values as no trophies have been earned yet
            values.put(KEY_USAGE, "N");
            values.put(KEY_EVERYTHING, "N");
            values.put(KEY_STEPS, "N");
            values.put(KEY_FRUIT, "N");
            values.put(KEY_WATER, "N");
            values.put(KEY_ACTIVITY, "N");
            db.insert(TABLE_TROPHIES, null, values); //insert this row into the database
        }
        cursor.close();
        db.close();
    }

    /* Method to read all the users trophies from the database
        Returns TrophyStore  - users currently achieved trophies

     */
    public TrophyStore readTrophies() {
        SQLiteDatabase db = this.getReadableDatabase(); //gets database connection
        Cursor cursor = db.rawQuery("Select * FROM " + TABLE_TROPHIES, null); //selects all the rows from the trophies table, should only be one row
        if (cursor != null) { //checks the cursor isn't empty before moving to the first
            //It then sets the trophy store to value stored in the cursor
            cursor.moveToFirst();
            TrophyStore trophy = new TrophyStore();
            trophy.setUsage(cursor.getString(cursor.getColumnIndex(KEY_USAGE)));
            trophy.setEverything(cursor.getString(cursor.getColumnIndex(KEY_EVERYTHING)));
            trophy.setSteps(cursor.getString(cursor.getColumnIndex(KEY_STEPS)));
            trophy.setWater(cursor.getString(cursor.getColumnIndex(KEY_WATER)));
            trophy.setFruit(cursor.getString(cursor.getColumnIndex(KEY_FRUIT)));
            trophy.setActive(cursor.getString(cursor.getColumnIndex(KEY_ACTIVITY)));
            db.close();
            cursor.close();
            return trophy; //returns the trophy store
        }
        db.close(); //close the db connection
        return null;
    }

    /*
        Method to update the value in the database for trophies
        Parameters: String trophy - the name of the trophy being updated
                    String result - The letter of the new trophy
     */

    public void updateTrophies(String trophy, String result) {
        SQLiteDatabase db = this.getReadableDatabase(); //get readable database connection
        ContentValues values = new ContentValues();
        if (trophy.equals("Usage")) { //if the trophy being updated is Usage - updated the correct row in the table
            values.put(KEY_USAGE, result);
            db.update(TABLE_TROPHIES, values, null, null);
        } else if (trophy.equals("Everything")) {//if the trophy being updated is Everything - updated the correct row in the table
            values.put(KEY_EVERYTHING, result);
            db.update(TABLE_TROPHIES, values, null, null);
        } else if (trophy.equals("Steps")) { //if the trophy being updated is Steps - updated the correct row in the table
            values.put(KEY_STEPS, result);
            db.update(TABLE_TROPHIES, values, null, null);
        } else if (trophy.equals("Water")) { //if the trophy being updated is Water - updated the correct row in the table
            values.put(KEY_WATER, result);
            db.update(TABLE_TROPHIES, values, null, null);
        } else if (trophy.equals("Fruit")) { //if the trophy being updated is Fruit - updated the correct row in the table
            values.put(KEY_FRUIT, result);
            db.update(TABLE_TROPHIES, values, null, null);
        } else if (trophy.equals("Active")) {
            values.put(KEY_ACTIVITY, result);
            db.update(TABLE_TROPHIES, values, null, null);
        }
        db.close(); //close the db connection
    }


}
