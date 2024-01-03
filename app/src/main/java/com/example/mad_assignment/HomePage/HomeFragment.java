package com.example.mad_assignment.HomePage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment{

    String state;
    TextView temperature, CurrentLoacation;
    MainPage mainPage;

    LinearLayout GameBtn;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton logout = view.findViewById(R.id.logoutBtn);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        temperature = view.findViewById(R.id.temperature);
        CurrentLoacation = view.findViewById(R.id.state);


        WeatherAPICall();



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                userSignout();
            }
        });

        GameBtn = view.findViewById(R.id.gameWidget);
        GameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });






    }

    private void userSignout() {

        Intent intent = new Intent(getActivity(), First_page.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }



    public void weatherAPICall(View view) {
        temperature = view.findViewById(R.id.temperature);
        CurrentLoacation = view.findViewById(R.id.state);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyLocation", Context.MODE_PRIVATE);
        String savedLatitude = sharedPreferences.getString("latitude", "defaultLatitude");
        String savedLongitude = sharedPreferences.getString("longitude", "defaultLongitude");
        String savedState = sharedPreferences.getString("State","");

        String apiKey = "tBZ8jCBATn6gSS1Wxxid6UCB5c26OEK8";  // Replace with your actual API key
        String apiUrl = "https://api.tomorrow.io/v4/weather/realtime?location=" +savedState+","+savedLongitude+ "&apikey=" + apiKey;

        // Use HTTPS for security
        apiUrl = apiUrl.replace("http://", "https://");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(apiUrl, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                // Check for null values before accessing elements
                JSONObject data = response.optJSONObject("data");
                if (data != null) {
                    JSONObject values = data.optJSONObject("values");
                    if (values != null) {
                        String temp = values.optString("temperature");
                        temperature.setText(temp + "°C");
                        CurrentLoacation.setText(savedState);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle API request error
                error.printStackTrace();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity().getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }

    public void WeatherAPICall(){

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyLocation", Context.MODE_PRIVATE);
        String savedLatitude = sharedPreferences.getString("latitude", "-");
        String savedLongitude = sharedPreferences.getString("longitude", "-");
        String savedAddress = sharedPreferences.getString("Address", "-");
        String savedState = sharedPreferences.getString("State","");
        CurrentLoacation.setText(savedAddress);
        String apiKey = "tBZ8jCBATn6gSS1Wxxid6UCB5c26OEK8";
        String apiUrl = "https://api.tomorrow.io/v4/weather/realtime?location="+savedLatitude+","+savedLongitude+"&apikey=" + apiKey;

        //must implement com.android.volley:volley:1.2.1
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(apiUrl, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {

                    String temp = response.getJSONObject("data").getJSONObject("values").getString("temperature");
                    temperature.setText(temp+ "°C");


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