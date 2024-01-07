package com.example.mad_assignment.HomePage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mad_assignment.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AQIFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AQIFragment extends Fragment {

    private static final int PERMISSION_LOCATION = 1000;
    TextView date;
    TextView time;
    TextView location;
    TextView lvl;
    TextView today;
    TextView tmrw;
    TextView tmrw2;
    TextView tmrw3;
    TextView todayv;
    TextView tmrwv;
    TextView tmrw2v;
    TextView tmrw3v;
    Button detail;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AQIFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AQIFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AQIFragment newInstance(String param1, String param2) {
        AQIFragment fragment = new AQIFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aqi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        location = view.findViewById(R.id.AQI_Location);
        lvl =  view.findViewById(R.id.AQI_Lvl);
        today =  view.findViewById(R.id.AQI_Date);
        tmrw =  view.findViewById(R.id.AQI_DateTmrw0);
        tmrw2 =  view.findViewById(R.id.AQI_DateTmrw);
        tmrw3 =  view.findViewById(R.id.AQI_DateTmrw2);
        todayv =  view.findViewById(R.id.AQI_Value);
        tmrwv =  view.findViewById(R.id.AQI_ValueTmrw0);
        tmrw2v =  view.findViewById(R.id.AQI_ValueTmrw);
        tmrw3v =  view.findViewById(R.id.AQI_ValueTmrw2);
        detail =  view.findViewById(R.id.Btn_details);

        Date();

        AQI();


        detail.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.airPollutant);
        });


    }



    //show location as string
    private String hereLocation(Location location) {
        try {
            if (Geocoder.isPresent()) {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    return address.getAdminArea() + ", " + address.getCountryName();
                }
            } else {
                return "Geocoder is not available";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error retrieving location";
        }
        return "Lat: " + location.getLatitude() + "\nLong: " + location.getLongitude();
    }


    @SuppressLint("SetTextI18n")
    public void AQI(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyLocation", Context.MODE_PRIVATE);
        String savedLatitude = sharedPreferences.getString("latitude", "-");
        String savedLongitude = sharedPreferences.getString("longitude", "-");
        String savedAddress = sharedPreferences.getString("Address", "-");
        location.setText(savedAddress);

        String apiKey = "f6b0e9e985d5c35e9e2834c0546415e1";
        String apiUrl = "https://api.openweathermap.org/data/2.5/air_pollution/forecast?lat="+savedLatitude+"&lon="+savedLongitude+"&appid="+apiKey;


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(apiUrl, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {

                    String aqiToday = response.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("aqi");
                    todayv.setText(aqiToday);

                    String aqi1 = response.getJSONArray("list").getJSONObject(1).getJSONObject("main").getString("aqi");
                    tmrwv.setText(aqi1);

                    String aqi2 = response.getJSONArray("list").getJSONObject(2).getJSONObject("main").getString("aqi");
                    tmrw2v.setText(aqi2);

                    String aqi3 = response.getJSONArray("list").getJSONObject(3).getJSONObject("main").getString("aqi");
                    tmrw3v.setText(aqi3);


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



    public void Date(){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MM-yyyy");
        String todayS = sdf.format(date);

        calendar.add(Calendar.DAY_OF_MONTH,1);
        Date tomorrowDate2 = calendar.getTime();
        SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE, dd-MM-yyyy");
        String tmrw2S = sdf2.format(tomorrowDate2);

        calendar.add(Calendar.DAY_OF_MONTH,1);
        Date tomorrowDate3 = calendar.getTime();
        SimpleDateFormat sdf3 = new SimpleDateFormat("EEEE, dd-MM-yyyy");
        String tmrw3S = sdf3.format(tomorrowDate3);

        calendar.add(Calendar.DAY_OF_MONTH,1);
        Date tomorrowDate4 = calendar.getTime();
        SimpleDateFormat sdf4 = new SimpleDateFormat("EEEE, dd-MM-yyyy");
        String tmrw4S = sdf4.format(tomorrowDate4);

        today.setText(todayS);
        tmrw.setText(tmrw2S);
        tmrw2.setText(tmrw3S);
        tmrw3.setText(tmrw4S);
    }
}