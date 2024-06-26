package com.example.mad_assignment.AccountManagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mad_assignment.HomePage.MainPage;
import com.example.mad_assignment.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;


public class First_page extends AppCompatActivity {

    Button loginBtn;
    Button SignUpUsingEmailBtn;
    Button googleSignUpBtn;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 20;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        loginBtn = findViewById(R.id.loginButton);
        SignUpUsingEmailBtn = findViewById(R.id.SignUpUsingEmailButton);
        googleSignUpBtn = findViewById(R.id.GoogleButton);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(First_page.this,LoginPage.class));
            }
        });

        SignUpUsingEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(First_page.this,SignUpPage.class));
            }
        });




        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();


        googleSignInClient = GoogleSignIn.getClient(this,gso);
//--------------------------------------------------------------------------------------------------------------------------------------------------
        googleSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                googleSignUp();
            }
        });


        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(First_page.this, MainPage.class));
            finish();
        }

    }

    private void googleSignUp() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());

            }catch (Exception e){
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }


    }

    private void firebaseAuth(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    HashMap<String,String>  map = new HashMap<>();

                    map.put("id",user.getUid());
                    map.put("username",user.getDisplayName());
                    map.put("email",user.getEmail());
                    map.put("profilePicture",user.getPhotoUrl().toString());




                    database.getReference().child("Users Account").child(user.getUid()).setValue(map);

                    startActivity(new Intent(First_page.this, MainPage.class));
                }
                else{
                    Toast.makeText(First_page.this,"Something went wrong",Toast.LENGTH_LONG).show();
                }
            }
        });

    }





}