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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.transition.TransitionInflater;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AQIFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AQIFragment extends Fragment {

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
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
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



//    @SuppressLint("SetTextI18n")
//    public void AQI(){
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyLocation", Context.MODE_PRIVATE);
//        String savedLatitude = sharedPreferences.getString("latitude", "-");
//        String savedLongitude = sharedPreferences.getString("longitude", "-");
//        String savedAddress = sharedPreferences.getString("Address", "-");
//        location.setText(savedAddress);
//
//        String apiKey = "f6b0e9e985d5c35e9e2834c0546415e1";
//        String apiUrl = "https://api.openweathermap.org/data/2.5/air_pollution/forecast?lat="+savedLatitude+"&lon="+savedLongitude+"&appid="+apiKey;
//
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(apiUrl, new Response.Listener<JSONObject>() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    int index = 0;
//                    int point = 20;
//                    for(int i=0; i<4; i++){
//                        int sumAQI = 0;
//                        for(int j=index; j<point; j++){
//                            String aqiToday = response.getJSONArray("list").getJSONObject(j).getJSONObject("main").getString("aqi");
//                            int aqi = Integer.parseInt(aqiToday);
//                            sumAQI += aqi;
//
//                        }
//                        int averageAQI = sumAQI/20;
//                        switch (i) {
//                            case (0):
//                                todayv.setText("" + averageAQI);
//                                setTextViewWithColor(lvl,averageAQI);
//                                break;
//                            case (1):
//                                tmrwv.setText("" + averageAQI);
//                                break;
//                            case (2):
//                                tmrw2v.setText("" + averageAQI);
//                                break;
//                            case (3):
//                                tmrw3v.setText("" + averageAQI);
//                                break;
//                        }
//                        index += 20;
//                        point += 20;
//                    }
//
//
//
//                } catch (JSONException e) {
//                    Toast.makeText(getActivity().getApplicationContext(),""+e,Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//
//        RequestQueue referenceQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
//        referenceQueue.add(jsonObjectRequest);
//
//
//    }

    public void AQI() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyLocation", Context.MODE_PRIVATE);
        String savedLatitude = sharedPreferences.getString("latitude", "-");
        String savedLongitude = sharedPreferences.getString("longitude", "-");
        String savedAddress = sharedPreferences.getString("Address", "-");
        location.setText(savedAddress);

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
                            List<String> listdate = new ArrayList<>();
                            List<Integer> listaqi = new ArrayList<>();
                            // Calculate daily average AQI for today and the next 3 days

                            for (int i = 0; i < dataList.length(); i++) {
                                JSONObject data = dataList.getJSONObject(i);
                                listaqi.add(data.getInt("aqi"));
                                listdate.add(data.getString("timestamp_local"));
                            }

                            int count = 0;
                            int count2 = 0;
                            int sumAQI = listaqi.get(0);
                            int averageAQI = 0;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                            for (int i = 0; i < listdate.size(); i++) {
                                count++;
                                if(i<listdate.size()-1){
                                    Date date = sdf.parse(listdate.get(i));
                                    Date date2 = sdf.parse(listdate.get(i + 1));
                                    if (date.compareTo(date2)==0) {
                                        sumAQI += listaqi.get(i+1);
                                    }else {
                                        averageAQI = sumAQI/count;
                                        switch (count2){
                                            case 0:
                                                todayv.setText(String.valueOf(averageAQI));
                                                setTextViewWithColor(todayv,averageAQI);
                                                setTextViewWithColor(lvl, averageAQI);
                                                int color = lvl.getCurrentTextColor();
                                                if (color == ContextCompat.getColor(requireContext(), R.color.GOOD)) {
                                                    lvl.setText("GOOD");
                                                } else if (color == ContextCompat.getColor(requireContext(), R.color.MODERATE)) {
                                                    lvl.setText("MODERATE");
                                                } else if (color == ContextCompat.getColor(requireContext(), R.color.POOR)) {
                                                    lvl.setText("POOR");
                                                } else if (color == ContextCompat.getColor(requireContext(), R.color.VERYPOOR)) {
                                                    lvl.setText("VERY POOR");
                                                } else if (color == ContextCompat.getColor(requireContext(), R.color.black)) {
                                                    lvl.setText("HAZARDOUS");
                                                }
                                                break;
                                            case 1:
                                                tmrwv.setText(String.valueOf(averageAQI));
                                                setTextViewWithColor(tmrwv,averageAQI);
                                                break;
                                            case 2:
                                                tmrw2v.setText(String.valueOf(averageAQI));
                                                setTextViewWithColor(tmrw2v,averageAQI);
                                                break;
                                            case 3:
                                                tmrw3v.setText(String.valueOf(averageAQI));
                                                setTextViewWithColor(tmrw3v,averageAQI);
                                                break;
                                        }
                                        sumAQI=0;
                                        count=0;
                                        count2++;
                                    }
                                }else{
                                    averageAQI = sumAQI/count;
                                    tmrw3v.setText(String.valueOf(averageAQI));
                                    setTextViewWithColor(tmrw3v,averageAQI);
                                }
                            }
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
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.GOOD));}
        else if(averageAQI<101){
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.MODERATE));}
        else if(averageAQI<151){
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.POOR));}
        else if(averageAQI<201){
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.VERYPOOR));}
        else{
            textView.setTextColor(ContextCompat.getColor(requireContext(),R.color.black));}
    }

//    public void setTextViewWithColor(TextView textView, int averageAQI) {
//        switch (averageAQI) {
//            case 1:
//                textView.setText("Good");
//                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.GOOD));
//
//                break;
//            case 2:
//                textView.setText("Fair");
//                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.FAIR));
//
//                break;
//            case 3:
//                textView.setText("Moderate");
//                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.MODERATE));
//
//                break;
//            case 4:
//                textView.setText("Poor");
//                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.POOR));
//
//                break;
//            case 5:
//                textView.setText("Very Poor");
//                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.VERYPOOR));
//
//                break;
//        }
//    }



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