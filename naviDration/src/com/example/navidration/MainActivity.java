package com.example.navidration;

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
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.*;
import com.google.android.maps.*;


public class MainActivity extends MapActivity implements SensorEventListener {
	  private SensorManager mSensorManager;
	  private Sensor mAccelerometer;
	  TextView title,tv,tv1,tv2, tv3;
	  RelativeLayout layout;
	  private FileWriter writer;
	  private List<Overlay> mapOverlays;
	  private MapView mapView;
	  private Projection projection;  
	  String weatherData;
	  DecimalFormat dForm;

	  private class WeatherAsyncTask extends AsyncTask<Void, Integer, Void>{
	      
			@Override
			protected void onPreExecute() {
			// update the UI immediately after the task is executed
				super.onPreExecute();

			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
			}

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
				  Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				  double longitude = Double.valueOf(dForm.format(location.getLongitude()));
				  double latitude = Double.valueOf(dForm.format(location.getLatitude()));
				  System.out.println("Location: " +longitude+" "+latitude);
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
			                
			                String tempC = weather_json.getJSONObject("data").getJSONArray("current_condition").getJSONObject(0).getString("temp_C");
			                String humidity = weather_json.getJSONObject("data").getJSONArray("current_condition").getJSONObject(0).getString("humidity");
			                System.out.println("Weather tempC: "+tempC);
			                System.out.println("Weather humidity: "+humidity);
			                weatherData = tempC+","+humidity;
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
	  public final void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    dForm = new DecimalFormat("#.###");
	    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

	    
	    //get layout
	    layout = (RelativeLayout)findViewById(R.id.relative);
	    mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);

	    mapOverlays = mapView.getOverlays();        
	    projection = mapView.getProjection();
	    mapOverlays.add(new MyOverlay());    
	    WeatherAsyncTask task = new WeatherAsyncTask();
	    task.execute();
	    try
	    {
	    task.get(1000, TimeUnit.MILLISECONDS);
	    }
	    catch (Exception ex)
	    {
	    	
	    }
	    System.out.println("WeatherData: "+weatherData);
	    //get textviews
	    title=(TextView)findViewById(R.id.name);   
	    tv=(TextView)findViewById(R.id.xval);
	    tv1=(TextView)findViewById(R.id.yval);
	    tv2=(TextView)findViewById(R.id.zval);
	    tv3=(TextView)findViewById(R.id.weatherval);
	    tv3.setText(weatherData);
	    Timer timer = new Timer();	
	    
	  }

	  @Override
		protected boolean isRouteDisplayed() {
		    return false;
		}
	 

		class MyOverlay extends Overlay{

		    public MyOverlay(){

		    }   

		    public void draw(Canvas canvas, MapView mapv, boolean shadow){
		        super.draw(canvas, mapv, shadow);

		        Paint   mPaint = new Paint();
		        mPaint.setDither(true);
		        mPaint.setColor(Color.RED);
		        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		        mPaint.setStrokeJoin(Paint.Join.ROUND);
		        mPaint.setStrokeCap(Paint.Cap.ROUND);
		        mPaint.setStrokeWidth(2);

		        GeoPoint gP1 = new GeoPoint(34059699,-118444934);
		        GeoPoint gP2 = new GeoPoint(34064641, -118445406);

		        Point p1 = new Point();
		        Point p2 = new Point();
		        Path path = new Path();

		        projection.toPixels(gP1, p1);
		        projection.toPixels(gP2, p2);

		        path.moveTo(p2.x, p2.y);
		        path.lineTo(p1.x,p1.y);

		        canvas.drawPath(path, mPaint);
		    }
		
		
		}
	  @Override
	  public final void onAccuracyChanged(Sensor sensor, int accuracy)
	  {
	    // Do something here if sensor accuracy changes.
	  }

	  @Override
	  public final void onSensorChanged(SensorEvent event) 
	  {
		  
		  
	    float x =  event.values[0];
	    float y =  event.values[1];
	    float z =  event.values[2];
	    try
	    {
	    	writer.write(""+x+","+y+","+z);
	    	writer.append(System.getProperty("line.separator"));
	    }
	    catch (Exception ex)
	    {
	    }
	    //display values using TextView
	    title.setText(R.string.app_name);
	    tv.setText("X axis" +"\t\t"+x);
	    tv1.setText("Y axis" + "\t\t" +y);
	    tv2.setText("Z axis" +"\t\t" +z);
	    
	  }

	  @Override
	  protected void onResume()
	  {
	    super.onResume();
	    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	    try
	    {
	    	File writeTo = new File(android.os.Environment.getExternalStorageDirectory(), "accelerometer_data.txt");
	    	if (!writeTo.exists())
	    		writeTo.createNewFile();
	    	writer = new FileWriter(writeTo,true);
	    }
	    catch (Exception ex)
	    {
	    }
	    
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