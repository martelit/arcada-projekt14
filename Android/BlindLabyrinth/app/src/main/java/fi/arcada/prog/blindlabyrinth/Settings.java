package fi.arcada.prog.blindlabyrinth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class Settings extends Activity {
    public static final String PREFS_NAME = "blindLabyrinthPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        int size = prefs.getInt("size", 2131296273);
        int gameMode = prefs.getInt("gameMode", 2131296271);
        int ball = prefs.getInt("ball", 2131296278);

        RadioButton rb1 = (RadioButton) findViewById(size);
        rb1.setChecked(true);

        rb1 = (RadioButton) findViewById(gameMode);
        rb1.setChecked(true);

        rb1 = (RadioButton) findViewById(ball);
        rb1.setChecked(true);


        RadioGroup rdGroup = (RadioGroup) findViewById(R.id.gameMode);
        rdGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("gameMode", checkedId);
                editor.commit(); //important, otherwise it wouldn't save.
            }
        });


        RadioGroup rdGroup2 = (RadioGroup) findViewById(R.id.size);
        rdGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("size", checkedId);
                editor.commit(); //important, otherwise it wouldn't save.
            }
        });


        RadioGroup rdGroup3 = (RadioGroup) findViewById(R.id.ball);
        rdGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                System.out.println(checkedId);
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("ball", checkedId);
                editor.commit(); //important, otherwise it wouldn't save.
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
