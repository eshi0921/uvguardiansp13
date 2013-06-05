package com.example.navidration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.example.navidration.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

/**
 * Created by Byte on 5/31/13.
 */
public class MapTest extends FragmentActivity
	implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

	private GoogleMap mMap;
	private LocationClient mLocationClient;
	private List<Fountain> mWaterFountains;
	private List<Marker> mMarkers;
	
	boolean wfDataRetrieved;
	
	private class WaterFountainAsyncTask extends AsyncTask<LatLng, Integer, Void>{

		
		@Override
		protected void onPreExecute() {
		// update the UI immediately after the task is executed
			super.onPreExecute();
			wfDataRetrieved = false;

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			wfDataRetrieved = true;
		}
		
		@Override
		protected Void doInBackground(LatLng... params) {
	    	URL url = null;
	    	String rawdata = "";
	    	InputStreamReader reader = null;
	    	BufferedReader buffer = null;
	    	//System.out.println("RETRIEVING WF");
	    	if (mWaterFountains == null)
	    		mWaterFountains = new ArrayList<Fountain>();
	    	try {
	    		String dbURL = "http://209.141.35.124/getWaterFountain.php?dbpass=uvguardian13&latitude="+params[0].latitude+"&longitude="+params[0].longitude;
	    		url = new URL(dbURL);
				HttpURLConnection fountainDBConnection = (HttpURLConnection) url.openConnection();
				if (fountainDBConnection != null) {
					fountainDBConnection.setDoInput(true);
				}
	    		InputStream stream = fountainDBConnection.getInputStream();
	    		reader = new InputStreamReader(stream);
	    		buffer = new BufferedReader(reader);
	    		rawdata = buffer.readLine();
	    		if (rawdata != null)
	    		{
	    			System.out.println(rawdata);
	    			String[] fountains = rawdata.split(";");
	    			for (int i = 0; i < fountains.length; i++) {
	    				String[] attribs = fountains[i].split(",");
	    				System.out.println("Adding WF!");
	    				mWaterFountains.add(new Fountain(Integer.parseInt(attribs[Fountain.FOUNTAINID]),
	    													Double.parseDouble(attribs[Fountain.LATITUDE]),
	    													Double.parseDouble(attribs[Fountain.LONGITUDE]),
	    													Integer.parseInt(attribs[Fountain.NYES]),
	    													Integer.parseInt(attribs[Fountain.NNO])));
	    			}
	    		}
	    		else
	    		{
	    			System.out.println("NO FOUNTAINS NEARBY");
	    			displayMessage("No water fountains nearby");
	    		}
	    		reader.close();
			} catch (IOException e) {
				e.printStackTrace();
                displayMessage(e.getMessage());
			} finally {
	            if (reader != null){
	                try{
	                    reader.close();
	                }catch (Exception e){
	                    e.printStackTrace();
	                    displayMessage(e.getMessage());
	                }
	            }
			}
	    	return null;
		}
		
		private void displayMessage(String msg)
		{
			Context context = getApplicationContext();
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUpLocationClientIfNeeded();
        mLocationClient.connect();
    }
    
    @Override
    public void onPause() {
      super.onPause();
      if (mLocationClient != null) {
        mLocationClient.disconnect();
      }
    }
    
	private static final LocationRequest REQUEST = LocationRequest.create()
    	.setInterval(5000)
    	.setFastestInterval(16)
    	.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not have been
     * completely destroyed during this process (it is likely that it would only be stopped or
     * paused), {@link #onCreate(Bundle)} may not be called again so we should call this method in
     * {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
                getApplicationContext(),
                this,  // ConnectionCallbacks
                this); // OnConnectionFailedListener
        }
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
	    mLocationClient.requestLocationUpdates(
	    		REQUEST,
	            this);  // LocationListener
      	Location loc = mLocationClient.getLastLocation();
      	if (mMap != null) {
	        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(loc.getLatitude(), loc.getLongitude())));
	        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
      	}
        WaterFountainAsyncTask wfTask = new WaterFountainAsyncTask();
        wfTask.execute(new LatLng(loc.getLatitude(), loc.getLongitude()));
        if (!wfDataRetrieved)
        {
			try {
				wfTask.get(1000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        if (mMarkers == null) {
        	mMarkers = new ArrayList<Marker>();
        }
        for (int i = 0; i < mWaterFountains.size(); i++)
        {
        	Fountain testFount = mWaterFountains.get(0);
	        mMarkers.add(mMap.addMarker(new MarkerOptions()
	        	.position(new LatLng(testFount.latitude, testFount.longitude))
	        	.title("ID: "+testFount.id)
	        	.snippet("Overall Rating: "+(testFount.nYes - testFount.nNo))
	        	.draggable(true)));
        }
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
}