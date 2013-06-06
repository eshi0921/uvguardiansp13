package com.example.navidration;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rachel Fang on 6/2/13.
 */
public class menu extends Activity {
    SharedPreferences mSharedPreferences;
    boolean userIDRetrieved = false;
    int userID;
    private class UserIDTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            // update the UI immediately after the task is executed
            super.onPreExecute();
             userIDRetrieved = false;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            userIDRetrieved = true;
        }

        @Override
        protected Void doInBackground(Void... params) {

            URL url = null;
            BufferedReader in = null;
            try
            {
                String line = "";
                String id_url = "http://209.141.35.124/getUserID.php?dbpass=uvguardian13";
                url = new URL(id_url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if(urlConnection!=null){
                    urlConnection.setDoInput(true);
                }
                InputStream is = urlConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                in = new BufferedReader(isr);
                while ((line = in.readLine()) !=null){
                    userID = Integer.valueOf(line);
                    userIDRetrieved = true;
                }

                in.close();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            finally{
                if (in != null){
                    try{
                        in.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        setContentView(R.layout.activity_menu);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userID = new Integer(mSharedPreferences.getString("prefUserID", "-1"));
        if (userID == -1)
        {
            UserIDTask uTask = new UserIDTask();

            uTask.execute();

            while (!userIDRetrieved)
            {
                try {

                        uTask.get(1000, TimeUnit.MILLISECONDS);
                    }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }


            mSharedPreferences.edit().putString("prefUserID", ""+userID).commit();
        }

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
