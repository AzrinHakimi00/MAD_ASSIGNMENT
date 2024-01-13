package com.example.mad_assignment.HomePage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AirPollutant#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AirPollutant extends Fragment {

    private static final int PERMISSION_LOCATION = 1000;
    private TextView date;
    TextView AQI_location;

    TextView PM10;
    TextView PM2_5;
    TextView O3;
    TextView NO2;
    TextView SO2;
    TextView CO;
    Button Btn_location;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AirPollutant() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AirPollutant.
     */
    // TODO: Rename and change types and number of parameters
    public static AirPollutant newInstance(String param1, String param2) {
        AirPollutant fragment = new AirPollutant();
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
        return inflater.inflate(R.layout.fragment_air_pollutant, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AQI_location = view.findViewById(R.id.AQI_Location);
        date = view.findViewById(R.id.AQI_Time);
        PM10 =view.findViewById(R.id.AQI_PM10V);
        PM2_5 = view.findViewById(R.id.AQI_PM25V);
        O3 = view.findViewById(R.id.AQI_O3V);
        NO2 =view.findViewById(R.id.AQI_NO2V);
        SO2 = view.findViewById(R.id.AQI_SO2V);
        CO = view.findViewById(R.id.AQI_COV);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());
        date.setText(currentDateandTime);

        AQIPollutants();
    }

    public void AQIPollutants(){

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyLocation", Context.MODE_PRIVATE);
        String savedLatitude = sharedPreferences.getString("latitude", "-");
        String savedLongitude = sharedPreferences.getString("longitude", "-");

        String apiKey = "bfae835a587c463187d4178050f47717";
        String apiUrl = "https://api.weatherbit.io/v2.0/current/airquality?lat=" + savedLatitude + "&lon=" + savedLongitude +"&key="+ apiKey;     //must implement com.android.volley:volley:1.2.1
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(apiUrl, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject mainObject = response.getJSONArray("data").getJSONObject(0);


                    PM10.setText(""+mainObject.getDouble("pm10"));
                    PM2_5.setText(""+mainObject.getDouble("pm25"));
                    O3.setText(""+mainObject.getDouble("o3"));
                    NO2.setText(""+mainObject.getDouble("no2"));
                    SO2.setText(""+mainObject.getDouble("so2"));
                    CO.setText(""+mainObject.getDouble("co"));

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue referenceQueue = Volley.newRequestQueue(getContext());
        referenceQueue.add(jsonObjectRequest);
    }
}