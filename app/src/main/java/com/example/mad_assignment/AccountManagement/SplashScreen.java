package com.example.mad_assignment.AccountManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mad_assignment.R;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    private TextView typingText;
    private String fullText = "EarthGuard";
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        typingText = findViewById(R.id.typingText);

        // Start the typing animation
        startTypingAnimation();
    }

    private void startTypingAnimation() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index <= fullText.length()) {
                    // Update the text with the typing effect
                    typingText.setText(fullText.substring(0, index++));
                    handler.postDelayed(this, 100); // Adjust the delay as needed
                } else {
                    // Animation complete, navigate to the main activity or another activity
                    startActivity(new Intent(SplashScreen.this, First_page.class));
                    finish();
                }
            }
        }, 100); // Initial delay before starting the animation
    }
}