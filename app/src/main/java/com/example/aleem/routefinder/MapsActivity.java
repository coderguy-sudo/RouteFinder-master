package com.example.aleem.routefinder;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aleem.routefinder.Model.MyPlaces;
import com.example.aleem.routefinder.Model.PlaceDetail;
import com.example.aleem.routefinder.Model.Results;
import com.example.aleem.routefinder.Remote.IGoogleAPIService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{


    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 5000;
    double latitude,longitude;
    IGoogleAPIService mService;
    String url;
    String placeType;

    RelativeLayout bottom_row_design;
    TextView place_name;
    RatingBar rating;
    Button directions;

    MyPlaces currentPlace;
    PlaceDetail mPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setupMapIfNeeded();

        mService = Common.getGoogleApiService();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }

        buildLocationCallBack();
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

    }

    @Override
    protected void onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                lastLocation = locationResult.getLastLocation();
                latitude = lastLocation.getLatitude();
                longitude = lastLocation.getLongitude();
                if(currentLocationMarker != null){
                    currentLocationMarker.remove();
                }
                Log.d("MapsActivity = ",""+latitude);
                LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Location");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                currentLocationMarker = mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

                if (isNetworkAvailable()) {
                    showPlaceResult();
                }else {
                    Log.e("Error","No network");
                    Toast.makeText(getApplicationContext(), "No Internet Connection" , Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void setupMapIfNeeded() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mMap.setMyLocationEnabled(true);
                        buildLocationCallBack();
                        buildLocationRequest();
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;

        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.night_mode));

            if (!success) {
                Log.e("Maps", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("Maps", "Can't find style. Error: ", e);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }else {
            mMap.setMyLocationEnabled(true);
        }

        MapStateManager mgr = new MapStateManager(this);
        CameraPosition position = mgr.getSavedCameraPosition();
        if (position != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            mMap.moveCamera(update);
            mMap.setMapType(mgr.getSavedMapType());
        }



        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getSnippet() !=null) {
                    Common.currentResult = currentPlace.getResults()[Integer.parseInt(marker.getSnippet())];
                    bottom_row_design = findViewById(R.id.bottom_row_design);
                    bottom_row_design.setVisibility(View.VISIBLE);

                    place_name = findViewById(R.id.place_name);
                    rating = findViewById(R.id.rating);

                    //emptyViews
                    place_name.setText("");

                    mService.getDetailPlace(getPlaceDetailUrl(Common.currentResult.getPlace_id()))
                            .enqueue(new Callback<PlaceDetail>() {
                                @Override
                                public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                                    mPlace = response.body();
                                    place_name.setText(mPlace.getResult().getName());
                                }

                                @Override
                                public void onFailure(Call<PlaceDetail> call, Throwable t) {

                                }
                            });

                    if (Common.currentResult.getRating() != null && !TextUtils.isEmpty(Common.currentResult.getRating())) {
                        rating.setRating(Float.parseFloat(Common.currentResult.getRating()));
                        rating.setVisibility(View.VISIBLE);
                    } else {
                        rating.setVisibility(View.GONE);
                    }

                    directions = findViewById(R.id.directions);
                    directions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MapsActivity.this, ViewDirections.class);
                            startActivity(intent);
                        }
                    });
                }
                return true;
            }
        });
    }

    private String getPlaceDetailUrl(String place_id) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json");
        url.append("?placeid="+place_id);
        url.append("&key="+"AIzaSyBkvnDOws6tM-CrdpEQRZE4PDSEp9PMkpA");
        return url.toString();
    }


    /*private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }*/

    public void showPlaceResult(){
        mMap.clear();
        Intent intent = getIntent();
        if(intent.hasExtra("HOSPITAL")){
            placeType = "hospital";
            url = getUrl(latitude, longitude, placeType);
            finalResult();
        }else if(intent.hasExtra("BANK")){
            placeType = "bank";
            url = getUrl(latitude, longitude, placeType);
            finalResult();
        }else if(intent.hasExtra("ATM")){
            placeType = "atm";
            url = getUrl(latitude, longitude, placeType);
            finalResult();
        }else if(intent.hasExtra("PHARMACY")){
            placeType = "pharmacy";
            url = getUrl(latitude, longitude, placeType);
            finalResult();
        }else if(intent.hasExtra("MARKET")){
            placeType = "supermarket";
            url = getUrl(latitude, longitude, placeType);
            finalResult();
        }else if(intent.hasExtra("RESTAURANTS")){
            placeType = "restaurant";
            url = getUrl(latitude, longitude, placeType);
            finalResult();
        }else if(intent.hasExtra("MOSQUES")){
            placeType = "mosque";
            url = getUrl(latitude, longitude, placeType);
            finalResult();
        }else if(intent.hasExtra("SCHOOL")){
            placeType = "school";
            url = getUrl(latitude, longitude, placeType);
            finalResult();
        }else if(intent.hasExtra("UNIVERSITY")){
            placeType = "university";
            url = getUrl(latitude, longitude, placeType);
            finalResult();
        }else if(intent.hasExtra("BUS")){
            placeType = "bus_station";
            url = getUrl(latitude, longitude, placeType);
            finalResult();
        }else if(intent.hasExtra("PETROL")){
            placeType = "gas_station";
            url = getUrl(latitude, longitude, placeType);
            finalResult();
        }else if(intent.hasExtra("MOVIE")){
            placeType = "movie_theater";
            url = getUrl(latitude, longitude, placeType);
            finalResult();
        }
    }

    public void finalResult(){
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching Nearby " +placeType+ ".\nNote: Searching speed depends on your Internet speed.");
        progressDialog.show();
        mService.getNearByPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                        progressDialog.dismiss();
                        currentPlace = response.body();
                        if(response.isSuccessful()){
                            for (int i=0; i<response.body().getResults().length; i++){
                                MarkerOptions markerOptions = new MarkerOptions();
                                Results googlePlace = response.body().getResults()[i];
                                double lat = Double.parseDouble(googlePlace.getGeometry().getLocation().getLat());
                                double lng = Double.parseDouble(googlePlace.getGeometry().getLocation().getLng());
                                String placeName = googlePlace.getName();
                                LatLng latLng = new LatLng(lat, lng);
                                markerOptions.position(latLng);
                                markerOptions.title(placeName);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                                markerOptions.snippet(String.valueOf(i));
                                mMap.addMarker(markerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {
                        progressDialog.dismiss();
                    }
                });

    }
    private String getUrl(double latitude , double longitude , String placeType) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+placeType);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyBkvnDOws6tM-CrdpEQRZE4PDSEp9PMkpA");

        Log.i("MapsActivity", "url = "+googlePlaceUrl.toString());
        Log.i("MapsActivity = ",""+latitude);
        Log.i("MapsActivity = ",""+PROXIMITY_RADIUS);

        return googlePlaceUrl.toString();
    }


    public boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;
        }else
            return true;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.subitem1:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.subitem2:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.subitem3:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.subitem4:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MapStateManager mgr = new MapStateManager(this);
        mgr.saveMapState(mMap);
    }
}
