package com.example.mad_assignment.HomePage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.mad_assignment.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainPage extends AppCompatActivity implements LocationListener {

    FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("MyLocation", Context.MODE_PRIVATE);

        if (ContextCompat.checkSelfPermission(MainPage.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainPage.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.FCVmainpage);

        NavController navController = navHostFragment.getNavController();

        setUpBottomNavBar(navController);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getLocation();
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

            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("Country",addresses.get(0).getCountryName());
            editor.putString("State",addresses.get(0).getLocality());
            editor.putString("Address",addresses.get(0).getAddressLine(0));
            editor.putString("latitude", String.valueOf(location.getLatitude()));
            editor.putString("longitude", String.valueOf(location.getLongitude()));

              // Apply the changes
            editor.apply();

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
}