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
import androidx.core.content.ContextCompat;
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

    String state;
    TextView temperature, weather,aqi, level;
    ImageView weatherIcon;

    ImageButton GameBtn;

    View aqi1,aqi2,aqi3,aqi4,aqi5;
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


        LinearLayout aqiWidget = view.findViewById(R.id.AirQualityWidget);
        aqiWidget.setOnClickListener(v -> {Navigation.findNavController(view).navigate(R.id.AQIFragment);});

        FloatingActionButton logout = view.findViewById(R.id.logoutBtn);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        temperature = view.findViewById(R.id.temperature);
        weather = view.findViewById(R.id.weatherCode);

        setAQIWidget();
        WeatherAPICall();


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


    private void setAQIWidget(){
        AQI();
        aqi1 = getView().findViewById(R.id.aqi1);
        aqi2 = getView().findViewById(R.id.aqi2);
        aqi3 = getView().findViewById(R.id.aqi3);
        aqi4 = getView().findViewById(R.id.aqi4);
        aqi5 = getView().findViewById(R.id.aqi5);

        aqi1.setVisibility(View.GONE);
        aqi2.setVisibility(View.GONE);
        aqi3.setVisibility(View.GONE);
        aqi4.setVisibility(View.GONE);
        aqi5.setVisibility(View.GONE);






    }


    public void WeatherAPICall(){

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyLocation", Context.MODE_PRIVATE);
        String savedLatitude = sharedPreferences.getString("latitude", "-");
        String savedLongitude = sharedPreferences.getString("longitude", "-");
        String savedAddress = sharedPreferences.getString("Address", "-");
        String savedState = sharedPreferences.getString("State","");

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
    public void AQI(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyLocation", Context.MODE_PRIVATE);
        String savedLatitude = sharedPreferences.getString("latitude", "-");
        String savedLongitude = sharedPreferences.getString("longitude", "-");

        String apiKey = "f6b0e9e985d5c35e9e2834c0546415e1";
        String apiUrl = "https://api.openweathermap.org/data/2.5/air_pollution/forecast?lat="+savedLatitude+"&lon="+savedLongitude+"&appid="+apiKey;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(apiUrl, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int sumAQI = 0;
                    for (int j = 0; j < 24; j++) {
                        String aqiToday = response.getJSONArray("list").getJSONObject(j).getJSONObject("main").getString("aqi");
                        int aqi = Integer.parseInt(aqiToday);
                        sumAQI += aqi;
                    }
                    int averageAQI = sumAQI / 24;
                    AQI = averageAQI;

                    aqi.setText(""+averageAQI);
                    switch (averageAQI) {
                        case 1:
                            aqi1.setVisibility(View.VISIBLE);
                            level.setText("Good");
                            break;
                        case 2:
                            aqi2.setVisibility(View.VISIBLE);
                            level.setText("Fair");
                            break;
                        case 3:
                            aqi3.setVisibility(View.VISIBLE);
                            level.setText("Moderate");

                            break;
                        case 4:
                            aqi4.setVisibility(View.VISIBLE);
                            level.setText("Poor");

                            break;
                        case 5:
                            aqi5.setVisibility(View.VISIBLE);
                            level.setText("Very Poor");
                            break;
                    }

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


}