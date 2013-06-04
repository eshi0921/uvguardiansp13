package com.example.navidration;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.support.v4.app.NavUtils;
/**
 * Created by Rachel Fang on 6/2/13.
 */
public class menu extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        setContentView(R.layout.activity_menu);
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

    public void load_profile(View view)
    {
        Intent intent = new Intent(this, profile.class);
        startActivity(intent);
    }

    public void load_map(View view)
    {
        Intent intent = new Intent(this, MapTest.class);
        startActivity(intent);
    }

    public void load_tracking(View view)
    {
        Intent intent = new Intent(this, tracking.class);
        startActivity(intent);
    }
}
