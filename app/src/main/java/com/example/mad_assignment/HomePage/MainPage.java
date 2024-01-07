package com.example.mad_assignment.HomePage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mad_assignment.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainPage extends AppCompatActivity implements LocationListener {
    private APIcache dataCache;
    FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);


        Toolbar toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        dataCache = new APIcache(this);

        String cachedTemperature = dataCache.getCachedTemperature();
        String cachedWeather = dataCache.getCachedWeather();
        String cachedWeatherIcon = dataCache.getCachedWeatherIcon();

        if (cachedTemperature == null || cachedWeather == null || cachedWeatherIcon == null
                || cachedTemperature.isEmpty() || cachedWeather.isEmpty() || cachedWeatherIcon.isEmpty()) {
            // If any of the cached items is null or empty, make the API call to fetch new data
            WeatherAPICall();
        }



        firebaseAuth = FirebaseAuth.getInstance();



        if (ContextCompat.checkSelfPermission(MainPage.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainPage.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.FCVmainpage);
        NavController navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this,navController,appBarConfiguration);

        setUpBottomNavBar(navController);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getLocation();
        }

    }

    private void loadMainFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.FCVmainpage, new HomeFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // If there are fragments in the back stack, pop the top one
            getSupportFragmentManager().popBackStack();
        } else {
            // If the back stack is empty, handle the back button as normal
            super.onBackPressed();
        }
    }



    //--------------------------------------------------------------------------------------------------------------------//
    private void setUpBottomNavBar(NavController navController) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {
            Navigation.findNavController(this, R.id.FCVmainpage).navigate(item.getItemId());
            return true;
        } catch (Exception e) {
            return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this,R.id.FCVmainpage).navigateUp();
    }

    //--------------------------------------------------------------------------------------------------------------------//
    @RequiresApi(api = Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    public void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 5000, 5, MainPage.this);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("Users Location").child(user.getUid());

        try {
            Geocoder geocoder = new Geocoder(MainPage.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);


            HashMap<String,String> loc = new HashMap<>();
            loc.put("Country",addresses.get(0).getCountryName());
            loc.put("State",addresses.get(0).getLocality());
            loc.put("Address",addresses.get(0).getAddressLine(0));
            loc.put("Latitude", String.valueOf(location.getLatitude()));
            loc.put("Longitude", String.valueOf(location.getLongitude()));
            reference.setValue(loc);

            //Toast.makeText(this, address, Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void WeatherAPICall() {


        SharedPreferences sharedPreferences = getSharedPreferences("MyLocation", Context.MODE_PRIVATE);
        String savedLatitude = sharedPreferences.getString("latitude", "-");
        String savedLongitude = sharedPreferences.getString("longitude", "-");

        String apiKey = "tBZ8jCBATn6gSS1Wxxid6UCB5c26OEK8";
        String apiUrl = "https://api.tomorrow.io/v4/weather/realtime?location=" + savedLatitude + "," + savedLongitude + "&apikey=" + apiKey;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String temp = response.getJSONObject("data").getJSONObject("values").getString("temperature");
                    String weatherCode = response.getJSONObject("data").getJSONObject("values").getString("weatherCode");

                    Double roundTemp = Double.valueOf(temp);
                    DecimalFormat decimalFormat = new DecimalFormat("#.#");
                    String formattedTemp = decimalFormat.format(roundTemp);

                    dataCache.saveTemperature(formattedTemp);
                    checkWeatherCode(weatherCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("WeatherAPICall", "Error parsing JSON response: " + e.getMessage());
                    // Handle the error, show a message, or take appropriate action
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("WeatherAPICall", "Volley Error: " + error.getMessage());
                // Handle the error, show a message, or take appropriate action
            }
        });

        RequestQueue referenceQueue = Volley.newRequestQueue(this.getApplicationContext());
        referenceQueue.add(jsonObjectRequest);
    }



    private void checkWeatherCode(String weatherCode){
        switch (weatherCode){
            case "1000":
                dataCache.saveWeather("Clear");
                dataCache.saveWeatherIcon("https://files.readme.io/48b265b-weather_icon_small_ic_clear3x.png");
                break;
            case "1100":
                dataCache.saveWeather("Mostly Clear");
                dataCache.saveWeatherIcon("https://files.readme.io/c3d2596-weather_icon_small_ic_mostly_clear3x.png");
                break;
            case "1101":
                dataCache.saveWeather("Partly Cloudy");
                dataCache.saveWeatherIcon("https://files.readme.io/5ef9011-weather_icon_small_ic_partly_cloudy3x.png");
                break;
            case "1102":
                dataCache.saveWeather("Mostly Cloudy");
                dataCache.saveWeatherIcon("https://files.readme.io/6beaa54-weather_icon_small_ic_mostly_cloudy3x.png");
                break;
            case "1001":
                dataCache.saveWeather("Cloudy");
                dataCache.saveWeatherIcon("https://files.readme.io/4042728-weather_icon_small_ic_cloudy3x.png");
                break;
            case "4000":
                dataCache.saveWeather("Drizzle");
                dataCache.saveWeatherIcon("https://files.readme.io/f22e925-weather_icon_small_ic_rain_drizzle3x.png");
                break;
            case "4001":
                dataCache.saveWeather("Rain");
                dataCache.saveWeatherIcon("https://files.readme.io/aab8713-weather_icon_small_ic_rain3x.png");
                break;
            case "4200":
                dataCache.saveWeather("Light Rain");
                dataCache.saveWeatherIcon("https://files.readme.io/ea98852-weather_icon_small_ic_rain_light3x.png");
                break;
            case "4201":
                dataCache.saveWeather("Heavy Rain");
                dataCache.saveWeatherIcon("https://files.readme.io/fdacbb8-weather_icon_small_ic_rain_heavy3x.png");
                break;
            case "8000":
                dataCache.saveWeather("Thunderstorm");
                dataCache.saveWeatherIcon("https://files.readme.io/39fb806-weather_icon_small_ic_tstorm3x.png");
                break;

        }
    }

}