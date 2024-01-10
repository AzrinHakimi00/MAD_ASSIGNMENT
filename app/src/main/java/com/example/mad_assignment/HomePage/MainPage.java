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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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
import java.util.Map;

public class MainPage extends AppCompatActivity implements LocationListener {

    FirebaseAuth firebaseAuth;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor));
        }


        Toolbar toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);




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

            SharedPreferences sharedPreferences = getSharedPreferences("MyLocation",MODE_PRIVATE);
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("Country",addresses.get(0).getCountryName());
            editor.putString("State",addresses.get(0).getLocality());
            editor.putString("Address",addresses.get(0).getAddressLine(0));
            editor.putString("Latitude", String.valueOf(location.getLatitude()));
            editor.putString("Longitude", String.valueOf(location.getLongitude()));


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

    public void AddForumMember(){

// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

// Define the URL
        String url = "https://api.chatengine.io/chats/{{chat_id}}/people/";

// Create a JSONObject for the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", "bob_baker");
        } catch (JSONException e) {
            e.printStackTrace();
        }

// Create a POST request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Log.e("Error", "Error occurred", error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Add headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Project-ID", "121180d3-8c29-47ae-a10e-7371c876b528");
                headers.put("User-Name", "{{user_name}}");
                headers.put("User-Secret", "{{user_secret}}");
                return headers;
            }
        };

// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);


    }


}