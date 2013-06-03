package com.example.navidration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by Rachel Fang on 6/2/13.
 */
public class profile extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        //READ PROFILE FROM FILE
        setContentView(R.layout.activity_profile);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void save_profile() {
        String walk;
        String run;
        String jog;
        String weight;

        EditText wEdit = (EditText) findViewById(R.id.editText);
        weight = wEdit.getText().toString();

        RadioGroup walkgrp = (RadioGroup) findViewById(R.id.RG_walking);
        if (walkgrp.getCheckedRadioButtonId() == R.id.Button_walk_easy) {
            walk = "easy";
        }
        else if (walkgrp.getCheckedRadioButtonId()==R.id.Button_walk_med) {
            walk = "medium";
        }
        else {
            walk = "hard";
        }
        RadioGroup joggrp = (RadioGroup) findViewById(R.id.RG_jogging);
        if (joggrp.getCheckedRadioButtonId() == R.id.Button_jog_easy) {
            jog = "easy";
        }
        else if (joggrp.getCheckedRadioButtonId()==R.id.Button_jog_med) {
            jog = "medium";
        }
        else {
            jog = "hard";
        }
        RadioGroup rungrp = (RadioGroup) findViewById(R.id.RG_running);
        if (rungrp.getCheckedRadioButtonId() == R.id.Button_run_easy) {
            jog = "easy";
        }
        else if (rungrp.getCheckedRadioButtonId()==R.id.Button_run_med) {
            jog = "medium";
        }
        else {
            jog = "hard";
        }

        //do something with all of this D: ******************!!!!!!!

        Intent intent = new Intent(this, menu.class);
        startActivity(intent);
    }

}