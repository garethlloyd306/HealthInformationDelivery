package honoursproject.garethlloyd.healthinformationdelivery;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentPagerAdapter;
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

    public ViewPagerAdapter(Context context, int[] values, int[] icons, int[] targets, String[] titles, int[] images, String[] text) {
        this.context = context;
        this.values = values;
        this.icons = icons;
        this.targets = targets;
        this.titles = titles;
        this.images = images;
        this.text = text;
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

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.page_item, container,
                false);

        // Locate the TextViews in viewpager_item.xml
        value = (TextView) itemView.findViewById(R.id.stepText);
        donut = (DonutProgress) itemView.findViewById(R.id.progressBar);
        imageView = (ImageView) itemView.findViewById(R.id.centerImage);


        imageView.setImageResource(images[position]);
        if (position == 1) {
            imageView.setTag("Water");
        } else if (position == 2) {
            imageView.setTag("Fruit");
        }
        value.setText(text[position] + ": " + values[position]);
        int progress = (int) (((double) values[position] / (double) targets[position]) * 100.f);
        if (progress >= 100) {
            progress = 100;
        }
        donut.setProgress(progress);
        Button daily = (Button) itemView.findViewById(R.id.button);
        daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ResultActivity.class);
                context.startActivity(intent);
            }
        });
        container.addView(itemView);

        return itemView;
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
