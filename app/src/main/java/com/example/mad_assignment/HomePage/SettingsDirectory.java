package com.example.mad_assignment.HomePage;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mad_assignment.R;

public class SettingsDirectory extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Default layout if the extra is not provided
        int layoutResId = getIntent().getIntExtra("layoutResId", R.layout.fragment_aboutapp);

        setContentView(layoutResId);
    }
}
