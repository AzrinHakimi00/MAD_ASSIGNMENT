package com.example.mad_assignment.HomePage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertFragment extends Fragment {

    TextView time1;
    TextView location;
    TextView message;
    ImageView weather;
    TextView advice;
    Button alert;
    TextView duration;
    List<AlertFragment.WeatherEntry> weatherEntries = new ArrayList<>();
    static List<AlertFragment.WeatherEntry> alertlist = new ArrayList<>();
    public AlertFragment() {
        // Required empty public constructor
    }


    public static AlertFragment newInstance(String param1, String param2) {
        AlertFragment fragment = new AlertFragment();
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
        return inflater.inflate(R.layout.fragment_alert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        time1 = view.findViewById(R.id.Alert_Time);
        message = view.findViewById(R.id.Message);
        location = view.findViewById(R.id.location);
        weather = view.findViewById(R.id.Weather);
        alert = view.findViewById(R.id.Alert);
        advice = view.findViewById(R.id.Advice);
        duration= view.findViewById(R.id.Alert_Duration);

        alert.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigate(R.id.alertListFragment);
        });

        alert();
    }


    public void alert() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyLocation", Context.MODE_PRIVATE);
        String savedLatitude = sharedPreferences.getString("latitude", "-");
        String savedLongitude = sharedPreferences.getString("longitude", "-");
        String savedAddress = sharedPreferences.getString("Address", "-");

        location.setText(savedAddress);

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

                            JSONObject jsonRootObject = new JSONObject(response.toString());
                            JSONArray weatherList = jsonRootObject.getJSONArray("data");

                            for (int i = 0; i < weatherList.length(); i++) {
                                JSONObject weatherObject = weatherList.getJSONObject(i);

                                double temperature = weatherObject.getDouble("temp");
                                int id = weatherObject.getJSONObject("weather").getInt("code");
                                String time = weatherObject.getString("timestamp_local");

                                // Create a WeatherEntry object and add it to the list
                                WeatherEntry weatherEntry = new WeatherEntry(temperature, id, time);
                                weatherEntries.add(weatherEntry);
                            }

                            for(AlertFragment.WeatherEntry entry:weatherEntries){
                                if(entry.temperature>31){
                                    alertlist.add(entry);
                                }
                                switch (entry.id){
                                    case(501): case(502): case(503): case(504):
                                    case(200): case(201): case(202):
                                    case(210): case(211): case(212):
                                    case(221): case(230): case(231): case(232):
                                    case(701): case(711): case(721): case(741):
                                        alertlist.add(entry);
                                        break;
                                }
                            }
                            if(!alertlist.isEmpty()) {
                                getDuration((ArrayList<WeatherEntry>) alertlist);
                                time1.setText(alertlist.get(0).getTime());
                                message.setText(alertlist.get(0).getMessage());
                                weather.setImageDrawable(alertlist.get(0).getIconDrawable(requireContext()));
                                duration.setText("~"+alertlist.get(0).duration+" hours");
                            }else{
                                time1.setText("");
                                message.setText("Rest assure! Your place has no extreme weather for the next 5 days");
                                weather.setImageResource(R.drawable.clear_day);
                            }
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


    public static class WeatherEntry {
        public double temperature;
        public int id;
        public String time;
        public int duration;

        public WeatherEntry(double temperature, int id, String time) {
            this.temperature = temperature;
            this.id = id;//502-504 heavyrain, 200-202,210-212,221,230-232 thunderstorm, 701,711,721,741 fog etc.
            this.time = time;
            this.duration = 1;
        }

        String getType() {
            if (temperature > 31) {
                return "Blazing hot";
            }
            switch (id) {
                case 501:
                case 502:
                case 503:
                case 504:
                    return "Heavy rain";
                case 200:
                case 201:
                case 202:
                case 210:
                case 211:
                case 212:
                case 221:
                case 230:
                case 231:
                case 232:
                    return "Thunderstorm";
                case 701:
                case 711:
                case 721:
                case 741:
                    return "Foggy";
                default:
                    // You can throw an exception or return a default drawable
                    // For example, returning R.drawable.default_icon
                    return "";
            }
        }

        String getMessage() {
            if (temperature > 31) {
                return "Its going to be scorching outside! Drink plenty of water and stay hydrated!";
            }
            switch (id) {
                case 501:
                case 502:
                case 503:
                case 504:
                    return "Beware! Anticipate substantial rainfall at your location. Please prepare an umbrella or raincoat before going outside!";
                case 200:
                case 201:
                case 202:
                case 210:
                case 211:
                case 212:
                case 221:
                case 230:
                case 231:
                case 232:
                    return "Uh oh! A thunderstorm gonna bombard your place soon. Avoid going outside for your own safety.";
                case 701:
                case 711:
                case 721:
                case 741:
                    return "Its gonna be hard to walk around outside. Watch out for your surrounding!";
                default:
                    // You can throw an exception or return a default drawable
                    // For example, returning R.drawable.default_icon
                    return "Rest assure! Your place has no extreme weather for the next 5 days";
            }
        }

        String getTime() {
            try {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date = inputFormat.parse(time);

                // Format the Date object into the desired output format
                @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, ha dd-MM-yyyy");
                String outputDate = outputFormat.format(date);
                return outputDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        int setDuration(int duratio) {
            return this.duration = duratio;
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable getIconDrawable(Context context) {
            Resources resources = context.getResources(); // Assuming this method is within an Activity or Context
            if (temperature > 31) {
                return resources.getDrawable(R.drawable.hot);
            }
            switch (id) {
                case 501:
                case 502:
                case 503:
                case 504:
                    return resources.getDrawable(R.drawable.heavyrain);
                case 200:
                case 201:
                case 202:
                case 210:
                case 211:
                case 212:
                case 221:
                case 230:
                case 231:
                case 232:
                    return resources.getDrawable(R.drawable.thunderstorm);
                case 701:
                case 711:
                case 721:
                case 741:
                    return resources.getDrawable(R.drawable.foggy);
                default:
                    // You can throw an exception or return a default drawable
                    // For example, returning R.drawable.default_icon
                    return resources.getDrawable(R.drawable.border);
            }
        }

        double getTemperature() {
            return temperature;
        }
    }

    void getDuration(ArrayList<AlertFragment.WeatherEntry> alertList) {
        // Sort the alertList based on the timestamp
        Collections.sort(alertList, new Comparator<WeatherEntry>() {
            @Override
            public int compare(AlertFragment.WeatherEntry entry1, AlertFragment.WeatherEntry entry2) {
                return entry1.time.compareTo(entry2.time);
            }
        });

        int hours = 1; // Initialize the duration to 1 hour
        for (int i = 0; i < alertList.size() - 1; i++) {
            AlertFragment.WeatherEntry currentAlert = alertList.get(i);
            AlertFragment.WeatherEntry nextAlert = alertList.get(i + 1);

            // Check if the alerts have the same type and time difference is 1 hour or less
            if (currentAlert.id == nextAlert.id && getTimeDifference(currentAlert.time, nextAlert.time) <= 1) {
                hours++; // Increment duration
                alertList.get(i + 1).setDuration(hours); // Set the duration for the next alert
                alertList.remove(i); // Remove the current alert
                i--;
            } else {
                hours = 1; // Reset the duration when type changes or the time difference is more than 1 hour
            }
        }
    }
    long getTimeDifference(String startTime, String endTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date startDate = format.parse(startTime);
            Date endDate = format.parse(endTime);

            long diffInMillis = endDate.getTime() - startDate.getTime();
            return TimeUnit.MILLISECONDS.toHours(diffInMillis);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 in case of an error
    }
}