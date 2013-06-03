package com.example.navidration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Byte on 5/31/13.
 */
public class map extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }

    public void load_menu(View view)
    {
        Intent intent = new Intent(this, menu.class);
        startActivity(intent);
    }
}