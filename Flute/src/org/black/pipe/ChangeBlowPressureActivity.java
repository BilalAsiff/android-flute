package org.black.pipe;

import org.black.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Activity to save blow pressure.
 * 
 * @author black
 * 
 */
public class ChangeBlowPressureActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        String[] blowPressures = { this.getString(R.string.BLOW_PRESSURE_HIGH),
                this.getString(R.string.BLOW_PRESSURE_MIDDLE),
                this.getString(R.string.BLOW_PRESSURE_LOW) };

        // To create a list view with radio button function.
        ListView listView = new ListView(this);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, blowPressures);

        SharedPreferences sharedPreferences = getSharedPreferences(
                PipeConstant.SHARED_PERFERENCE, Context.MODE_PRIVATE);
        // Retrieve hole number
        int blowPressure = sharedPreferences.getInt(
                PipeConstant.BLOW_PRESSURE_THRESHOLD,
                PipeConstant.DEFAULT_MIN_BLOW_PRESSURE);
        Log.i(PipeConstant.APP_TAG, "Blow pressure: " + blowPressure);

        int checkedPosition = 1;
        if (blowPressure == PipeConstant.BLOW_PRESSURES[0]) {
            checkedPosition = 0;
        } else if (blowPressure == PipeConstant.BLOW_PRESSURES[1]) {
            checkedPosition = 1;
        } else if (blowPressure == PipeConstant.BLOW_PRESSURES[2]) {
            checkedPosition = 2;
        } else {
            checkedPosition = 1;
        }

        listView.setAdapter(adapter);

        listView.setItemChecked(checkedPosition, true);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                int blowPressure = PipeConstant.BLOW_PRESSURES[position];

                SharedPreferences sharedPreferences = getSharedPreferences(
                        PipeConstant.SHARED_PERFERENCE, Context.MODE_PRIVATE);
                sharedPreferences
                        .edit()
                        .putInt(PipeConstant.BLOW_PRESSURE_THRESHOLD,
                                blowPressure).commit();
                Log.d(PipeConstant.APP_TAG, "Blow pressure switch to :"
                        + blowPressure);
                finish();
            }
        });

        setContentView(listView);
    }

}
