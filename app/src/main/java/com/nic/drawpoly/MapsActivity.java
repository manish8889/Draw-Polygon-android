package com.nic.drawpoly;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MapsActivity extends FragmentActivity implements LocationListener,
        OnMapReadyCallback, GoogleApiClient
                .ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final String TAG = "MapsActivity";
    //    private static final long INTERVAL = 1000 * 60 * 1; //1 minute
    private static final long INTERVAL = 1 * 1000;  /* 1 secs */

    private static final long FASTEST_INTERVAL = 1000; /* 1 sec */
    //    private static final long FASTEST_INTERVAL = 1000 * 60 * 1; // 1 minute
    private static final float SMALLEST_DISPLACEMENT = 0.25F; //quarter of a meter


    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private String city = "";
    private String country = "";
    private String area = "";
    private String title;
    private String requiredArea = "";
    private GoogleMap googleMap;
    private List<Address> addresses;
    ArrayList<LatLng> routePoints;
    Polyline line; //added
    TextView textViewAccurecy;
    private TextView normalMap, hybridMap, satelliteMap, terrainMap, noneMap;


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(INTERVAL);
//        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
//        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT); //added
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate ...............................");
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {

            Toast.makeText(this, "Google Play Services is not available", Toast.LENGTH_LONG).show();

            finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        textViewAccurecy = (TextView) findViewById(R.id.current_location_label);

        normalMap = (TextView) findViewById(R.id.normal_map);
        hybridMap = (TextView) findViewById(R.id.hybrid_map);
        satelliteMap = (TextView) findViewById(R.id.satellite_map);
        terrainMap = (TextView) findViewById(R.id.terrain_map);
        noneMap = (TextView) findViewById(R.id.none_map);


        normalMap.setOnClickListener(this);
        hybridMap.setOnClickListener(this);
        satelliteMap.setOnClickListener(this);
        terrainMap.setOnClickListener(this);
        noneMap.setOnClickListener(this);

        fm.getMapAsync(this);

        routePoints = new ArrayList<LatLng>();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        isReadStoragePermissionGranted();
        isWriteStoragePermissionGranted();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {

                    File directory = Environment.getExternalStorageDirectory();

//                    if (directory.canWrite()) {
                    File kmlFile = new File(directory, "" + "abc" + ".kml");
                    FileWriter fileWriter = new FileWriter(kmlFile);
                    BufferedWriter outWriter = new BufferedWriter(fileWriter);
//
//                    outWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//                            "\n <kml xmlns=\"http://www.opengis.net/kml/2.2\">" +
//                            "\n <Document>" + "\n");
//                    for (int i = 0; i < routePoints.size(); i++) {
//                        outWriter.write("<Placemark>" +
//                                "\n <name>" + i + "</name>" +
//                                "\n <description> </description>" +
//                                "\n <Point>" +
//                                "\n <coordinates>" + routePoints.get(i).longitude + "," + routePoints.get(i).latitude + "</coordinates>" +
//                                "\n </Point>" +
//                                "\n </Placemark>" + "\n");
//                    }
//                    outWriter.write("</Document>" +
//                            "\n </kml>");
//                    outWriter.close();


//                    //Test to draw Polygon in KML
//
                    outWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                            "\n <kml xmlns=\"http://www.opengis.net/kml/2.2\">" +
                            "\n <Document>" + "\n");
//                    for (int i = 0; i < routePoints.size(); i++) {
                    outWriter.write("<Placemark>" +
                            "\n <name>" + " PolygonVanApp" + "</name>" +
                            "\n <description> </description>" +
                            "\n <Polygon>" +
                            "\n <outerBoundaryIs>" +
                            "\n <LinearRing>" +
                            "\n <coordinates> " + "\n");
                    for (int i = 0; i < routePoints.size(); i++) {
                        outWriter.write(+routePoints.get(i).longitude + "," + routePoints.get(i).latitude + ",0  ");
                    }
                    outWriter.write("\n </coordinates>" +
                            "\n </LinearRing>" +
                            "\n </outerBoundaryIs>" +
                            "\n </Polygon>" +
                            "\n </Placemark>" + "\n");
//                    }
                    outWriter.write("</Document>" +
                            "\n </kml>");
                    outWriter.close();

//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Snackbar.make(view, "Export To KML", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


//        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFrag.getMapAsync(this);
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


    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            Toast.makeText(getApplicationContext(), "Google Play Services is not Available", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted1");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted1");
            return true;
        }
    }

    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted2");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted2");
            return true;
        }
    }

    protected void startLocationUpdates() {
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
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, MapsActivity.this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }


    @Override

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");

                if (grantResults.length > 0) {

                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                        //resume tasks needing this permission

                    } else {
                    }

                }


                break;

            case 3:
                Log.d(TAG, "External storage1");

                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                        //resume tasks needing this permission

                    } else {
                    }
                }


                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());


//        addMarker();


        float accuracy = location.getAccuracy();
        textViewAccurecy.setText("accuracy:" + accuracy);
        Log.d("iFocus", "The amount of accuracy is " + accuracy);

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
//        double latitude = location.getLatitude();
//        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude); //you already have this
        routePoints.add(latLng); //added
        redrawLine(); //added
    }

    private void redrawLine() {

//        googleMap.clear();  //clears all Markers and Polylines


//        / Add polygons to indicate areas on the map.

//        Polygon polygon1 = googleMap.addPolygon(new PolygonOptions().fillColor(Color.BLUE).geodesic(true)
//                .clickable(true)
//                .add());

//        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
//        for (int i = 0; i < routePoints.size(); i++) {
//            LatLng point = routePoints.get(i);
//            options.add(point);
//        }

        PolygonOptions options1 = new PolygonOptions().fillColor(Color.BLUE).strokeWidth(5).strokeColor(Color.RED);
        for (int i = 0; i < routePoints.size(); i++) {
            LatLng point = routePoints.get(i);
            options1.add(point);

        }
        addMarker(); //add Marker in current position
//        line = googleMap.addPolyline(options); //add Polyline
        Polygon polygon1 = googleMap.addPolygon(options1);
    }

    private void addMarker() {
//        MarkerOptions options = new MarkerOptions();

        // following four lines requires 'Google Maps Android API Utility Library'
        // https://developers.google.com/maps/documentation/android/utility/
        // I have used this to display the time as title for location markers
//        // you can safely comment the following four lines but for this info
//        IconGenerator iconFactory = new IconGenerator(this);
//        iconFactory.setStyle(IconGenerator.STYLE_PURPLE);
//        // options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(mLastUpdateTime + requiredArea + city)));
//        options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(requiredArea + ", " + city)));
//        options.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());


        LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        options.position(currentLatLng);
        Marker mapMarker = googleMap.addMarker(markerOptions);
//        long atTime = mCurrentLocation.getTime();
//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));
//        String title = mLastUpdateTime.concat(", " + requiredArea).concat(", " + city).concat(", " + country);
//        mapMarker.setTitle(title);

//
//        TextView mapTitle = (TextView) findViewById(R.id.textViewTitle);
//        mapTitle.setText(title);

        Log.d(TAG, "Marker added.............................");
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
                30));
        Log.d(TAG, "Zoom done.............................");
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.normal_map:
                setMapType(MapType.NORMAL);
                break;
            case R.id.hybrid_map:
                setMapType(MapType.HYBRID);
                break;
            case R.id.satellite_map:
                setMapType(MapType.SATELLITE);
                break;
            case R.id.terrain_map:
                setMapType(MapType.TERRAIN);
                break;
            case R.id.none_map:
                setMapType(MapType.NONE);
                break;
        }
    }

    /*  Set Map Type on basis of MapType Enum and highlight selected TextView */
    private void setMapType(MapType mapType) {
        switch (mapType) {
            case NORMAL:
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                normalMap.setTextColor(getResources().getColor(R.color.teal));
                setDefaultColorBack(new TextView[]{hybridMap, satelliteMap, terrainMap, noneMap});
                break;
            case HYBRID:
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                hybridMap.setTextColor(getResources().getColor(R.color.teal));
                setDefaultColorBack(new TextView[]{normalMap, satelliteMap, terrainMap, noneMap});
                break;
            case SATELLITE:
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                satelliteMap.setTextColor(getResources().getColor(R.color.teal));
                setDefaultColorBack(new TextView[]{hybridMap, normalMap, terrainMap, noneMap});
                break;
            case TERRAIN:
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                terrainMap.setTextColor(getResources().getColor(R.color.teal));
                setDefaultColorBack(new TextView[]{hybridMap, satelliteMap, normalMap, noneMap});
                break;
            case NONE:
                googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                noneMap.setTextColor(getResources().getColor(R.color.teal));
                setDefaultColorBack(new TextView[]{hybridMap, satelliteMap, terrainMap, normalMap});
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /*  Turn Selected TextView color back to Black when MapType changes  */
    private void setDefaultColorBack(TextView[] views) {
        for (TextView v : views)
            v.setTextColor(getResources().getColor(android.R.color.black));
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.clear();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                Toast.makeText(getApplicationContext(), "Location button has been clicked", Toast.LENGTH_LONG).show();
                return true;
            }
        });
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
//
//        LatLng[] points = GetPolygonPoints();
//
//        if (points.length >3){
//
//            Draw_Polygon();
//
//        }
//        else {
//
//            Add_Markers();
//
//        }

//        addMarker();

//        LatLng sydney = new LatLng(-34, 151);
//        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    public void Draw_Polygon() {

        LatLng[] points = GetPolygonPoints();
        Polygon p = googleMap.addPolygon(
                new PolygonOptions()
                        .add(points)
                        .strokeWidth(7)
                        .fillColor(Color.CYAN)
                        .strokeColor(Color.BLUE)
        );

        //Calculate the markers to get their position
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for (LatLng point : points) {
            b.include(point);
        }
        LatLngBounds bounds = b.build();
        //Change the padding as per needed
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 20, 20, 5);
        googleMap.animateCamera(cu);

    }

    private LatLng[] GetPolygonPoints() {

//        Bundle bundle = getIntent().getExtras();
//        wkt = bundle.getString("wkt");
        ArrayList<LatLng> points = new ArrayList<LatLng>();
//        Pattern p = Pattern.compile("(\\d*\\.\\d+)\\s(\\d*\\.\\d+)");
//        Matcher m = p.matcher(wkt);
//        String point;

//        points.add(new LatLng(Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2))));
//        points.add(new LatLng(Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2))));

//        LatLng latLng = new LatLng(Double.parseDouble(result.get(i).get_Latitude()), Double.parseDouble(result.get(i).get_Longitude()));


//        while (m.find()){
//            point =  wkt.substring(m.start(), m.end());
//            points.add(new LatLng(Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2))));
//        }
        return points.toArray(new LatLng[points.size()]);

    }
}