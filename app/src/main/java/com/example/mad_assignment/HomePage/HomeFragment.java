package com.example.mad_assignment.HomePage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.mad_assignment.AccountManagement.QuizFragment;
import com.example.mad_assignment.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment{

    String state;
    TextView temperature, CurrentLoacation;
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

        FloatingActionButton logout = view.findViewById(R.id.logoutBtn);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        temperature = view.findViewById(R.id.temperature);
        CurrentLoacation = view.findViewById(R.id.state);


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

    private void replaceFragment(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.FCVmainpage, fragment)
                    .addToBackStack(null)
                    .commit();
        }
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