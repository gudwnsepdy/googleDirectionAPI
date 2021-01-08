package com.andreasgift.googledirectionexample;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.android.gms.maps.model.JointType.ROUND;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private final static int MY_PERMISSIONS_REQUEST = 32;

    private GoogleMap mMap;
    private LatLng mOrigin;
    private LatLng mDestination;
    private LatLng mDestination2;
    private LatLng mDestination3;
    public TextView textview3;
    public TextView textview4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mOrigin = new LatLng(-33.8667, 151.2);
        mDestination = new LatLng(-31.578, 115.5141);
        //mDestination2 = new LatLng(41.2, 1.244);

        //mDestination3 = new LatLng(42.2, 1.3);
        mDestination2 = new LatLng(-34.92788,138.60008);
        mDestination3 = new LatLng(-28.81223,134.96254);
        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


         textview3 = (TextView)findViewById(R.id.textView2);
         textview4 = (TextView)findViewById(R.id.textView4);


    }


    /**
     * This method will manipulate the Google Map on the main screen
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Google map setup
        //Google map setup
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Show marker on the screen and adjust the zoom level
        mMap.addMarker(new MarkerOptions().position(mOrigin).title("출발지"));
        mMap.addMarker(new MarkerOptions().position(mDestination).title("목적지"));
        mMap.addMarker(new MarkerOptions().position(mDestination2).title("경유지1"));
        mMap.addMarker(new MarkerOptions().position(mDestination3).title("경유2"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDestination2,8f));
        new TaskDirectionRequest().execute(buildRequestUrl(mOrigin,mDestination));
//        new TaskDirectionRequest().execute(buildRequestUrl(mDestination,mDestination2));
//        new TaskDirectionRequest().execute(buildRequestUrl(mDestination2,mDestination3));

    }

    /**
     * Create requested url for Direction API to get routes from origin to destination
     *
     * @param origin
     * @param destination
     * @return
     */
    private String buildRequestUrl(LatLng origin, LatLng destination) {

        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDestination = "destination=" + destination.latitude + "," + destination.longitude;


        String sensor = "sensor=false";
        String mode = "mode=driving";

        String param = strOrigin + "&" + strDestination + "&" + sensor + "&" + mode;
        String output = "json";
        String APIKEY = getResources().getString(R.string.google_maps_key);

        //
        // String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key="+APIKEY;
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=sydney,au&destination=perth,au" +
                "&waypoints=via:-34.92788%2C138.60008%7Cvia:-28.81223%2C134.96254" +
                "&key=" + APIKEY;

        Log.d("TAG", url);



        return url;
    }

    /**
     * Request direction from Google Direction API
     *
     * @param requestedUrl see {@link #buildRequestUrl(LatLng, LatLng)}
     * @return JSON data routes/direction
     */
    private String requestDirection(String requestedUrl) {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(requestedUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        httpURLConnection.disconnect();
        return responseString;
    }

    //Get JSON data from Google Direction
    public class TaskDirectionRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            Log.d("test1","doInBackground1");
            try {
                responseString = requestDirection(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String responseString) {
            super.onPostExecute(responseString);
            //Json object parsing
            Log.d("test1","onPostExecute");
            TaskParseDirection parseResult = new TaskParseDirection();
            parseResult.execute(responseString);

        }
    }


    //Parse JSON Object from Google Direction API & display it on Map
    public class TaskParseDirection extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonString) {
            List<List<HashMap<String, String>>> routes = null;
            JSONObject jsonObject = null;

            try {
                Log.d("test1","doInBackground2");
                jsonObject = new JSONObject(jsonString[0]);
                DirectionParser parser = new DirectionParser();
                routes = parser.parse(jsonObject);

                Log.d("test1",DirectionParser.totalDis);
                textview3.setText(DirectionParser.totalDis);
                textview4.setText(DirectionParser.totalDur);
//                textview2.setText("ho");
//                textview3.setText(DirectionParser.totalDis);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            
//            textview3.setText("hwo");
            return routes;

        }


        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);
            ArrayList points = null;
            PolylineOptions polylineOptions = null;
            Log.d("test1","onPostExecute2");
            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lng"));

                    points.add(new LatLng(lat, lon));
                    

                }

                Cap endCap;
                polylineOptions.addAll(points);
                polylineOptions.width(15f);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
//                polylineOptions.endCap(new SquareCap());
                polylineOptions.startCap(new SquareCap());
                polylineOptions.endCap(new SquareCap());
                polylineOptions.jointType(ROUND);



            }
            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Request app permission for API 23/ Android 6.0
     *
     * @param permission
     */
    private void requestPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    MY_PERMISSIONS_REQUEST);
        }
    }




}
