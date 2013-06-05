package com.example.navidration;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Rachel Fang on 6/2/13.
 */
public class profile extends Activity {

    int walk;
    int run;
    int jog;
    String weight;
    EditText wEdit;
    RadioGroup walkgrp, rungrp, joggrp;
    SharedPreferences mSharedPreferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_profile);
        rungrp = (RadioGroup)findViewById(R.id.RG_running);
        joggrp = (RadioGroup)findViewById(R.id.RG_jogging);
        walkgrp = (RadioGroup)findViewById(R.id.RG_walking);
        wEdit = (EditText)findViewById(R.id.editText);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        weight = mSharedPreferences.getString("setWeight","100");
        walk = new Integer(mSharedPreferences.getString("prefWalking", ""+R.id.Button_walk_easy));
        jog = new Integer(mSharedPreferences.getString("prefJogging", ""+R.id.Button_jog_med));
        run = new Integer(mSharedPreferences.getString("prefRunning", ""+R.id.Button_run_hard));
        wEdit.setText(weight);;
        walkgrp.check(walk);
        rungrp.check(run);
        joggrp.check(jog);



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

    public void save_profile(View view) {
        weight = wEdit.getText().toString();
        walk = walkgrp.getCheckedRadioButtonId();
        jog = joggrp.getCheckedRadioButtonId();
        run = rungrp.getCheckedRadioButtonId();

        String walkDiff, jogDiff, runDiff;

        if (walk == R.id.Button_walk_easy)
            walkDiff = "1";
        else if (walk == R.id.Button_walk_med)
            walkDiff = "2";
        else
            walkDiff = "3";

        if (jog == R.id.Button_jog_easy)
            jogDiff = "1";
        else if (jog == R.id.Button_jog_med)
            jogDiff = "2";
        else
            jogDiff = "3";

        if (run == R.id.Button_run_easy)
            runDiff = "1";
        else if (run == R.id.Button_run_med)
            runDiff = "2";
        else
            runDiff = "3";

        mSharedPreferences.edit().putString("setWeight", weight).commit();
        mSharedPreferences.edit().putString("prefWalking", ""+walk).commit();
        mSharedPreferences.edit().putString("prefWalkingValue", ""+walkDiff).commit();
        mSharedPreferences.edit().putString("prefJogging", ""+jog).commit();
        mSharedPreferences.edit().putString("prefJoggingValue", ""+jogDiff).commit();
        mSharedPreferences.edit().putString("prefRunning", ""+run).commit();
        mSharedPreferences.edit().putString("prefRunningValue",""+runDiff).commit();

        //do something with all of this D: ******************!!!!!!!
    }

}