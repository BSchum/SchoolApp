package com.ynov.bsc.Activies;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.brice.messagemanager.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ynov.bsc.Adapter.SchoolAdapter;
import com.ynov.bsc.Model.School;
import com.ynov.bsc.Services.AddEntryService;
import com.ynov.bsc.Services.AsyncResponce;
import com.ynov.bsc.Services.GetEntrys;
import com.ynov.bsc.Services.ISchoolController;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Marker mCurrLocationMarker;
    ImageButton center;
    Map<Marker, ArrayList<String>> markerMap;
    int height = 100;
    int width = 100;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private ArrayList<School> schoolsArray;

    private boolean centered = false;

    public void OnMapSearch(View view){
        EditText locationSearch = (EditText) findViewById(R.id.searchbar);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listbutton, menu);
        final Menu mMenu = menu;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.list_button:
                Intent toList= new Intent(MapsActivity.this, ListEntryActivity.class);
                startActivity(toList);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mapFragment.getMapAsync(this);

        center = findViewById(R.id.center);
        markerMap = new HashMap<>();

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
        ImageButton searchbutton = findViewById(R.id.search);
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnMapSearch(v);
            }
        });
        Intent intent = getIntent();

        if(intent.getDoubleExtra("latitude", 9999) != 9999 && !centered){
            centered = true;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude", 0)), 12));
        }
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker == mCurrLocationMarker){
                    return true;
                }

                if(markerMap.get(marker) == null){
                    return true;
                }
                ArrayList<String> data =  markerMap.get(marker);
                BottomSheetMap bottomSheet = new BottomSheetMap();
                Bundle bundle = new Bundle();
                bundle.putString("nom",data.get(0));
                bundle.putString("addresse",data.get(1));
                bundle.putString("nbeleve",data.get(2));

                double currentLocLat = mCurrLocationMarker.getPosition().latitude;
                double currentLocLong = mCurrLocationMarker.getPosition().longitude;

                double markerLocLat = marker.getPosition().latitude;
                double markerLocLong = marker.getPosition().longitude;

                float distance = (float) Math.sqrt(Math.pow((markerLocLat - currentLocLat), 2) + Math.pow((markerLocLong - currentLocLong), 2));
                bundle.putFloat("distance", Math.round(distance*100));


                bottomSheet.setArguments(bundle);
                bottomSheet.show(getSupportFragmentManager(), "bottom_sheet_frag");

                return true;
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, 177);
            }
            return;
        }else{
            //Default centering and current positionning system of google, pretty good but with it we cant change pics.
            //mMap.setMyLocationEnabled(true);

            LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // Define a listener that responds to location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    if(mCurrLocationMarker != null){
                        mCurrLocationMarker.remove();
                    }
                    SetCurrentPos(location);
                    SetMapUI(location);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {}
                public void onProviderEnabled(String provider) {}
                public void onProviderDisabled(String provider) {}
            };

            // Register the listener with the Location Manager to receive location updates
            if (mLocationManager != null) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 50, locationListener);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
            }
            setSchoolsMarkers();

        }

    }

    // Get school marker from service and print them on map
    private void setSchoolsMarkers() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                prefs = PreferenceManager.getDefaultSharedPreferences(MapsActivity.this);
                prefsEditor = prefs.edit();
                Request request = original.newBuilder()
                        .header("AUTHORIZATION", prefs.getString("auth_token", "").substring(1, prefs.getString("auth_token", "").length() - 1))
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient.build();
        ISchoolController iSchoolController = new Retrofit.Builder()
                .baseUrl(ISchoolController.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(ISchoolController.class);
        prefs = PreferenceManager.getDefaultSharedPreferences(MapsActivity.this);

        iSchoolController.getSchool(prefs.getString("status", "")).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                schoolsArray = new ArrayList<>();
                JsonArray schools = response.body().get("schools").getAsJsonArray();
                for (int i = 0; i < schools.size(); i++) {
                    JsonObject entry = schools.get(i).getAsJsonObject();
                    LatLng latLng = new LatLng(entry.get("latitude").getAsDouble(), entry.get("longitude").getAsDouble());
                    MarkerOptions mKO = new MarkerOptions();
                    mKO.position(latLng);
                    Marker m = mMap.addMarker(mKO);

                    //Add to map the marker and his data
                    ArrayList<String> markerData = new ArrayList<String>();
                    markerData.add(entry.get("name").toString());
                    markerData.add(entry.get("address").toString());
                    markerData.add(entry.get("nb_student").toString());
                    markerMap.put(m, markerData);

                    //Color by student number
                    if(entry.get("nb_student").getAsInt() < 50){
                        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.markerred);
                        Bitmap b=bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                        m.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    }else if(entry.get("nb_student").getAsInt() >= 50 && entry.get("nb_student").getAsInt() < 200){
                        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.markerorange);
                        Bitmap b=bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                        m.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    }else{
                        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.markergreen);
                        Bitmap b=bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                        m.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
//        new GetEntrys(new AsyncResponce() {
//            @Override
//            publ void ComputeResult(Object result) {
//                String dataS = (String) result;
//                try{
//                    JSONArray datas = new JSONArray(dataS);
//                    //Pour chaque entrée dans le json, je rempli ma liste
//                    for(int i=0; i< datas.length(); i++){
//                        JSONObject entry = datas.getJSONObject(i);
//
//                        //Get latitude and longitude and create a marker
//                        LatLng latLng = new LatLng(Double.parseDouble(entry.getString("latitude")), Double.parseDouble(entry.getString("longitude")));
//                        MarkerOptions mKO = new MarkerOptions();
//                        mKO.position(latLng);
//                        Marker m = mMap.addMarker(mKO);
//
//                        //Add to map the marker and his data
//                        ArrayList<String> markerData = new ArrayList<String>();
//                        markerData.add(entry.getString("nom"));
//                        markerData.add(entry.getString("addresse"));
//                        markerData.add(entry.getString("nbEleve"));
//                        markerMap.put(m, markerData);
//
//                        //Color by student number
//                        if(Integer.parseInt(entry.getString("nbEleve")) < 50){
//                            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.markerred);
//                            Bitmap b=bitmapdraw.getBitmap();
//                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//                            m.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
//                        }else if(Integer.parseInt(entry.getString("nbEleve")) >= 50 && Integer.parseInt(entry.getString("nbEleve")) < 200){
//                            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.markerorange);
//                            Bitmap b=bitmapdraw.getBitmap();
//                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//                            m.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
//                        }else{
//                            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.markergreen);
//                            Bitmap b=bitmapdraw.getBitmap();
//                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//                            m.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
//                        }
//                    }
//                }catch (Exception e){
//                    Log.e("Error", "JSON Exception", e);
//                }
//            }
//        }).execute();
    }
    //Add current pos marker
    private void SetCurrentPos(Location l) {
        double lat = l.getLatitude();
        double lon = l.getLongitude();
        MarkerOptions mKO = new MarkerOptions();
        LatLng latlng = new LatLng(lat, lon);
        mKO.position(latlng);
        mCurrLocationMarker = mMap.addMarker(mKO);
    }
    //Center button
    View.OnClickListener genericOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.center:
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(mCurrLocationMarker.getPosition()));
                default:
                    break;
            }
        }
    };

    public void SetMapUI(Location l){
        //Changing current pos marker
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.mypos);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        mCurrLocationMarker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));

        // Moving the camera to be centered on the new position
        LatLng latlng = new LatLng(l.getLatitude(), l.getLongitude());

        if(!centered){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12));
            centered = true;
        }
        center.setOnClickListener(genericOnClickListener);
    }
}
