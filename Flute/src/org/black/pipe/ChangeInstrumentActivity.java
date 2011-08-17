package org.black.pipe;

import org.black.R;

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

public class ChangeInstrumentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        String[] instruments = { this.getString(R.string.INSTRUMENT_PICCOLO),
                this.getString(R.string.INSTRUMENT_FLUTE),
                this.getString(R.string.INSTRUMENT_RECORDER),
                this.getString(R.string.INSTRUMENT_OCARINA) };

        ListView listView = new ListView(this);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, instruments);

        SharedPreferences sharedPreferences = getSharedPreferences(
                PipeConstant.SHARED_PERFERENCE, Context.MODE_PRIVATE);
        int instrumentNumber = sharedPreferences.getInt(
                PipeConstant.INSTRUMENT_NUMBER,
                PipeConstant.DEFAULT_MIDI_PIPE_INSTRUMENT_NUMBERT);

        int checkedPosition = 0;
        if (instrumentNumber == PipeConstant.MIDI_PIPE_INSTRUMENT_NUMBERT[0]) {
            checkedPosition = 0;
        } else if (instrumentNumber == PipeConstant.MIDI_PIPE_INSTRUMENT_NUMBERT[1]) {
            checkedPosition = 1;
        } else if (instrumentNumber == PipeConstant.MIDI_PIPE_INSTRUMENT_NUMBERT[2]) {
            checkedPosition = 2;
        } else if (instrumentNumber == PipeConstant.MIDI_PIPE_INSTRUMENT_NUMBERT[3]) {
            checkedPosition = 3;
        } else {
            checkedPosition = 1;
        }

        listView.setAdapter(adapter);

        listView.setItemChecked(checkedPosition, true);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                int instrumentNumber = PipeConstant.MIDI_PIPE_INSTRUMENT_NUMBERT[position];

                SharedPreferences sharedPreferences = getSharedPreferences(
                        PipeConstant.SHARED_PERFERENCE, Context.MODE_PRIVATE);
                sharedPreferences.edit().putInt(PipeConstant.INSTRUMENT_NUMBER,
                        instrumentNumber).commit();
                finish();
            }
        });

        setContentView(listView);
    }

}
