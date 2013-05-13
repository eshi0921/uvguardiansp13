package com.example.navidration;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.*;
import java.util.List;
import java.util.Timer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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
	  
	  
	  @Override
	  public final void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);

	    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

	    
	    //get layout
	    layout = (RelativeLayout)findViewById(R.id.relative);
	    mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);

	    mapOverlays = mapView.getOverlays();        
	    projection = mapView.getProjection();
	    mapOverlays.add(new MyOverlay());    
	    
	    String weatherData = getWeatherData();
	    
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
	  
	  protected String getWeatherData()
	  {
		  LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		  Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		  double longitude = location.getLongitude();
		  double latitude = location.getLatitude();
		  HttpClient httpc_weather = new DefaultHttpClient();
		  HttpGet http_request = new HttpGet("http://api.worldweatheronline.com/free/v1/tz.ashx?key=324eefpxmgz3ww7c6tga4svd&num_of_days=1&q="+longitude+","+latitude+"&format=json");
		  HttpResponse response;
		  BufferedReader in = null;
		  String data= "";
		  try
	        {
	           	response = httpc_weather.execute(http_request);
	            response.getStatusLine().getStatusCode();
	            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	            StringBuffer sb = new StringBuffer("");
	            String l = "";
	            String output = "";
	            String nl = System.getProperty("line.separator");
	            while ((l = in.readLine()) !=null){
	                sb.append(l + nl);
	                if (l.contains("humidity"))
	                {
	                	output += l.split(":= ")[1]+"%,";
	                }
	                else if (l.contains("temp_C"))
	                {
	                	output += l.split(":= ")[1]+"C";
	                }
	            }
	            in.close();
	            data = sb.toString();
	            return output;        
	        }
		  catch (Exception ex)
		  {
		  }
		  finally{
	            if (in != null){
	                try{
	                    in.close();
	                    return data;
	                }catch (Exception e){
	                    e.printStackTrace();
	                }
	            }
	        }
		  return data;
	  }
	  
	  
	  
	  
	}