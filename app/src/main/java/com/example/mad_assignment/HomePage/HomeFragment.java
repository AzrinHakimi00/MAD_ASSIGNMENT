package com.example.mad_assignment.HomePage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mad_assignment.AccountManagement.First_page;
import com.example.mad_assignment.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment{

    private APIcache dataCache;
    String state;
    TextView temperature, weatherTV;
    ImageView weatherIcon;
    MainPage mainPage;

    ImageButton GameBtn;


    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.fade));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.rgb(244, 244, 244), Color.argb(102, 255, 238, 145)}
        );
        LinearLayout weatherWidget = view.findViewById(R.id.weatherWidget);
        weatherWidget.setBackground(gradientDrawable);
        gradientDrawable.setCornerRadius(50);


        LinearLayout aqiWidget = view.findViewById(R.id.AirQualityWidget);
        aqiWidget.setOnClickListener(v -> {Navigation.findNavController(view).navigate(R.id.AQIFragment);});

        FloatingActionButton logout = view.findViewById(R.id.logoutBtn);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        temperature = view.findViewById(R.id.temperature);
        weatherTV = view.findViewById(R.id.weatherCode);

        dataCache = new APIcache(getContext());
        WeatherWidget();


        ImageButton quizBtn = view.findViewById(R.id.quizBtn);
        quizBtn.setOnClickListener(v -> {Navigation.findNavController(view).navigate(R.id.quizFragment);});

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                userSignout();
            }
        });

        GameBtn = view.findViewById(R.id.snakeGameBtn);
        GameBtn.setOnClickListener(v -> {startActivity(new Intent(getActivity(), MainActivity.class));});


    }



    private void userSignout() {

        Intent intent = new Intent(getActivity(), First_page.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }


    public void WeatherWidget(){
        weatherIcon = getView().findViewById(R.id.weatherIcon);

        String temp = dataCache.getCachedTemperature();
        temperature.setText(temp);
        String weather = dataCache.getCachedWeather();
        weatherTV.setText(weather);

        String weatherIconURL = dataCache.getCachedWeatherIcon();
        Picasso.get().load(weatherIconURL).into(weatherIcon);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyLocation", Context.MODE_PRIVATE);
        String savedAddress = sharedPreferences.getString("Address", "-");
        String[] add = savedAddress.split(",");
        TextView locationTV = getView().findViewById(R.id.location);
        locationTV.setText(add[0]);
    }












}