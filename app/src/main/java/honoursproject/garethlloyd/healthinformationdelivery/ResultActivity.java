package honoursproject.garethlloyd.healthinformationdelivery;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
    Result Activity
    Written by Gareth Lloyd
    This is the activity that controls the daily results screen
 */

public class ResultActivity extends AppCompatActivity {
    DatabaseHandler db;
    TextView stepText;
    TextView waterText;
    TextView fruitText;
    TextView activityText;
    TextView dateText;
    RatingBar stepRating;
    RatingBar waterRating;
    RatingBar fruitRating;
    RatingBar activityRating;
    ImageView leftArrow;
    ImageView rightArrow;
    Calendar date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        db = new DatabaseHandler(this);
        date = Calendar.getInstance(); //gets today's date
        String dateString = new SimpleDateFormat("dd-MM-yyyy").format(date.getTime());//converts this to string
        dateText = (TextView) findViewById(R.id.dateText);
        dateText.setText(dateString);
        stepText = (TextView) findViewById(R.id.textStep);
        stepRating = (RatingBar) findViewById(R.id.ratingBarStep);
        waterText = (TextView) findViewById(R.id.textWater);
        waterRating = (RatingBar) findViewById(R.id.ratingBarWater);
        fruitText = (TextView) findViewById(R.id.textFruit);
        fruitRating = (RatingBar) findViewById(R.id.ratingBarFruit);
        activityText = (TextView) findViewById(R.id.textActivity);
        activityRating = (RatingBar) findViewById(R.id.ratingBarActivity);
        leftArrow = (ImageView) findViewById(R.id.left_arrow);
        rightArrow = (ImageView) findViewById(R.id.right_arrow);
        setRating(dateString); //calls  method to sets all the values from the database
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //When the left arrow is placed, one day is taken away for the day before
                date.add(Calendar.DATE, -1);
                String dayBefore = new SimpleDateFormat("dd-MM-yyyy").format(date.getTime());
                dateText.setText(dayBefore); //updates the date shown
                setRating(dayBefore); //calls method to set all values from database for updated date
            }
        });
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //when the right arrow is placed, one day is added for the day after.
                date.add(Calendar.DATE, 1);
                String dayAfter = new SimpleDateFormat("dd-MM-yyyy").format(date.getTime());
                dateText.setText(dayAfter); //updates the date shown
                setRating(dayAfter); //calls method to set all values from database for updated date
            }
        });
        Button home = (Button) findViewById(R.id.buttonHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //button returns the user to the main activity
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                ResultActivity.this.startActivity(intent);
            }
        });
    }

    /*
           Method to set the Rating and all the values.
           Parameter: String - date to get results from the database for
     */
    public void setRating(String ratingDate) {
        DailyStore data = db.readDailyResults(ratingDate); //read the results for the date from the database
        stepText.setText(Html.fromHtml("Steps: <b>" + data.getSteps() + "</b>")); //sets the text to the steps results
       /*
            checks the result against the target to see what reward has been achieved  - set progress to correct number of stars
            and colour these stars appropriately.
        */
        if (data.getSteps() >= 10000) { //if gold award achieved , set 3 stars and colour these gold
            stepRating.setRating(3.0f);
            LayerDrawable stars = (LayerDrawable) stepRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.gold), PorterDuff.Mode.SRC_ATOP);
        } else if (data.getSteps() >= 7500) { //if silver award achieved,set 2 stars and colour these silver
            stepRating.setRating(2.0f);
            LayerDrawable stars = (LayerDrawable) stepRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.silver), PorterDuff.Mode.SRC_ATOP);
        } else if (data.getSteps() >= 5000) { //if bronze award achieved, set 1 star and colour this bronze
            stepRating.setRating(1.0f);
            LayerDrawable stars = (LayerDrawable) stepRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.bronze), PorterDuff.Mode.SRC_ATOP);
        } else {
            stepRating.setRating(0.0f); //no award achieved, don't colour any stars.
        }
        waterText.setText(Html.fromHtml("Water: <b>" + data.getWater() + "</b>")); //sets the text for the water result
        if (data.getWater() >= 8) { //if gold award achieved , set 3 stars and colour these gold
            waterRating.setRating(3.0f);
            LayerDrawable stars = (LayerDrawable) waterRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.gold), PorterDuff.Mode.SRC_ATOP);
        } else if (data.getWater() >= 6) { //if silver award achieved , set 2 stars and colour these silver
            waterRating.setRating(2.0f);
            LayerDrawable stars = (LayerDrawable) waterRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.silver), PorterDuff.Mode.SRC_ATOP);
        } else if (data.getWater() >= 4) { //if bronze award achieved , set 1 stars and colour these bronze
            waterRating.setRating(1.0f);
            LayerDrawable stars = (LayerDrawable) waterRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.bronze), PorterDuff.Mode.SRC_ATOP);
        } else {
            waterRating.setRating(0.0f); //no award achieved don't colour any stars
        }
        fruitText.setText(Html.fromHtml("Fruit and Vegetables:  <b> " + data.getFruitAndVeg() + "</b>")); //sets the text for the fruit result
        if (data.getFruitAndVeg() >= 5) { //if gold award achieved , set 3 stars and colour these gold
            fruitRating.setRating(3.0f);
            LayerDrawable stars = (LayerDrawable) fruitRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.gold), PorterDuff.Mode.SRC_ATOP);
        } else if (data.getFruitAndVeg() >= 4) { //if silver award achieved , set 2 stars and colour these silver
            fruitRating.setRating(2.0f);
            LayerDrawable stars = (LayerDrawable) fruitRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.silver), PorterDuff.Mode.SRC_ATOP);
        } else if (data.getFruitAndVeg() >= 2) { //if bronze award achieved , set 1 stars and colour these bronze
            fruitRating.setRating(1.0f);
            LayerDrawable stars = (LayerDrawable) fruitRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.bronze), PorterDuff.Mode.SRC_ATOP);
        } else {
            fruitRating.setRating(0.0f); //if no award achieved, no stars are coloured
        }
        activityText.setText(Html.fromHtml("Activity Time:  <b>" + data.getActivityTime() + " mins</b>")); //set the text for the activity time result
        if (data.getActivityTime() >= 30) { //if gold award achieved , set 3 stars and colour these gold
            activityRating.setRating(3.0f);
            LayerDrawable stars = (LayerDrawable) activityRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.gold), PorterDuff.Mode.SRC_ATOP);
        } else if (data.getActivityTime() >= 20) { //if silver award achieved , set 2 stars and colour these silver
            activityRating.setRating(2.0f);
            LayerDrawable stars = (LayerDrawable) activityRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.silver), PorterDuff.Mode.SRC_ATOP);
        } else if (data.getActivityTime() >= 10) { //if bronze award achieved , set 1 stars and colour these bronze
            activityRating.setRating(1.0f);
            LayerDrawable stars = (LayerDrawable) activityRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.bronze), PorterDuff.Mode.SRC_ATOP);
        } else {
            activityRating.setRating(0.0f); //if no award achieved, no stars are coloured
        }
        checkArrows();
    }

    /*
        Method to check the database to see if their is data for the day before or the day after
        If there is data the arrow is visible and if not this is made invisible
     */
    public void checkArrows() {
        date.add(Calendar.DATE, -1); //sets the date to the day before
        String dayBefore = new SimpleDateFormat("dd-MM-yyyy").format(date.getTime()); //converts this to string
        if (!db.rowExists(dayBefore)) { //checks the database to see if this row exists for the day before
            leftArrow.setVisibility(View.INVISIBLE); //if it doesn't then set the left arrow to invisible
        } else {
            leftArrow.setVisibility(View.VISIBLE); //if there is set the left arrow to visible
        }
        date.add(Calendar.DATE, 2); //sets the date to the day after  ( 2 since it is currently the day before the current date)
        String dayAfter = new SimpleDateFormat("dd-MM-yyyy").format(date.getTime()); //converts this to string
        if (!db.rowExists(dayAfter)) { //checks the database to see if this row exists for the day after
            rightArrow.setVisibility(View.INVISIBLE); //if it doesn't then set the right arrow to invisible
        } else {
            rightArrow.setVisibility(View.VISIBLE); //if it does then set the right arrow to visible
        }
        date.add(Calendar.DATE, -1); //reset the date to the current date selected by taking one day away from the day after.
    }
}
