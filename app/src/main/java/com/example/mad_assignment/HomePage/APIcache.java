package com.example.mad_assignment.HomePage;

import android.content.Context;
import android.content.SharedPreferences;

public class APIcache {

    private static final String CACHE_FILE = "data_cache";
    private static final String KEY_TEMPERATURE = "cached_temperature";
    private static final String KEY_WEATHER = "cached_weather";
    private static final String KEY_WEATHER_ICON = "cached_weather_icon";
    private static final String KEY_TIMESTAMP = "cache_timestamp";

    private final SharedPreferences sharedPreferences;

    public APIcache(Context context) {
        sharedPreferences = context.getSharedPreferences(CACHE_FILE, Context.MODE_PRIVATE);
    }

    public void saveTemperature(String temperature) {
        saveData(KEY_TEMPERATURE, temperature);
    }

    public void saveWeather(String weather) {
        saveData(KEY_WEATHER, weather);
    }

    public void saveWeatherIcon(String weatherIcon) {
        saveData(KEY_WEATHER_ICON, weatherIcon);
    }

    private void saveData(String key, String data) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, data);
        editor.putLong(KEY_TIMESTAMP + key, System.currentTimeMillis()); // Save current timestamp for this key
        editor.apply();
    }

    public String getCachedTemperature() {
        return getCachedData(KEY_TEMPERATURE);
    }

    public String getCachedWeather() {
        return getCachedData(KEY_WEATHER);
    }

    public String getCachedWeatherIcon() {
        return getCachedData(KEY_WEATHER_ICON);
    }

    private String getCachedData(String key) {
        long timestamp = sharedPreferences.getLong(KEY_TIMESTAMP + key, 0);
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - timestamp;

        // Check if the cached data is older than 1 hour (3600000 milliseconds)
        if (timeDifference <= 0 || timeDifference > 3600000) {
            // Clear the cache if data is older than 1 hour
            clearCache(key);
            return null;
        }

        return sharedPreferences.getString(key, null);
    }

    private void clearCache(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.remove(KEY_TIMESTAMP + key);
        editor.apply();
    }
}


