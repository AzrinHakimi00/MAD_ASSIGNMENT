package com.example.mad_assignment.AccountManagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mad_assignment.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class SignUpPage extends AppCompatActivity {

    EditText ETemail, ETpassword, ETconfirmpassword, ETusername;
    Button signUp;
    TextView goToLoginPage;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        ETemail = findViewById(R.id.ETemail);
        ETpassword = findViewById(R.id.ETpassword);
        ETconfirmpassword = findViewById(R.id.ETconfirmPassword);
        ETusername = findViewById(R.id.ETusername);

        signUp = findViewById(R.id.signUpBtn);
        goToLoginPage = findViewById(R.id.loginBtn);

        firebaseAuth = FirebaseAuth.getInstance();

        goToLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpPage.this,LoginPage.class));
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = ETemail.getText().toString();
                String password = ETpassword.getText().toString();
                String confirmpassword = ETconfirmpassword.getText().toString();
                String username = ETusername.getText().toString();

                if(email.isEmpty()){
                    ETemail.setError("Please provide your email address");
                    ETemail.requestFocus();

                }
                else if(password.isEmpty()){
                    ETpassword.setError("Please provide a password");
                    ETpassword.requestFocus();
                }
                else if(confirmpassword.isEmpty()){
                    ETconfirmpassword.setError("Please retype your password");
                    ETconfirmpassword.requestFocus();
                }
                else if(!password.equals(confirmpassword)){
                    ETconfirmpassword.setError("The password does not match");
                    ETconfirmpassword.requestFocus();
                }
                else if(!email.isEmpty() && !password.isEmpty() && !confirmpassword.isEmpty() && password.equals(confirmpassword)){
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Sign Up Unsuccessful",Toast.LENGTH_LONG).show();
                            }
                            else{
                                Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            database = FirebaseDatabase.getInstance();
                                            reference = database.getReference("Users Account").child(user.getUid());

//                                            SharedPreferences sharedPreferences = getSharedPreferences("User Data", MODE_PRIVATE);
//                                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                                            editor.putString("Username", username);
//                                            editor.putString("Email", email);
                                            String defaultProfilePic = "https://firebasestorage.googleapis.com/v0/b/mad-assignment-1a67d.appspot.com/o/man.png?alt=media&token=25e85a6b-5e4d-4d46-b226-840088aebeeb";

                                            HashMap<String,String> map = new HashMap<>();
                                            map.put("username",username);
                                            map.put("email",email);
                                            map.put("password",password);
                                            map.put("profilePicture",defaultProfilePic);

                                            reference.setValue(map);

                                            Toast.makeText(getApplicationContext(), "Sign up successful. Please verify you email.", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignUpPage.this,VerificationPage.class));
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(),"Sign Up Unsuccessful",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            }
                        }
                    });
                }

            }
        });
    }
}