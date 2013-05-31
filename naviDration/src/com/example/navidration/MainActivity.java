package com.example.navidration;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import android.opengl.*;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.*;
import com.google.android.maps.*;

public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGravity;
    private Sensor mMagnetic;
    TextView title,tv1,tv2, tv3, tv4;

    private FileWriter writer;

    float[] gravity = null;
    float[] magnet = null;
    float[] inR = new float[16];
    float[] outR = new float[16];
    float[] output = new float[4];
    float[] linAcceleration = {0,0,0,0};
    private float localMin = 0;
    private float localMax = 0;
    private float localMean = 0;
    private float threshold = 0;
    private int step = 0;

    private static int DEFAULT_WALKING_MAX_STEPS = 200;
    private static int DEFAULT_IDLE = 0;
    private static int DEFAULT_JOGGING_MAX_STEPS = 300;
    private LinkedList<Long> step_timestamps = new LinkedList<Long>();

    private static int IDLE_MODE = 90009;
    private static int WALKING_MODE = 10001;
    private static int JOGGING_MODE = 20002;
    private static int RUNNING_MODE = 30003;
    private int CURRENT_MODE = IDLE_MODE;
    private long prev_timestamp = -1;

    String weatherData;
    DecimalFormat dForm = new DecimalFormat("#.###");
    boolean weatherDataRetrieved;

	  private class WeatherAsyncTask extends AsyncTask<Void, Integer, Void>{
	      
			@Override
			protected void onPreExecute() {
			// update the UI immediately after the task is executed
				super.onPreExecute();
                weatherDataRetrieved = false;

			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
                weatherDataRetrieved = true;
			}

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
				  Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				  double longitude = Double.valueOf(dForm.format(location.getLongitude()));
				  double latitude = Double.valueOf(dForm.format(location.getLatitude()));
				  URL url = null;
				  String data= "";
				  BufferedReader in = null;
				  try
			        {
					  	String l = "";
					  	String output = "";
					  	String api_url = "http://api.worldweatheronline.com/free/v1/weather.ashx?key=324eefpxmgz3ww7c6tga4svd&num_of_days=1&q="+latitude+","+longitude+"&format=json";
					  	url = new URL(api_url);
					  	HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					  	if(urlConnection!=null){
					  		urlConnection.setDoInput(true);
					  		}

					  	InputStream is = urlConnection.getInputStream();
					  	InputStreamReader isr = new InputStreamReader(is);
			            in = new BufferedReader(isr);
			            while ((l = in.readLine()) !=null){

			                JSONObject weather_json = new JSONObject(l);
			                
			                String tempF = weather_json.getJSONObject("data").getJSONArray("current_condition").getJSONObject(0).getString("temp_F");
			                String humidity = weather_json.getJSONObject("data").getJSONArray("current_condition").getJSONObject(0).getString("humidity");
			                weatherData = tempF+","+humidity;
			            }
                        weatherDataRetrieved = true;
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
	  public final void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGravity= mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
	    mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

	    //get layout
	    WeatherAsyncTask wTask = new WeatherAsyncTask();

	    wTask.execute();
        while (!weatherDataRetrieved)
          {
            try
            {
            wTask.get(1000, TimeUnit.MILLISECONDS);
            }
            catch (Exception ex)
            {

            }
          }

	    //get textviews
	    title=(TextView)findViewById(R.id.name);
	    tv3=(TextView)findViewById(R.id.weatherval);
        tv4 = (TextView)findViewById(R.id.stepcount);
        tv1 =(TextView)findViewById(R.id.activity);
        tv2 =(TextView)findViewById(R.id.stepsmin);
        title.setText(R.string.app_name);
	    tv3.setText(weatherData);

	    
	  }

	  @Override
	  public final void onAccuracyChanged(Sensor sensor, int accuracy)
	  {
	    // Do something here if sensor accuracy changes.
	  }

	  @Override
	  public final void onSensorChanged(SensorEvent event) 
	  {
          long currentTimestamp = System.currentTimeMillis();
          if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
              gravity = event.values;
          }
          if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
              magnet = event.values;
          }
          if (gravity != null && magnet != null && event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
              processAccelerationData(event.values, currentTimestamp);
          }
        /*
	    try
	    {
	    	writer.write(""+x+","+y+","+z);
	    	writer.append(System.getProperty("line.separator"));
	    }
	    catch (Exception ex)
	    {
	    }
	    */
	    
	  }

    protected void processAccelerationData(float[] data, long currentTimeStamp)
    {

        linAcceleration[0]= data[0];
        linAcceleration[1] = data[1];
        linAcceleration[2] = data[2];
        SensorManager.getRotationMatrix(inR, null, gravity, magnet);
        android.opengl.Matrix.invertM(outR, 0, inR, 0);
        if (0.9 < outR[6] && outR[6] < 1.1 ) {
            return;
        }
        float[] result = new float[4];
        android.opengl.Matrix.multiplyMV(result, 0, outR, 0, linAcceleration, 0);
        float xyAcceleration = android.util.FloatMath.sqrt(result[0]*result[0] + result[1]*result[1]);
        boolean stepCounted = false;
        if (xyAcceleration > 1.3) {
            if (localMax < result[2]) {
                localMax = result[2];
            }
            if (localMin > result[2] && result[2] < localMax) {
                localMin = result[2];
            }
            localMean = (localMax + localMin) / 2;
            if (localMin <= localMean && localMean <= localMax) {
                threshold++;
                localMax = 0;
                localMin = 0;

            }
            if (threshold > 50) {
                threshold = 0;
                step++;
                stepCounted = true;
                step_timestamps.add(currentTimeStamp);

            }
        }

        //display values using TextView

        //tv.setText("X axis" +"\t\t"+linAcceleration[0]);
        //tv1.setText("Y axis" + "\t\t" +linAcceleration[1]);
        //tv2.setText("Z axis" +"\t\t" +linAcceleration[2]);


        if (prev_timestamp != -1 && currentTimeStamp-prev_timestamp > 1000)
        {
            long iterator = step_timestamps.getFirst();
            while (currentTimeStamp-60000>iterator)
            {
                step_timestamps.removeFirst();
                iterator = step_timestamps.getFirst();
            }

            int num_steps_last_minute = step_timestamps.size();

            if (num_steps_last_minute == DEFAULT_IDLE)
                CURRENT_MODE = IDLE_MODE;
            else if (num_steps_last_minute > DEFAULT_IDLE && num_steps_last_minute <= DEFAULT_WALKING_MAX_STEPS)
                CURRENT_MODE = WALKING_MODE;
            else if (num_steps_last_minute > DEFAULT_WALKING_MAX_STEPS && num_steps_last_minute <= DEFAULT_JOGGING_MAX_STEPS)
                CURRENT_MODE = JOGGING_MODE;
            else if (num_steps_last_minute > DEFAULT_JOGGING_MAX_STEPS)
                CURRENT_MODE = RUNNING_MODE;

        }
        if (stepCounted)
            prev_timestamp = currentTimeStamp;
        String current_activity = CURRENT_MODE == IDLE_MODE ? "Idle" : CURRENT_MODE == WALKING_MODE ? "Walking" : CURRENT_MODE == JOGGING_MODE ? "Jogging" : "Running";
        tv1.setText(current_activity);
        tv4.setText("Step count " +"\t\t" +step);
    }

	  @Override
	  protected void onResume()
	  {
	    super.onResume();
	    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mMagnetic, SensorManager.SENSOR_DELAY_FASTEST);
          /*
	    try
	    {
	    	File writeTo = new File(android.os.Environment.getExternalStorageDirectory(), "accelerometer_data.txt");
	    	if (!writeTo.exists())
	    		writeTo.createNewFile();
	    	writer = new FileWriter(writeTo,true);
	    }
	    catch (Exception ex)
	    {
	    }*/
	    
	  }

	  @Override
	  protected void onPause() {
	    super.onPause();
	    mSensorManager.unregisterListener(this);
	    if(writer != null)
	    {
	    	try
	    	{
	    			writer.flush();
	    			writer.close();
	    	}
	    	catch (Exception ex)
	    	{
	    	}
	    }
	  }
	  
	  
	}