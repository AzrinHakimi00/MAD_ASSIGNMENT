package com.example.mad_assignment.AccountManagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mad_assignment.HomePage.MainPage;
import com.example.mad_assignment.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginPage extends AppCompatActivity {

    EditText ETemail,ETpassword;
    Button login;
    TextView signUp, resetPassword;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        ETemail = findViewById(R.id.ETemail);
        ETpassword = findViewById(R.id.ETpassword);
        login = findViewById(R.id.loginBtn);
        signUp = findViewById(R.id.signUp);
        resetPassword = findViewById(R.id.resetPasswordBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginPage.this,SignUpPage.class));
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ETemail.getText().toString();
                String password = ETpassword.getText().toString();

                if(email.isEmpty()){
                    ETemail.setError("Please enter the email");
                    ETemail.requestFocus();
                }
                else if(password.isEmpty()){
                    ETpassword.setError("Please enter your password");
                    ETpassword.requestFocus();
                }
                else if(!password.isEmpty() && !email.isEmpty()){

                    firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){

                                Toast.makeText(getApplicationContext(),"Login Unsuccessful",Toast.LENGTH_LONG).show();
                            }
                            else{

                                if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                   // HashMap<String,String> map = new HashMap<>();
                                   // map.put("email", email);
                                   // map.put("password", password);
                                   // database.getReference().child("Users").setValue(map);
                                    startActivity(new Intent(LoginPage.this, MainPage.class));
                                }
                                else{
                                    Toast.makeText(getApplicationContext()
                                    ,"Email not verified",Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                    });
                }
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginPage.this,ResetPasswordPage.class));
            }
        });

    }
}