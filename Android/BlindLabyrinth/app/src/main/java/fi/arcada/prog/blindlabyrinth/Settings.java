package fi.arcada.prog.blindlabyrinth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


public class Settings extends GameActivity {
    public static final String PREFS_NAME = "blindLabyrinthPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        String size = prefs.getString("size", "small");
        String gameMode = prefs.getString("gameMode", "lights_on");
        String ball = prefs.getString("ball", "ball1");

        int resID = getResources().getIdentifier(size, "id", "fi.arcada.prog.blindlabyrinth");
        RadioButton rb1 = (RadioButton) findViewById(resID);
        rb1.setChecked(true);

        resID = getResources().getIdentifier(gameMode, "id", "fi.arcada.prog.blindlabyrinth");
        rb1 = (RadioButton) findViewById(resID);
        rb1.setChecked(true);

        resID = getResources().getIdentifier(ball, "id", "fi.arcada.prog.blindlabyrinth");
        rb1 = (RadioButton) findViewById(resID);
        rb1.setChecked(true);


        RadioGroup rdGroup = (RadioGroup) findViewById(R.id.gameMode);
        rdGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb1 = (RadioButton) findViewById(checkedId);
                String IdAsString = rb1.getResources().getResourceName(rb1.getId());
                IdAsString = IdAsString.split("/")[1];
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("gameMode", IdAsString);
                editor.commit(); //important, otherwise it wouldn't save.
                TextView tv1 = (TextView) findViewById(R.id.saveMessage);
                tv1.setText("Game mode change saved");
            }
        });


        RadioGroup rdGroup2 = (RadioGroup) findViewById(R.id.size);
        rdGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb1 = (RadioButton) findViewById(checkedId);
                String IdAsString = rb1.getResources().getResourceName(rb1.getId());
                IdAsString = IdAsString.split("/")[1];
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("size", IdAsString);
                editor.commit(); //important, otherwise it wouldn't save.
                TextView tv1 = (TextView) findViewById(R.id.saveMessage);
                tv1.setText("Size change saved");
            }
        });


        RadioGroup rdGroup3 = (RadioGroup) findViewById(R.id.ball);
        rdGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb1 = (RadioButton) findViewById(checkedId);
                String IdAsString = rb1.getResources().getResourceName(rb1.getId());
                IdAsString = IdAsString.split("/")[1];
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("ball", IdAsString);
                editor.commit(); //important, otherwise it wouldn't save.
                TextView tv1 = (TextView) findViewById(R.id.saveMessage);
                tv1.setText("Ball change saved");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
