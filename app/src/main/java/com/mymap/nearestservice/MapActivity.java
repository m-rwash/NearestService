package com.mymap.nearestservice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.impl.client.*;
import org.apache.http.client.methods.*;
import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MapActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks
        ,GoogleApiClient.OnConnectionFailedListener,LocationListener {

    protected final String LOG_TAG = "NearestServiceApp";

    private GoogleMap myGoogleMap = null;
    private GoogleApiClient googleApiClient = null;
    private LocationRequest locationRequest = null;
    private int serviceCode;
    private Location lastKnownLocation;

    private LatLng nearestService;

    private Route route = new Route();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Set up the map
        if(myGoogleMap == null)
        {
            SupportMapFragment mapFrag = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
            myGoogleMap = mapFrag.getMap();
            myGoogleMap.setMyLocationEnabled(true);
            myGoogleMap.setBuildingsEnabled(true);
            myGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // Marker Click Listener
        }

        Intent intent = getIntent();
        serviceCode = intent.getIntExtra("key",0);

        buildGoogleApiClient();
    }

    protected  void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override           // GoogleAPIClient
    protected void onStart()
    {
        super.onStart();
        googleApiClient.connect();
    }

    @Override       // GoogleAPIClient
    protected void onStop()
    {
        if(googleApiClient.isConnected())
            googleApiClient.disconnect();
        super.onStop();
    }

    @Override       // GoogleAPIClient
    public void onConnected(Bundle bundle)
    {
        locationRequest = locationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(40 * 1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
//        Log.i(LOG_TAG, latLng.toString());
        Location llocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(llocation!=null)
        {
            LatLng lastLatLng = new LatLng(llocation.getLatitude(),llocation.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(lastLatLng).zoom(15).build();
            myGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            myGoogleMap.addMarker(new MarkerOptions()
                    .position(lastLatLng)
                    .title("You Are Here!"));
            getNearestServices(llocation);
            drawRoute();
        }

    }

    @Override
    public void onLocationChanged(Location location)
    {
      //  latLng = new LatLng(location.getLatitude(),location.getLongitude());    //Continuous Update; That's why I implemented LocationListener

    }
    /*
    * Build URL here then pass it to Asynctask class to get services location
    * @param
    * */
    private void getNearestServices(Location location)
    {
        lastKnownLocation = location;
        String type = null;
        if(serviceCode == 1)    type="cafe";
        else if (serviceCode == 2)  type="restaurant";
        else if (serviceCode == 3)  type="park";
        else if (serviceCode == 4)  type="bar";
        else if (serviceCode == 5)  type="movie_theater";
        else if (serviceCode == 6)  type="book_store";
        else if (serviceCode == 7)  type="atm";
        else if (serviceCode == 8)  type="police";
        else if (serviceCode == 9)  type="hospital";

        Log.i("getNearestServi method", "Start");
        ServicesLocation servicesLocation = new ServicesLocation();
        servicesLocation.execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                + Double.toString(location.getLatitude()) +  "," +  Double.toString(location.getLongitude())
                + "&radius=10000&types="+type+"&sensor=true&key=AIzaSyD713l8X0QY21DsmLeZJMeHXWGfQ2BpqHU");
        Log.i("getNearestServic method", "Done!");
    }
    private String fetchURL(String url)
    {
        Log.i("fetchURL","Start!");
        HttpURLConnection httpURLConnection = null;
        try
        {
            Log.i("HttpURLConnection","Start!");

            URL u = new URL(url);
            httpURLConnection = (HttpURLConnection)u.openConnection();
            httpURLConnection.setRequestMethod("GET");

            Log.i("HttpURLConnection", "Start2");


            httpURLConnection.setRequestProperty("Content-length", "0");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setAllowUserInteraction(false);
           /* TIMEOUT
           httpURLConnection.setConnectTimeout(timeout);
            httpURLConnection.setReadTimeout(timeout);*/
            httpURLConnection.connect();
            int status = httpURLConnection.getResponseCode();
            Log.i("HttpURLConnection", "Start3");

            if(status == 200 || status == 201)
            {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                Log.i("HttpURLConnection", "Buffer Start");

                while ((line=bufferedReader.readLine())!=null)
                {
                    Log.i("HttpURLConnection", "Buffer reading...");

                    stringBuilder.append(line+"/n");
                }
                bufferedReader.close();
                Log.i("HttpURLConnection", "Buffer closing...");
                Log.i("HttpURLConnection", stringBuilder.toString());


              //  Toast.makeText(this,stringBuilder.toString(),Toast.LENGTH_LONG).show();

                return stringBuilder.toString();
            }
            Log.i("HttpURLConnection","Done!");


        }catch (MalformedURLException ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            if(httpURLConnection!=null)
            {
                try{
                    httpURLConnection.disconnect();
                }catch (Exception ex)
                {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        Log.i("fetchURL","Done!");

        return null;
    }

    private class ServicesLocation extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... urls)
        {
            Log.i("doInBackground", "Start!");
            return fetchURL(urls[0]);
        }
        protected void onPostExecute(String JSONString)
        {
            String convertedJSON = convertStandardJSONString(JSONString);
            Log.i("postExcute","Start!");
            try
            {
                Log.i("postExcute","try!");

                JSONObject jsonObject = new JSONObject(convertedJSON);
                JSONArray nearestLocations = new JSONArray(jsonObject.getString("results"));
                Log.i("postExcute","try2!");
                Log.i("postExcute",jsonObject.getString("results"));

                for(int i=0; i<nearestLocations.length(); i++)
                {
                    JSONObject location = nearestLocations.getJSONObject(i);
                    Double lat = Double.parseDouble(location.getJSONObject("geometry").getJSONObject("location").getString("lat"));
                    Double lng = Double.parseDouble(location.getJSONObject("geometry").getJSONObject("location").getString("lng"));
                    LatLng locationLatLng = new LatLng(lat,lng);
                    Log.i("postExcute",locationLatLng.toString());
                    //String openNow = location.getJSONObject("opening_hours").getString("open_now");
                        myGoogleMap.addMarker(new MarkerOptions()
                                .position(locationLatLng)
                                .title(Integer.toString(i+1) +"  "+location.getString("name"))
                                .snippet("Open Now"));
                }

            }catch (Exception e)
            {
                Log.d("Emergency Item", e.getLocalizedMessage());
            }
            Log.i("postExcute","Done!");

        }
    }

    private void drawRoute()
    {

        if(myGoogleMap!=null)
        {
            myGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(final Marker marker)
                {
                    Log.i("drawRoute","drawRoute Start");
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MapActivity.this);
                    alertBuilder.setTitle("First-Responder");
                    alertBuilder.setMessage("Would you like to show routes on the map ?");
                    alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {

                                @Override
                                public void onClick(DialogInterface dialog, int arg1)
                                {
                                    Log.i("drawRoute","onClick Yes Start");
                                    nearestService = marker.getPosition();
                                    //Call drawOnmap function
                                    route.drawOnMap(myGoogleMap, lastKnownLocation, nearestService);

                                    Log.i("drawRoute", "onClick Yes End");
                                }
                            }
                    );
                    alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    });
                    alertBuilder.show();
                }
            });
        }
    }
    

    public String convertStandardJSONString(String data_json){

        data_json = data_json.replace("/n", "");
        return data_json;
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.i(LOG_TAG, "GoogleAPIClient connection has been SUSPENDED!");
       // textView.setText("connection has been SUSPENDED");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.i(LOG_TAG, "GoogleAPIClient connection has been FAILED!");
        //textView.setText("connection has been SUSPENDED");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
