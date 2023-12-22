package com.example.mad_assignment.AccountManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mad_assignment.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordPage extends AppCompatActivity {

    MaterialButton resetBtn, backBtn;
    EditText email;
    String emailAdd;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_page);

        resetBtn = findViewById(R.id.resetPasswordBtn);
        backBtn = findViewById(R.id.backBtn);
        email = findViewById(R.id.ETemail);
        firebaseAuth = FirebaseAuth.getInstance();


        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailAdd = email.getText().toString();
                if(!emailAdd.isEmpty()){
                    resetPassword();
                }
                else{
                    email.setError("Please provide a valid email address");
                }

            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResetPasswordPage.this,LoginPage.class));
            }
        });



    }

    private void resetPassword() {

        String emailAdd = email.getText().toString();

        firebaseAuth.sendPasswordResetEmail(emailAdd).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"Reset password link has been send to your email", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ResetPasswordPage.this,LoginPage.class));
                finish();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_LONG).show();
            }
        });


    }
}