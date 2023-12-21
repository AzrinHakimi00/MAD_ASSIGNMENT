package com.example.mad_assignment.AccountManagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mad_assignment.R;

public class VerificationPage extends AppCompatActivity {


    Button gotoLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_page);

        gotoLogin = findViewById(R.id.GoToLoginBtn);

        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VerificationPage.this,LoginPage.class));
            }
        });
    }
}