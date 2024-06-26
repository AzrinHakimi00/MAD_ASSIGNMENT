package com.example.mad_assignment.HomePage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment{

    String state;
    TextView temperature, weather,aqi, level;
    ImageView weatherIcon;
    ImageButton GameBtn;
    int AQI = 0;


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

        aqi = getView().findViewById(R.id.AQI);
        level = getView().findViewById(R.id.level);

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.rgb(244, 244, 244), Color.argb(102, 255, 238, 145)}
        );
        LinearLayout weatherWidget = view.findViewById(R.id.weatherWidget);
        weatherWidget.setBackground(gradientDrawable);
        gradientDrawable.setCornerRadius(50);

        weatherWidget.setOnClickListener(v -> {Navigation.findNavController(requireView()).navigate(R.id.alertFragment);});


        LinearLayout aqiWidget = view.findViewById(R.id.AirQualityWidget);
        aqiWidget.setOnClickListener(v -> {Navigation.findNavController(view).navigate(R.id.AQIFragment);});

        LinearLayout activityTracker = view.findViewById(R.id.activityTrackerWidget);
        activityTracker.setOnClickListener(v -> {Navigation.findNavController(view).navigate(R.id.userImpactFragment);});

        LinearLayout achievement = view.findViewById(R.id.achievementWidget);
        achievement.setOnClickListener(v -> {Navigation.findNavController(getView()).navigate(R.id.achievementFragment);});

        temperature = view.findViewById(R.id.temperature);
        weather = view.findViewById(R.id.weatherCode);


        ImageButton quizBtn = view.findViewById(R.id.quizBtn);
        quizBtn.setOnClickListener(v -> {Navigation.findNavController(view).navigate(R.id.quizFragment);});


        GameBtn = view.findViewById(R.id.snakeGameBtn);
        GameBtn.setOnClickListener(v -> {startActivity(new Intent(getActivity(), MainActivity.class));});

        WeatherAPICall();
        AQI();
    }



    private void userSignout() {

        Intent intent = new Intent(getActivity(), First_page.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }


    public void WeatherAPICall(){

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyLocation", Context.MODE_PRIVATE);
        String savedLatitude = sharedPreferences.getString("latitude", "-");
        String savedLongitude = sharedPreferences.getString("longitude", "-");
        String savedAddress = sharedPreferences.getString("Address", "-");

        String[] add = savedAddress.split(",");
        TextView locationTV = getView().findViewById(R.id.location);
        locationTV.setText(add[0]);

        String apiKey = "tBZ8jCBATn6gSS1Wxxid6UCB5c26OEK8";
        String apiUrl = "https://api.tomorrow.io/v4/weather/realtime?location="+savedLatitude+","+savedLongitude+"&apikey=" + apiKey;

        //must implement com.android.volley:volley:1.2.1
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(apiUrl, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {

                    String temp = response.getJSONObject("data").getJSONObject("values").getString("temperature");
                    String weatherCode = response.getJSONObject("data").getJSONObject("values").getString("weatherCode");

                    Double roundTemp = Double.valueOf(temp);
                    DecimalFormat decimalFormat = new DecimalFormat("#.#");

                    // Format the number to one decimal point
                    String formattedTemp = decimalFormat.format(roundTemp);
                    temperature.setText(formattedTemp+ "°");

                    checkWeatherCode(weatherCode);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue referenceQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        referenceQueue.add(jsonObjectRequest);
    }




    private void checkWeatherCode(String weatherCode){
        weatherIcon = getView().findViewById(R.id.weatherIcon);


        switch (weatherCode){
            case "1000":
                weather.setText("Clear");
                Picasso.get().load("https://files.readme.io/48b265b-weather_icon_small_ic_clear3x.png").into(weatherIcon);
                break;
            case "1100":
                weather.setText("Mostly Clear");
                Picasso.get().load("https://files.readme.io/c3d2596-weather_icon_small_ic_mostly_clear3x.png").into(weatherIcon);
                break;
            case "1101":
                weather.setText("Partly Cloudy");
                Picasso.get().load("https://files.readme.io/5ef9011-weather_icon_small_ic_partly_cloudy3x.png").into(weatherIcon);
                break;
            case "1102":
                weather.setText("Mostly Cloudy");
                Picasso.get().load("https://files.readme.io/6beaa54-weather_icon_small_ic_mostly_cloudy3x.png").into(weatherIcon);
                break;
            case "1001":
                weather.setText("Cloudy");
                Picasso.get().load("https://files.readme.io/4042728-weather_icon_small_ic_cloudy3x.png").into(weatherIcon);
                break;

            case "4000":
                weather.setText("Drizzle");
                Picasso.get().load("https://files.readme.io/f22e925-weather_icon_small_ic_rain_drizzle3x.png").into(weatherIcon);
                break;
            case "4001":
                weather.setText("Rain");
                Picasso.get().load("https://files.readme.io/aab8713-weather_icon_small_ic_rain3x.png").into(weatherIcon);
                break;
            case "4200":
                weather.setText("Light Rain");
                Picasso.get().load("https://files.readme.io/ea98852-weather_icon_small_ic_rain_light3x.png").into(weatherIcon);
                break;
            case "4201":
                weather.setText("Heavy Rain");
                Picasso.get().load("https://files.readme.io/fdacbb8-weather_icon_small_ic_rain_heavy3x.png").into(weatherIcon);
                break;
            case "8000":
                weather.setText("Thunderstorm");
                Picasso.get().load("https://files.readme.io/39fb806-weather_icon_small_ic_tstorm3x.png").into(weatherIcon);
                break;

        }
    }

    public void AQI() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyLocation", Context.MODE_PRIVATE);
        String savedLatitude = sharedPreferences.getString("latitude", "-");
        String savedLongitude = sharedPreferences.getString("longitude", "-");

        String apiKey = "bfae835a587c463187d4178050f47717";
        String apiUrl = "https://api.weatherbit.io/v2.0/forecast/airquality?lat=" + savedLatitude + "&lon=" + savedLongitude + "&key=" + apiKey;

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,  // Adjust the method based on your API requirements
                apiUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //JSONObject json = new JSONObject(jsonData);
                            JSONArray dataList = response.getJSONArray("data");

                            JSONObject currentData = dataList.getJSONObject(0);

                            int AQI = currentData.getInt("aqi");
                            aqi.setText(String.valueOf(AQI));
                            setTextViewWithColor(level,AQI);



                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }


    public void setTextViewWithColor(TextView textView, int averageAQI) {
        if(averageAQI<51) {
            textView.setText("GOOD");
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.GOOD));}
        else if(averageAQI<101){
            textView.setText("MODERATE");
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.MODERATE));}
        else if(averageAQI<151){
            textView.setText("POOR");
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.POOR));}
        else if(averageAQI<201){
            textView.setText("VERYPOOR");
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.VERYPOOR));}
        else{
            textView.setText("HAZARDOUS");
            textView.setTextColor(ContextCompat.getColor(requireContext(),R.color.black));}
    }


}