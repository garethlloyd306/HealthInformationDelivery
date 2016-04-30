package honoursproject.garethlloyd.healthinformationdelivery;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.Locale;

/*
        View Pager Adapter
        Controls the viewpager and the displaying of results on the home screen
        Written by Gareth Lloyd

        View Pager Library : https://www.github.com/JakeWharton/ViewPagerIndicator
        Circular Progress bar Library : https://www.github.com/lzyzsd/CircleProgress
        Tutorial: http://www.androidbegin.com/tutorial/android-jake-wharton-viewpager-indicator-tutorial/
 */

public class ViewPagerAdapter extends PagerAdapter implements IconPagerAdapter {
    Context context;
    int[] values;
    int[] icons;
    String[] titles;
    int[] targets;
    int[] images;
    String[] text;
    LayoutInflater inflater;
    TextToSpeech textToSpeech;

    public ViewPagerAdapter(Context context, int[] values, int[] icons, int[] targets, String[] titles, int[] images, String[] text) {
        this.context = context;
        this.values = values;
        this.icons = icons;
        this.targets = targets;
        this.titles = titles;
        this.images = images;
        this.text = text;
        //initialising the text to speech to the uk locale.
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeech.setLanguage(Locale.UK);
            }
        }
        );
    }

    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        // Declare Variables
        TextView value;
        DonutProgress donut;
        ImageView imageView;
        ImageView plusView;
        ImageView minusView;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.page_item, container,
                false);

        // Locate the TextViews in viewpager_item.xml
        value = (TextView) itemView.findViewById(R.id.stepText);
        donut = (DonutProgress) itemView.findViewById(R.id.progressBar);
        imageView = (ImageView) itemView.findViewById(R.id.centerImage);
        plusView = (ImageView) itemView.findViewById(R.id.plus);
        minusView = (ImageView) itemView.findViewById(R.id.minus);


        imageView.setImageResource(images[position]); //sets the icon to the correct one for the position
        if (position == 0) { //if the position is 0  this is  the steps screen so don't show the plus minus buttons
            plusView.setVisibility(View.INVISIBLE);
            minusView.setVisibility(View.INVISIBLE);
        } else if (position == 1) { //if the position is 1 this is the water screen , show plus minus buttons and set tag to water so this can be used
            //to determine which button was pressed
            plusView.setTag("plusWater");
            minusView.setTag("minusWater");
        } else if (position == 2) {//if the position is 2 this is the fruit screen , show plus minus buttons and set tag to fruit so this can be used
            //to determine which button was pressed
            plusView.setTag("plusFruit");
            minusView.setTag("minusFruit");
        } else if (position == 3) { //if the position is 3 this is the activity screen, plus and minus buttons are set to invisible
            plusView.setVisibility(View.INVISIBLE);
            minusView.setVisibility(View.INVISIBLE);
        }
        if (position != 3) { //this sets the value of the text depending on the position in the view pager- the first 3 do not need anything after value
            value.setText(text[position] + ": " + values[position]);
        } else {//the third is activity time and mins is required after to display how long the time is they have been active
            value.setText(text[position] + ": " + values[position] + " mins");
        }
        int progress = (int) (((double) values[position] / (double) targets[position]) * 100.f); //this works out the progress based on their targets and the value they have achieved
        if (progress >= 100) { //if its greater than 100 - resets to the maximum 100%
            progress = 100;
        }
        if (position == 0) { //if position is 0 - the steps page
            if (values[position] >= 10000) { //if gold award achieved set colour of progress to gold
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.gold));
            } else if (values[position] >= 7500) { //if silver award achieved set colour of progress to silver
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.silver));
            } else if (values[position] >= 5000) {//if bronze award achieved set colour of progress to bronze
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.bronze));
            } else { //if no award achieved set colour of progress to red
                donut.setFinishedStrokeColor(Color.RED);
            }
        } else if (position == 1) {// if position is 0 - the water page
            if (values[position] >= 8) {//if gold award achieved set colour of progress to gold
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.gold));
            } else if (values[position] >= 6) {//if silver award achieved set colour of progress to silver
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.silver));
            } else if (values[position] >= 4) {//if bronze award achieved set colour of progress to bronze
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.bronze));
            } else { //if no award achieved set colour of progress to red
                donut.setFinishedStrokeColor(Color.RED);
            }

        } else if (position == 2) { //if the position is 2 - the fruit and veg page
            if (values[position] >= 5) {//if gold award achieved set colour of progress to gold
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.gold));
            } else if (values[position] >= 4) {//if silver award achieved set colour of progress to silver
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.silver));
            } else if (values[position] >= 2) {//if bronze award achieved set colour of progress to bronze
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.bronze));
            } else { //if no award achieved set colour of progress to red
                donut.setFinishedStrokeColor(Color.RED);
            }
        } else if (position == 3) { //if the position is 2 - the activity time page
            if (values[position] >= 30) {//if gold award achieved set colour of progress to gold
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.gold));
            } else if (values[position] >= 20) {//if silver award achieved set colour of progress to silver
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.silver));
            } else if (values[position] >= 10) {//if bronze award achieved set colour of progress to bronze
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.bronze));
            } else { //if no award achieved set colour of progress to red
                donut.setFinishedStrokeColor(Color.RED);
            }
        }
        donut.setProgress(progress); //sets the progress bar
        ImageView daily = (ImageView) itemView.findViewById(R.id.button);
        daily.setOnClickListener(new View.OnClickListener() { //when daily button is pressed, opens results activity and this is the daily results screen
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ResultActivity.class);
                context.startActivity(intent);
            }
        });

        ImageView trophy = (ImageView) itemView.findViewById(R.id.buttonTrophy);
        trophy.setOnClickListener(new View.OnClickListener() { //when trophy button is pressed opens trophy activity and this is the trophy screen
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TrophyActivity.class);
                context.startActivity(intent);
            }
        });

        ImageView speech = (ImageView) itemView.findViewById(R.id.imageView);
        speech.setOnClickListener(new View.OnClickListener() { //when speech icon pressed this speaks the results to the user
            @Override
            public void onClick(View v) {
                //checks the current position and sets a congratulatory message with the number and what that is for and speaks this to the user
                if (position == 0) {
                    String text = "Well done, you've managed " + values[position] + " Steps today! Keep it up";
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                } else if (position == 1) {
                    String text = "Well done, you've drunk " + values[position] + "  glasses of water today! Keep it up";
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                } else if (position == 2) {
                    String text = "Well done, you've eaten " + values[position] + " fruit and vegetables today! Keep it up";
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                } else if (position == 3) {
                    String text = "Well done, you've been active for  " + values[position] + " minutes today! Keep it up";
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        container.addView(itemView);

        return itemView;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getIconResId(int index) {
        return icons[index];
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;

    }
}
