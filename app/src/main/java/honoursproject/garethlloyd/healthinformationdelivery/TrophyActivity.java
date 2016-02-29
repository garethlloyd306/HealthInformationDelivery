package honoursproject.garethlloyd.healthinformationdelivery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
        trophyUsage = (ImageView) findViewById(R.id.trophyUsage);
        trophyEverything = (ImageView) findViewById(R.id.trophyEverything);
        trophySteps = (ImageView) findViewById(R.id.trophySteps);
        trophyFruit = (ImageView) findViewById(R.id.trophyFruit);
        trophyActivity = (ImageView) findViewById(R.id.trophyActive);
        trophyWater = (ImageView) findViewById(R.id.trophyWater);

        if(results.getUsage().equals("S")){
            trophies[2].setImageDrawable(null);
        }else if (results.getUsage().equals("B")){
            trophies[1].setImageDrawable(null);
            trophies[2].setImageDrawable(null);
        }else if(results.getUsage().equals("N")){
            trophies[0].setImageDrawable(null);
            trophies[1].setImageDrawable(null);
            trophies[2].setImageDrawable(null);
        }

        if(results.getEverything().equals("S")){
            trophies[5].setImageDrawable(null);
        }else if (results.getEverything().equals("B")){
            trophies[4].setImageDrawable(null);
            trophies[5].setImageDrawable(null);
        }else if(results.getEverything().equals("N")){
            trophies[3].setImageDrawable(null);
            trophies[4].setImageDrawable(null);
            trophies[5].setImageDrawable(null);
        }

        if(results.getSteps().equals("S")){
            trophies[8].setImageDrawable(null);
        }else if (results.getSteps().equals("B")){
            trophies[7].setImageDrawable(null);
            trophies[8].setImageDrawable(null);
        }else if(results.getSteps().equals("N")){
            trophies[6].setImageDrawable(null);
            trophies[7].setImageDrawable(null);
            trophies[8].setImageDrawable(null);
        }

        if(results.getFruit().equals("S")){
            trophies[11].setImageDrawable(null);
        }else if (results.getFruit().equals("B")){
            trophies[10].setImageDrawable(null);
            trophies[11].setImageDrawable(null);
        }else if(results.getFruit().equals("N")){
            trophies[9].setImageDrawable(null);
            trophies[10].setImageDrawable(null);
            trophies[11].setImageDrawable(null);
        }

        if(results.getWater().equals("S")){
            trophies[14].setImageDrawable(null);
        }else if (results.getWater().equals("B")){
            trophies[13].setImageDrawable(null);
            trophies[14].setImageDrawable(null);
        }else if(results.getWater().equals("N")){
            trophies[12].setImageDrawable(null);
            trophies[13].setImageDrawable(null);
            trophies[14].setImageDrawable(null);
        }

        if(results.getActive().equals("S")){
            trophies[17].setImageDrawable(null);
        }else if (results.getActive().equals("B")){
            trophies[16].setImageDrawable(null);
            trophies[17].setImageDrawable(null);
        }else if(results.getActive().equals("N")){
            trophies[15].setImageDrawable(null);
            trophies[16].setImageDrawable(null);
            trophies[17].setImageDrawable(null);
        }

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

        final AlertDialog.Builder builder = new AlertDialog.Builder(TrophyActivity.this);
        LayoutInflater inflater = (LayoutInflater) TrophyActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.custom_dialog_trophy, null);
        builder.setView(view1);
        TextView title = new TextView(TrophyActivity.this);
        title.setText("Well Done!");
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        title.setPadding(10, 10, 10, 10);
        builder.setCustomTitle(title);
        final TextView textTop = (TextView) view1.findViewById(R.id.textTop);
        final TextView textEarned = (TextView) view1.findViewById(R.id.textAward);
       final ImageView imageAward = (ImageView) view1.findViewById(R.id.image);
        Button close = (Button) view1.findViewById(R.id.close);
        final AlertDialog dialog = builder.create();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        trophyUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(results.getUsage().equals("N")){
                    textTop.setText("No Award Yet!");
                    textEarned.setText("Use the app for two days in a row!");
                    imageAward.setImageResource(R.drawable.no_trophy);
                    dialog.show();
                }else if(results.getUsage().equals("B")){
                    textTop.setText("Bronze Trophy!");
                    textEarned.setText("Use the app for 7 days in a row!");
                    imageAward.setImageResource(R.drawable.bronze_trophy);
                    dialog.show();
                }else if(results.getUsage().equals("S")){
                    textTop.setText("Silver Trophy!");
                    textEarned.setText("Use the app for 14 days in a row!");
                    imageAward.setImageResource(R.drawable.silver_trophy);
                    dialog.show();
                }else if(results.getUsage().equals("G")){
                    textTop.setText("Gold Trophy!");
                    textEarned.setText("You have earned all trophies!");
                    imageAward.setImageResource(R.drawable.trophy);
                    dialog.show();
                }

            }
        });

        trophyEverything.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(results.getEverything().equals("N")){
                    textTop.setText("No Award Yet!");
                    textEarned.setText("Gain at least a bronze star in every area for a whole week!");
                    imageAward.setImageResource(R.drawable.no_trophy);
                    dialog.show();
                }else if(results.getEverything().equals("B")){
                    textTop.setText("Bronze Trophy!");
                    textEarned.setText("Gain at least a silver star in every area for a whole week!");
                    imageAward.setImageResource(R.drawable.bronze_trophy);
                    dialog.show();
                }else if(results.getEverything().equals("S")){
                    textTop.setText("Silver Trophy!");
                    textEarned.setText("Gain at least a gold star in every area for a whole week!");
                    imageAward.setImageResource(R.drawable.silver_trophy);
                    dialog.show();
                }else if(results.getEverything().equals("G")){
                    textTop.setText("Gold Trophy!");
                    textEarned.setText("You have earned all trophies!");
                    imageAward.setImageResource(R.drawable.trophy);
                    dialog.show();
                }

            }
        });

        trophySteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(results.getSteps().equals("N")){
                    textTop.setText("No Award Yet!");
                    textEarned.setText("Gain at least a bronze star in steps for a whole week!");
                    imageAward.setImageResource(R.drawable.no_trophy);
                    dialog.show();
                }else if(results.getSteps().equals("B")){
                    textTop.setText("Bronze Trophy!");
                    textEarned.setText("Gain at least a silver star in steps for a whole week!");
                    imageAward.setImageResource(R.drawable.bronze_trophy);
                    dialog.show();
                }else if(results.getSteps().equals("S")){
                    textTop.setText("Silver Trophy!");
                    textEarned.setText("Gain at least a gold star in steps for a whole week!");
                    imageAward.setImageResource(R.drawable.silver_trophy);
                    dialog.show();
                }else if(results.getSteps().equals("G")){
                    textTop.setText("Gold Trophy!");
                    textEarned.setText("You have earned all trophies!");
                    imageAward.setImageResource(R.drawable.trophy);
                    dialog.show();
                }

            }
        });

        trophyActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(results.getActive().equals("N")){
                    textTop.setText("No Award Yet!");
                    textEarned.setText("Gain at least a bronze star in active time for a whole week!");
                    imageAward.setImageResource(R.drawable.no_trophy);
                    dialog.show();
                }else if(results.getActive().equals("B")){
                    textTop.setText("Bronze Trophy!");
                    textEarned.setText("Gain at least a silver star in active time for a whole week!");
                    imageAward.setImageResource(R.drawable.bronze_trophy);
                    dialog.show();
                }else if(results.getActive().equals("S")){
                    textTop.setText("Silver Trophy!");
                    textEarned.setText("Gain at least a gold star in active time for a whole week!");
                    imageAward.setImageResource(R.drawable.silver_trophy);
                    dialog.show();
                }else if(results.getActive().equals("G")){
                    textTop.setText("Gold Trophy!");
                    textEarned.setText("You have earned all trophies!");
                    imageAward.setImageResource(R.drawable.trophy);
                    dialog.show();
                }

            }
        });

        trophyFruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(results.getFruit().equals("N")){
                    textTop.setText("No Award Yet!");
                    textEarned.setText("Gain at least a bronze star in fruit and vegetables for a whole week!");
                    imageAward.setImageResource(R.drawable.no_trophy);
                    dialog.show();
                }else if(results.getFruit().equals("B")){
                    textTop.setText("Bronze Trophy!");
                    textEarned.setText("Gain at least a silver star in fruit and vegetables for a whole week!");
                    imageAward.setImageResource(R.drawable.bronze_trophy);
                    dialog.show();
                }else if(results.getFruit().equals("S")){
                    textTop.setText("Silver Trophy!");
                    textEarned.setText("Gain at least a gold star in fruit and vegetables for a whole week!");
                    imageAward.setImageResource(R.drawable.silver_trophy);
                    dialog.show();
                }else if(results.getFruit().equals("G")){
                    textTop.setText("Gold Trophy!");
                    textEarned.setText("You have earned all trophies!");
                    imageAward.setImageResource(R.drawable.trophy);
                    dialog.show();
                }

            }
        });

        trophyWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(results.getWater().equals("N")){
                    textTop.setText("No Award Yet!");
                    textEarned.setText("Gain at least a bronze star in water for a whole week!");
                    imageAward.setImageResource(R.drawable.no_trophy);
                    dialog.show();
                }else if(results.getWater().equals("B")){
                    textTop.setText("Bronze Trophy!");
                    textEarned.setText("Gain at least a silver star in water for a whole week!");
                    imageAward.setImageResource(R.drawable.bronze_trophy);
                    dialog.show();
                }else if(results.getWater().equals("S")){
                    textTop.setText("Silver Trophy!");
                    textEarned.setText("Gain at least a gold star in water for a whole week!");
                    imageAward.setImageResource(R.drawable.silver_trophy);
                    dialog.show();
                }else if(results.getWater().equals("G")){
                    textTop.setText("Gold Trophy!");
                    textEarned.setText("You have earned all trophies!");
                    imageAward.setImageResource(R.drawable.trophy);
                    dialog.show();
                }

            }
        });

        Button home = (Button) findViewById(R.id.buttonHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrophyActivity.this, MainActivity.class);
                TrophyActivity.this.startActivity(intent);
            }
        });




        }
    }
