package honoursproject.garethlloyd.healthinformationdelivery;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TrophyActivity extends AppCompatActivity {

    DatabaseHandler db;
    ImageView[] trophies;
    TrophyModel results;
    ImageView trophyUsage;
    ImageView trophyEverything;
    ImageView trophySteps;
    ImageView trophyWater;
    ImageView trophyFruit;
    ImageView trophyActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trophy);
        db = new DatabaseHandler(this);
        trophies = new ImageView[20];
        results = db.readTrophies();

        trophies[0] = (ImageView) findViewById(R.id.trophy);
        trophies[1] = (ImageView) findViewById(R.id.trophy2);
        trophies[2] = (ImageView) findViewById(R.id.trophy3);
        trophies[3] = (ImageView) findViewById(R.id.trophy4);
        trophies[4] = (ImageView) findViewById(R.id.trophy5);
        trophies[5] = (ImageView) findViewById(R.id.trophy6);
        trophies[6] = (ImageView) findViewById(R.id.trophy7);
        trophies[7] = (ImageView) findViewById(R.id.trophy8);
        trophies[8] = (ImageView) findViewById(R.id.trophy9);
        trophies[9] = (ImageView) findViewById(R.id.trophy10);
        trophies[10] = (ImageView) findViewById(R.id.trophy11);
        trophies[11] = (ImageView) findViewById(R.id.trophy12);
        trophies[12] = (ImageView) findViewById(R.id.trophy13);
        trophies[13] = (ImageView) findViewById(R.id.trophy14);
        trophies[14] = (ImageView) findViewById(R.id.trophy15);
        trophies[15] = (ImageView) findViewById(R.id.trophy16);
        trophies[16] = (ImageView) findViewById(R.id.trophy17);
        trophies[17] = (ImageView) findViewById(R.id.trophy18);
        trophies[18] = (ImageView) findViewById(R.id.trophy19);
        trophies[19] = (ImageView) findViewById(R.id.trophy20);
        trophyUsage = (ImageView) findViewById(R.id.trophyUsage);
        trophyEverything = (ImageView) findViewById(R.id.trophyEverything);
        trophySteps = (ImageView) findViewById(R.id.trophySteps);
        trophyFruit = (ImageView) findViewById(R.id.trophyFruit);
        trophyActivity = (ImageView) findViewById(R.id.trophyActive);
        trophyWater = (ImageView) findViewById(R.id.trophyWater);

        trophies[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Use the app for One Day!", Toast.LENGTH_LONG).show();
            }
        });

        if(results.getUsage().equals("B")){
            trophyUsage.setImageResource(R.drawable.bronze_trophy);
        }else if(results.getUsage().equals("S")){
            trophyUsage.setImageResource(R.drawable.silver_trophy);
        }else if(results.getUsage().equals("G")){
            trophyUsage.setImageResource(R.drawable.trophy);
        }else if(results.getUsage().equals("N")){
            trophyUsage.setImageResource(R.drawable.no_trophy);
        }

        if(results.getEverything().equals("B")){
            trophyEverything.setImageResource(R.drawable.bronze_trophy);
        }else if(results.getEverything().equals("S")){
            trophyEverything.setImageResource(R.drawable.silver_trophy);
        }else if(results.getEverything().equals("G")){
            trophyEverything.setImageResource(R.drawable.trophy);
        }else if(results.getEverything().equals("N")){
            trophyEverything.setImageResource(R.drawable.no_trophy);
        }

        if(results.getSteps().equals("B")){
            trophySteps.setImageResource(R.drawable.bronze_trophy);
        }else if(results.getSteps().equals("S")){
            trophySteps.setImageResource(R.drawable.silver_trophy);
        }else if(results.getSteps().equals("G")){
            trophySteps.setImageResource(R.drawable.trophy);
        }else if(results.getSteps().equals("N")){
            trophySteps.setImageResource(R.drawable.no_trophy);
        }

        if(results.getWater().equals("B")){
            trophyWater.setImageResource(R.drawable.bronze_trophy);
        }else if(results.getWater().equals("S")){
            trophyWater.setImageResource(R.drawable.silver_trophy);
        }else if(results.getWater().equals("G")){
            trophyWater.setImageResource(R.drawable.trophy);
        }else if(results.getWater().equals("N")){
            trophyWater.setImageResource(R.drawable.no_trophy);
        }

        if(results.getFruit().equals("B")){
            trophyFruit.setImageResource(R.drawable.bronze_trophy);
        }else if(results.getFruit().equals("S")){
            trophyFruit.setImageResource(R.drawable.silver_trophy);
        }else if(results.getFruit().equals("G")){
            trophyFruit.setImageResource(R.drawable.trophy);
        }else if(results.getFruit().equals("N")){
            trophyFruit.setImageResource(R.drawable.no_trophy);
        }

        if(results.getActive().equals("B")){
            trophyActivity.setImageResource(R.drawable.bronze_trophy);
        }else if(results.getActive().equals("S")){
            trophyActivity.setImageResource(R.drawable.silver_trophy);
        }else if(results.getActive().equals("G")){
            trophyActivity.setImageResource(R.drawable.trophy);
        }else if(results.getActive().equals("N")){
            trophyActivity.setImageResource(R.drawable.no_trophy);
        }

        trophyUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(TrophyActivity.this);
                LayoutInflater inflater = (LayoutInflater) TrophyActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.custom_dialog, null);
                builder.setView(view1);
                TextView title = new TextView(TrophyActivity.this);
                title.setText("Well Done!");
                title.setGravity(Gravity.CENTER);
                title.setTextColor(Color.BLACK);
                title.setTextSize(20);
                title.setPadding(10, 10, 10, 10);
                builder.setCustomTitle(title);
                TextView text1 = (TextView) view1.findViewById(R.id.textAward);
                ImageView imageAward = (ImageView) view1.findViewById(R.id.image);
                Button close = (Button) view1.findViewById(R.id.close);
                final AlertDialog dialog = builder.create();
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });


        }
    }
