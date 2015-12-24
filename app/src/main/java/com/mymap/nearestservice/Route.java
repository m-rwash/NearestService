package com.mymap.nearestservice;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Route
{
    public String JSONStr;
    private List<LatLng> points;
    private GoogleMap myGoogleMap;
    private String LOG_TAG = "ROUTE_CLASS";

    public Route()
    {
        points = new ArrayList<LatLng>();
        Log.i(LOG_TAG,"empty constructor");
    }
    public void drawOnMap(GoogleMap googleMap, Location myLocation, LatLng nearestPoint)
    {
        Log.i(LOG_TAG,"drawonmap_function");

        myGoogleMap = googleMap;
        new CalcRoutePoints().execute("https://maps.googleapis.com/maps/api/directions/json?origin="
                + Double.toString(myLocation.getLatitude()) + ","
                + Double.toString(myLocation.getLongitude())
                + "&destination="
                + Double.toString(nearestPoint.latitude)+ ","
                + Double.toString(nearestPoint.longitude)
                +"&sensor=true");
    }
    private String readConnectionString(String url)
    {
        Log.i("fetchURL", "Start!");
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
    void parseJSONInstructions () throws JSONException
    {
        JSONObject obj = new JSONObject(JSONStr);
        JSONObject routes = obj.getJSONArray("routes").getJSONObject(0);
        JSONObject legs = routes.getJSONArray("legs").getJSONObject(0);
        JSONArray steps = legs.getJSONArray("steps");

        for (int i = 0; i < steps.length(); i++)
        {
            JSONObject step = steps.getJSONObject(i);
        }
    }
    private List <LatLng> decodePoints(String encodedPoints)
    {
        Log.i(LOG_TAG,"decode_function");

        List <LatLng> coordinates = new ArrayList<LatLng>();
        int index = 0;
        int len = encodedPoints.length();
        int lat = 0, lng = 0;
        while (index < len)
        {
            int b, shift = 0, result = 0;
            do
            {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do
            {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng( (((double) lat / 1E5)) , (((double) lng / 1E5) ));
            coordinates.add(p);
        }
        return coordinates;
    }
    private class CalcRoutePoints extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... urls)
        {
            Log.i(LOG_TAG,"doInBackGround_function");

            return readConnectionString(urls[0]);
        }
        protected void onPostExecute(String JSONString)
        {
            Log.i(LOG_TAG,"onPostEcecute_function");

            JSONStr = JSONString;
            String convertedJSON = convertStandardJSONString(JSONStr);

            try
            {
                JSONObject obj = new JSONObject(convertedJSON);
                JSONArray routes = new JSONArray(obj.getString("routes"));
                JSONObject direction = routes.getJSONObject(0);
                JSONObject overview_polyline = direction.getJSONObject("overview_polyline");
                String encodedPoints = overview_polyline.getString("points");
                points.clear();
                points.addAll(decodePoints(encodedPoints));
                for(int i = 0; i < points.size()-1 ; i++)
                {
                    Log.i(LOG_TAG,"onPostEcecute_function Draw Points!!!!!!!");

                    LatLng src = points.get(i);
                    LatLng dest = points.get(i+1);
                    myGoogleMap.addPolyline(new PolylineOptions().add(src, dest).width(5).color(Color.BLUE).geodesic(true));
                }
                Log.i(LOG_TAG,"onPostEcecute_function Draw Points END *****");

                parseJSONInstructions();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public String convertStandardJSONString(String data_json){

        data_json = data_json.replace("/n", "");
        return data_json;
    }

}