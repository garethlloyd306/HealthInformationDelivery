package honoursproject.garethlloyd.healthinformationdelivery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class TrophyActivity extends AppCompatActivity {

    DatabaseHandler db;
    ImageView[] trophies;
    TrophyModel results;

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

        if (results.getOne() == 0) {
            trophies[0].setVisibility(View.INVISIBLE);
        } else {
            trophies[0].setVisibility(View.VISIBLE);
        }
        if (results.getTwo() == 0) {
            trophies[1].setVisibility(View.INVISIBLE);
        } else {
            trophies[1].setVisibility(View.VISIBLE);
        }

        if (results.getWeek() == 0) {
            trophies[2].setVisibility(View.INVISIBLE);
        } else {
            trophies[2].setVisibility(View.VISIBLE);
        }

        if (results.getMonth() == 0) {
            trophies[3].setVisibility(View.INVISIBLE);
        } else {
            trophies[3].setVisibility(View.VISIBLE);
        }

        if (results.getgOne() == 0) {
            trophies[4].setVisibility(View.INVISIBLE);
        } else {
            trophies[4].setVisibility(View.VISIBLE);
        }

        if (results.getgTwo() == 0) {
            trophies[5].setVisibility(View.INVISIBLE);
        } else {
            trophies[5].setVisibility(View.VISIBLE);
        }

        if (results.getgWeek() == 0) {
            trophies[6].setVisibility(View.INVISIBLE);
        } else {
            trophies[6].setVisibility(View.VISIBLE);
        }

        if (results.getgMonth() == 0) {
            trophies[7].setVisibility(View.INVISIBLE);
        } else {
            trophies[7].setVisibility(View.VISIBLE);
        }

        if (results.getgSteps() == 0) {
            trophies[8].setVisibility(View.INVISIBLE);
        } else {
            trophies[8].setVisibility(View.VISIBLE);
        }

        if (results.getsSteps() == 0) {
            trophies[9].setVisibility(View.INVISIBLE);
        } else {
            trophies[9].setVisibility(View.VISIBLE);
        }

        if (results.getbSteps() == 0) {
            trophies[10].setVisibility(View.INVISIBLE);
        } else {
            trophies[10].setVisibility(View.VISIBLE);
        }
        if (results.getgWater() == 0) {
            trophies[11].setVisibility(View.INVISIBLE);
        } else {
            trophies[11].setVisibility(View.VISIBLE);
        }

        if (results.getsWater() == 0) {
            trophies[12].setVisibility(View.INVISIBLE);
        } else {
            trophies[12].setVisibility(View.VISIBLE);
        }

        if (results.getbWater() == 0) {
            trophies[13].setVisibility(View.INVISIBLE);
        } else {
            trophies[13].setVisibility(View.VISIBLE);
        }

        if (results.getgActive() == 0) {
            trophies[14].setVisibility(View.INVISIBLE);
        } else {
            trophies[14].setVisibility(View.VISIBLE);
        }

        if (results.getsSteps() == 0) {
            trophies[15].setVisibility(View.INVISIBLE);
        } else {
            trophies[15].setVisibility(View.VISIBLE);
        }

        if (results.getbSteps() == 0) {
            trophies[16].setVisibility(View.INVISIBLE);
        } else {
            trophies[16].setVisibility(View.VISIBLE);
        }

        if (results.getgFruit() == 0) {
            trophies[17].setVisibility(View.INVISIBLE);
        } else {
            trophies[17].setVisibility(View.VISIBLE);
        }

        if (results.getsFruit() == 0) {
            trophies[18].setVisibility(View.INVISIBLE);
        } else {
            trophies[18].setVisibility(View.VISIBLE);
        }

        if (results.getbFruit() == 0) {
            trophies[19].setVisibility(View.INVISIBLE);
        } else {
            trophies[19].setVisibility(View.VISIBLE);
        }

        trophies[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Use the app for One Day!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
