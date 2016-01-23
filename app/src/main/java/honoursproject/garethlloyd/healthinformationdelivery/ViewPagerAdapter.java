package honoursproject.garethlloyd.healthinformationdelivery;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.viewpagerindicator.IconPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.transform.Result;

public class ViewPagerAdapter extends PagerAdapter implements IconPagerAdapter {
    // Declare Variables
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



        imageView.setImageResource(images[position]);
        if (position == 0) {
            plusView.setVisibility(View.INVISIBLE);
            minusView.setVisibility(View.INVISIBLE);
        } else if (position == 1) {
            plusView.setTag("plusWater");
            minusView.setTag("minusWater");
        }else if (position==2){
            plusView.setTag("plusFruit");
            minusView.setTag("minusFruit");
        }else if (position ==3){
            plusView.setVisibility(View.INVISIBLE);
            minusView.setVisibility(View.INVISIBLE);
        }
        value.setText(text[position] + ": " + values[position]);
        int progress = (int) (((double) values[position] / (double) targets[position]) * 100.f);
        if (progress >= 100) {
            progress = 100;
        }
        if(position ==0){
            if(values[position] >= 10000){
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.gold));
            }else if(values[position] >= 7500){
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.silver));
            }else if (values[position] >= 5000) {
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.bronze));
            }else{
                donut.setFinishedStrokeColor(Color.RED);
            }
        }else if (position ==1){
            if(values[position] >= 8){
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.gold));
            }else if(values[position] >= 6){
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.silver));
            }else if (values[position] >= 4) {
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.bronze));
            }else{
                donut.setFinishedStrokeColor(Color.RED);
            }

        }else if(position ==2){
            if(values[position] >= 5){
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.gold));
            }else if(values[position] >= 4){
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.silver));
            }else if (values[position] >= 2) {
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.bronze));
            }else{
                donut.setFinishedStrokeColor(Color.RED);
            }
        }else if(position ==3){
            if(values[position] >= 30){
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.gold));
            }else if(values[position] >= 20){
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.silver));
            }else if (values[position] >= 10) {
                donut.setFinishedStrokeColor(ContextCompat.getColor(context, R.color.bronze));
            }else{
                donut.setFinishedStrokeColor(Color.RED);
            }
        }
        donut.setProgress(progress);
        ImageView daily = (ImageView) itemView.findViewById(R.id.button);
        daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ResultActivity.class);
                context.startActivity(intent);
            }
        });

        ImageView trophy = (ImageView) itemView.findViewById(R.id.buttonTrophy);
        trophy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TrophyActivity.class);
                context.startActivity(intent);
            }
        });

        ImageView speech = (ImageView) itemView.findViewById(R.id.imageView);
        speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(position ==0){
                    String text ="Well done, you've managed " + values[position] + " Steps today! Keep it up";
                    textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null);
                } else if(position ==1){
                    String text ="Well done, you've drunk " + values[position] + "  glasses of water today! Keep it up";
                    textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null);
                } else if(position ==2){
                    String text ="Well done, you've eaten " + values[position] + " fruit and vegetables today! Keep it up";
                    textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null);
                } else if(position ==3){
                    String text ="Well done, you've been active for  " + values[position] + " minutes today! Keep it up";
                    textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null);
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
