package com.example.navidration;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

/**
 * Created by Rachel Fang on 6/2/13.
 */
public class tracking extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
    }


    public void tracking_clicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            //RUNNING
        } else {
            //NOT RUNNING
        }
    }
}