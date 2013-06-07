package com.example.navidration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Byte on 5/31/13.
 */
public class MapTest extends FragmentActivity
	implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, OnInfoWindowClickListener, FountainPrompt.FountainPromptListener {

	static int RADIO_REQUEST = 123;
	
	private GoogleMap mMap;
	private LocationClient mLocationClient;
	private ArrayList<Fountain> mFountains;
	private Location mLocation;
	
	boolean wfDataRetrieved;
	
	private class WaterFountainAsyncTask extends AsyncTask<String, Integer, Void>{
		
		String rawdata = null;
		
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
			if (rawdata != null)
				wfDataRetrieved = true;
		}
		
		@Override
		protected Void doInBackground(String... params) {
	    	URL url = null;
	    	InputStreamReader reader = null;
	    	BufferedReader buffer = null;
	    	//System.out.println("RETRIEVING WF");
	    		
	    	try {
	    		String dbURL = params[0];
	    		System.out.println(dbURL);
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
	    			if(params[1].equals(getString(R.string.get_php)) || params[1].equals(getString(R.string.add_php)))
	    			{
	    				if (mFountains == null)
	    					mFountains = new ArrayList<Fountain>();
		    	    	
	    				System.out.println(rawdata);
		    			String[] fountains = rawdata.split(";");
		    			for (int i = 0; i < fountains.length; i++) {
		    				String[] attribs = fountains[i].split(",");
		    				System.out.println("Adding WF!");
		    				System.out.println(fountains[i]);
		    				System.out.println(fountains[i].length());

		    				if (attribs.length == 5 && getFountainById(Integer.parseInt(attribs[Fountain.FOUNTAINID])) == null) {
		    					mFountains.add(new Fountain(Integer.parseInt(attribs[Fountain.FOUNTAINID]),
		    													Double.parseDouble(attribs[Fountain.LATITUDE]),
		    													Double.parseDouble(attribs[Fountain.LONGITUDE]),
		    													Integer.parseInt(attribs[Fountain.NYES]),
		    													Integer.parseInt(attribs[Fountain.NNO])));
		    				}
		    			}
	    			}
	    			else if (params[1].equals(getString(R.string.rate_php)))
	    			{
	    				String[] attribs = rawdata.split(",");
	    				int fid = Integer.parseInt(attribs[0]);
	    				Fountain f = getFountainById(fid);
	    				f.nYes = Integer.parseInt(attribs[1]);
	    				f.nNo = Integer.parseInt(attribs[2].trim());
	    			}
	    			wfDataRetrieved = true;
	    		}
	    		reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
	            if (reader != null){
	                try{
	                    reader.close();
	                }catch (Exception e){
	                    e.printStackTrace();
	                }
	            }
			}
	    	return null;
		}
	}

	private class FountainInfoWindowAdapter implements InfoWindowAdapter {

		private final View mContents;
		
		FountainInfoWindowAdapter() {
			mContents = getLayoutInflater().inflate(R.layout.fountain_contents, null);
		}
		
		@Override
		public View getInfoContents(Marker fountain) {
			render(fountain, mContents);
			return mContents;
		}
		
		@Override
		public View getInfoWindow(Marker fountain) {
			return null;
		}
		
		private void render(Marker marker, View view) {
            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null && snippet.length() > 12) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }
		}
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        final Button button = (Button) findViewById(R.id.add);
        button.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				FountainPrompt.newInstance(getString(R.string.add_fountain), getString(R.string.add), -1)
							  .show(getSupportFragmentManager(), getString(R.string.add_fountain));
			}
		});
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
                mMap.setInfoWindowAdapter(new FountainInfoWindowAdapter());
                mMap.setOnInfoWindowClickListener(this);
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

    private void addFountainMarkers() {
    	Iterator<Fountain> it = mFountains.iterator();
    	while (it.hasNext()) {
    		Fountain f = it.next();
    		if (f.mMarker == null) {
	    		f.mMarker = mMap.addMarker(new MarkerOptions()
		        	.position(new LatLng(f.latitude, f.longitude))
		        	.title("ID: "+f.id)
		        	.snippet("Overall Rating: "+(f.nYes - f.nNo))
		        	.icon(BitmapDescriptorFactory.fromResource(R.drawable.fountain)));
    		}
    	}
    }
	@Override
	public void onLocationChanged(Location loc) {
		if (mLocation == null || mLocation.distanceTo(loc) > 1000)
		{
			mLocation = loc;
      		CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(loc.getLatitude(), loc.getLongitude()));
	        mMap.moveCamera(center);
	        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
		}
		if (mFountains == null) {
	        WaterFountainAsyncTask wfTask = new WaterFountainAsyncTask();
	        wfTask.execute("http://"+getString(R.string.db_ip)+"/"+getString(R.string.get_php)+"?dbpass="+getString(R.string.db_pass)+"&latitude="+loc.getLatitude()+"&longitude="+loc.getLongitude(), getString(R.string.get_php));
	        while (!wfDataRetrieved) {
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
					Toast.makeText(this, "Retrieving fountains timeout", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
	        }
			addFountainMarkers();
			wfDataRetrieved = false;
		}
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
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Fountain f = getFountainByMarker(marker);
		FountainPrompt fp = FountainPrompt.newInstance(getString(R.string.rate_fountain), getString(R.string.rate), f.id);
		fp.show(getSupportFragmentManager(), getString(R.string.rate_fountain));
	}
	
	@Override
	public void onDialogPositiveClick(FountainPrompt dialog) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		if (dialog.getFid() < 0) {
			if (mLocationClient.isConnected()) {
				WaterFountainAsyncTask wfTask = new WaterFountainAsyncTask();
				Location loc = mLocationClient.getLastLocation();
				wfTask.execute("http://"+getString(R.string.db_ip)+"/"+getString(R.string.add_php)+"?dbpass="+getString(R.string.db_pass)+"&latitude="+loc.getLatitude()+"&longitude="+loc.getLongitude()+"&rating="+dialog.rating+"&uid="+pref.getString("prefUserID", "-1"), getString(R.string.add_php));
		        while (!wfDataRetrieved) {
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
						Toast.makeText(this, "Add fountain timeout", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
		        }
		        addFountainMarkers();
		        wfDataRetrieved = false;
			}
		}
		else {
			if (mLocationClient.isConnected()) {
				WaterFountainAsyncTask wfTask = new WaterFountainAsyncTask();
				wfTask.execute("http://"+getString(R.string.db_ip)+"/"+getString(R.string.rate_php)+"?dbpass="+getString(R.string.db_pass)+"&fid="+dialog.getFid()+"&rating="+dialog.rating+"&uid="+pref.getString("prefUserID", "-1"), getString(R.string.rate_php));
		        while (!wfDataRetrieved) {
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
						Toast.makeText(this, "Updating ratings timeout", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
		        }
		        wfDataRetrieved = false;
		        Fountain f = getFountainById(dialog.getFid());
		        Marker m = f.mMarker;
		        m.setSnippet("Overall Rating "+(f.nYes - f.nNo));
		        m.showInfoWindow();
			}
		}
	}
	
	@Override
	public void onDialogNegativeClick(FountainPrompt dialog) {
		
	}
	
	private Fountain getFountainByMarker(Marker marker) {
		if (mFountains != null) {
			for (Fountain f : mFountains) {
				if (marker.equals(f.mMarker)) {
					return f;
				}
			}
		}
		return null;
	}
	
	private Fountain getFountainById(int fid) {
		if (mFountains != null) {
			for (Fountain f : mFountains) {
				if (f.id == fid) {
					return f;
				}
			}
		}
		return null;
	}
}