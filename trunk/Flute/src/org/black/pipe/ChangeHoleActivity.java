package org.black.pipe;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Activity to save hole number.
 * @author black
 *
 */
public class ChangeHoleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Integer[] holes = PipeConstant.INSTRUMENT_HOLE_NUMBER;

        // To create a list view with radio button selected function.
        ListView listView = new ListView(this);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,
                android.R.layout.simple_list_item_single_choice, holes);

        SharedPreferences sharedPreferences = getSharedPreferences(
                PipeConstant.SHARED_PERFERENCE, Context.MODE_PRIVATE);
        int holeNumber = sharedPreferences.getInt(
                PipeConstant.HOLE_NUMBER,
                PipeConstant.DEFAULT_INSTRUMENT_HOLE_NUMBER);

        int checkedPosition = 0;
        if (holeNumber == PipeConstant.INSTRUMENT_HOLE_NUMBER[0]) {
            checkedPosition = 0;
        } else if (holeNumber == PipeConstant.INSTRUMENT_HOLE_NUMBER[1]) {
            checkedPosition = 1;
        } else {
            checkedPosition = 0;
        }

        listView.setAdapter(adapter);

        listView.setItemChecked(checkedPosition, true);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                int holeNumber = PipeConstant.INSTRUMENT_HOLE_NUMBER[position];

                SharedPreferences sharedPreferences = getSharedPreferences(
                        PipeConstant.SHARED_PERFERENCE, Context.MODE_PRIVATE);
                sharedPreferences
                        .edit()
                        .putInt(PipeConstant.HOLE_NUMBER,
                                holeNumber).commit();
                finish();
            }
        });

        setContentView(listView);
    }

}
