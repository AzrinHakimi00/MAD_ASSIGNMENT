package com.example.mad_assignment.HomePage;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlertListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertListFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationManager notificationManager;
    private AlertAdapter alertAdapter;
    private List<AlertFragment.WeatherEntry> weatherEntries;
    private static final int NOTIFICATION_ID = 1;
    TextView time;
    TextView message;
    ImageView weather;

    public AlertListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlertListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlertListFragment newInstance(String param1, String param2) {
        AlertListFragment fragment = new AlertListFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alert_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycleview);

//        List<Alert.WeatherEntry> alertList = new ArrayList<>(); // Replace with your actual data
//        AlertAdapter alertAdapter = new AlertAdapter(alertList);

        setAdapter();
        setAlert();
    }


    void setAlert(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyLocation", Context.MODE_PRIVATE);
        String savedLatitude = sharedPreferences.getString("latitude", "-");
        String savedLongitude = sharedPreferences.getString("longitude", "-");

        String apiKey = "bfae835a587c463187d4178050f47717";
        String apiUrl = "https://api.weatherbit.io/v2.0/forecast/hourly?lat="+savedLatitude+"&lon="+savedLongitude+"&key=" + apiKey+"&hours=72";

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
                            List<AlertFragment.WeatherEntry> weatherEntries = new ArrayList<>();
                            List<AlertFragment.WeatherEntry> Alert = new ArrayList<>();

                            JSONObject jsonRootObject = new JSONObject(response.toString());
                            JSONArray weatherList = jsonRootObject.getJSONArray("data");

                            for (int i = 0; i < weatherList.length(); i++) {
                                JSONObject weatherObject = weatherList.getJSONObject(i);

                                double temperature = weatherObject.getDouble("temp");
                                int id = weatherObject.getJSONObject("weather").getInt("code");
                                String time = weatherObject.getString("timestamp_local");

                                // Create a WeatherEntry object and add it to the list
                                AlertFragment.WeatherEntry weatherEntry = new AlertFragment.WeatherEntry(temperature, id, time);
                                weatherEntries.add(weatherEntry);
                            }

                            for(AlertFragment.WeatherEntry entry:weatherEntries){
                                if(entry.temperature >=30){
                                    Alert.add(entry);
                                }
                                switch (entry.id){
                                    case(501): case(502): case(503): case(504):
                                    case(200): case(201): case(202):
                                    case(210): case(211): case(212):
                                    case(221): case(230): case(231): case(232):
                                    case(701): case(711): case(721): case(741):
                                        Alert.add(entry);
                                        break;
                                }
                            }
//                                    for(int i =0;i<Alert.size();i++){
                            getDuration(Alert);
//                                    }

                            alertAdapter.setAlertList(Alert);
                            alertAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                                Toast.makeText(this,"efed",TOAST_LENGTH_SHORT);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);

    }
    void setAdapter() {
        recyclerView = getView().findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Initialize the adapter with an empty list
        alertAdapter = new AlertAdapter(new ArrayList<>());

        recyclerView.setAdapter(alertAdapter);
    }

    void getDuration(List<AlertFragment.WeatherEntry> alertlist) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE,ha dd-MM-yyyy", Locale.ENGLISH);
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
        int hours = 0;

        for (int i = 0; i < alertlist.size() - 1; i++) {
            String d1 = alertlist.get(i).getTime();
            String d2 = alertlist.get(i + 1).getTime();

            try {
                Date date1 = sdf.parse(d1);
                Date date2 = sdf.parse(d2);

                if (sdf2.format(date1).equals(sdf2.format(date2))) {
                    hours++;
                    alertlist.get(i).setDuration(hours);
                    alertlist.remove(i + 1);
                    i--;
                } else {
                    hours = 0; // Reset the duration when dates are different
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        // Set duration for the last entry (if any)
        if (!alertlist.isEmpty()) {
            alertlist.get(alertlist.size() - 1).setDuration(hours);
        }
    }
}