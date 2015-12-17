package honoursproject.garethlloyd.healthinformationdelivery;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    ImageView leftArrow;
    ImageView rightArrow;
    Calendar date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        db = new DatabaseHandler(this);
        date =Calendar.getInstance();
        String dateString = new SimpleDateFormat("dd-MM-yyyy").format(date.getTime());
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
        setRating(dateString);
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date.add(Calendar.DATE, -1);
                String dayBefore = new SimpleDateFormat("dd-MM-yyyy").format(date.getTime());
                dateText.setText(dayBefore);
                setRating(dayBefore);
            }
        });
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date.add(Calendar.DATE,1);
                String dayAfter = new SimpleDateFormat("dd-MM-yyyy").format(date.getTime());
                dateText.setText(dayAfter);
                setRating(dayAfter);
            }
        });
        Button home = (Button) findViewById(R.id.buttonHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                ResultActivity.this.startActivity(intent);
            }
        });
    }

    public void setRating(String ratingDate){
        DataModel data = db.readData(ratingDate);
        stepText.setText(Html.fromHtml("Steps: <b>" + data.getSteps() + "</b>"));
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
        }else{
            stepRating.setRating(0.0f);
        }
        waterText.setText(Html.fromHtml("Water: <b>" + data.getWater() + "</b>"));
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
        }else{
            waterRating.setRating(0.0f);
        }
        fruitText.setText(Html.fromHtml("Fruit and Vegetables:  <b> " + data.getFruitAndVeg() + "</b>"));
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
        }else{
            fruitRating.setRating(0.0f);
        }
        activityText.setText(Html.fromHtml("Activity Time:  <b>" + data.getActivityTime() +  " mins</b>"));
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
        }else{
            activityRating.setRating(0.0f);
        }
        date.add(Calendar.DATE, -1);
        String dayBefore = new SimpleDateFormat("dd-MM-yyyy").format(date.getTime());
        if(!db.rowExists(dayBefore)){
            leftArrow.setVisibility(View.INVISIBLE);
        }else{
            leftArrow.setVisibility(View.VISIBLE);
        }
        //date =Calendar.getInstance();
        date.add(Calendar.DATE, 2);
        String dayAfter = new SimpleDateFormat("dd-MM-yyyy").format(date.getTime());
        if(!db.rowExists(dayAfter)){
            rightArrow.setVisibility(View.INVISIBLE);
        }else{
            rightArrow.setVisibility(View.VISIBLE);
        }
        date.add(Calendar.DATE,-1);
    }
}
