package honoursproject.garethlloyd.healthinformationdelivery;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        String test = "";
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        db = new DatabaseHandler(this);
        DataModel data = db.readData(date);
        dateText = (TextView) findViewById(R.id.dateText);
        dateText.setText(date);
        stepText = (TextView) findViewById(R.id.textStep);
        stepText.setText("Steps: " + data.getSteps());
        stepRating = (RatingBar) findViewById(R.id.ratingBarStep);
        if(data.getSteps() >= 10000){
            stepRating.setRating(3.0f);
            LayerDrawable stars = (LayerDrawable) stepRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.gold), PorterDuff.Mode.SRC_ATOP);
        }else if(data.getSteps() >= 5000){
            stepRating.setRating(2.0f);
            LayerDrawable stars = (LayerDrawable) stepRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.silver), PorterDuff.Mode.SRC_ATOP);
        }else if(data.getSteps() >= 3000){
            stepRating.setRating(1.0f);
            LayerDrawable stars = (LayerDrawable) stepRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.bronze), PorterDuff.Mode.SRC_ATOP);
        }

        waterText = (TextView) findViewById(R.id.textWater);
        waterText.setText("Water: " + data.getWater());
        waterRating = (RatingBar) findViewById(R.id.ratingBarWater);
        if(data.getWater() >= 8) {
            waterRating.setRating(3.0f);
            LayerDrawable stars = (LayerDrawable) waterRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.gold), PorterDuff.Mode.SRC_ATOP);
        }else if(data.getWater() >= 6){
            waterRating.setRating(2.0f);
            LayerDrawable stars = (LayerDrawable) waterRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.silver), PorterDuff.Mode.SRC_ATOP);
        }else if(data.getWater() >= 4){
            waterRating.setRating(1.0f);
            LayerDrawable stars = (LayerDrawable) waterRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.bronze), PorterDuff.Mode.SRC_ATOP);
        }

        fruitText = (TextView) findViewById(R.id.textFruit);
        fruitText.setText("Fruit and Veg: " + data.getFruitAndVeg());
        fruitRating = (RatingBar) findViewById(R.id.ratingBarFruit);
        if(data.getFruitAndVeg() >= 5){
            fruitRating.setRating(3.0f);
            LayerDrawable stars = (LayerDrawable) fruitRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.gold), PorterDuff.Mode.SRC_ATOP);
        }else if(data.getFruitAndVeg() >= 4){
            fruitRating.setRating(2.0f);
            LayerDrawable stars = (LayerDrawable) fruitRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.silver), PorterDuff.Mode.SRC_ATOP);
        }else if(data.getFruitAndVeg() >= 2){
            fruitRating.setRating(1.0f);
            LayerDrawable stars = (LayerDrawable) fruitRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.bronze), PorterDuff.Mode.SRC_ATOP);
        }

        activityText = (TextView) findViewById(R.id.textActivity);
        activityText.setText("Activity Time: " + data.getActivityTime() + " mins");
        activityRating = (RatingBar) findViewById(R.id.ratingBarActivity);
        if(data.getActivityTime() >= 30){
            activityRating.setRating(3.0f);
            LayerDrawable stars = (LayerDrawable) activityRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.gold), PorterDuff.Mode.SRC_ATOP);
        }else if(data.getActivityTime() >= 20){
            activityRating.setRating(2.0f);
            LayerDrawable stars = (LayerDrawable) activityRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.silver), PorterDuff.Mode.SRC_ATOP);
        }else if(data.getActivityTime() >= 10) {
            activityRating.setRating(1.0f);
            LayerDrawable stars = (LayerDrawable) activityRating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.bronze), PorterDuff.Mode.SRC_ATOP);
        }
    }

}
