package com.example.mad_assignment.HomePage;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mad_assignment.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainPage extends AppCompatActivity {

    Button logoutBtn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

      /*  logoutBtn = findViewById(R.id.logoutBtn);
        firebaseAuth = FirebaseAuth.getInstance();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();

                userSignOut();

            }
        });*/
    }

    /*private void userSignOut() {

        Intent intent = new Intent(MainPage.this, First_page.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }*/
}