package com.example.mad_assignment.HomePage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
    LocationManager locationManager;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        firebaseAuth = FirebaseAuth.getInstance();

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

    public String getAddress() {
        return address;
    }

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

        Toast.makeText(this, ""+location.getLatitude()+", "+ location.getLongitude(), Toast.LENGTH_LONG).show();
        try {
            Geocoder geocoder = new Geocoder(MainPage.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            address = addresses.get(0).getLocality();

            HashMap<String,String> loc = new HashMap<>();
            loc.put("Country",addresses.get(0).getCountryName());
            loc.put("State",addresses.get(0).getLocality());
            loc.put("Address",addresses.get(0).getAddressLine(0));
            loc.put("Latitude", String.valueOf(location.getLatitude()));
            loc.put("Longitude", String.valueOf(location.getLongitude()));
            reference.setValue(loc);


            TextView add = findViewById(R.id.address);
            add.setText(address);
            //Toast.makeText(this, address, Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}