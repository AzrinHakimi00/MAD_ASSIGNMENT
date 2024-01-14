package com.example.mad_assignment.HomePage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import com.example.mad_assignment.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivityTrackerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivityTrackerFragment extends Fragment {

    private double totalCarbon = 0.0;
    private EditText ETInput;
    private TextView TVResult, TVProgress;
    private ProgressBar PBCarbon;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;


    public ActivityTrackerFragment() {
        // Required empty public constructor
    }

    public static ActivityTrackerFragment newInstance(String param1, String param2) {
        ActivityTrackerFragment fragment = new ActivityTrackerFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activity_tracker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ETInput = view.findViewById(R.id.ETInput);
        TVResult = view.findViewById(R.id.TVResult);
        TVProgress = view.findViewById(R.id.TVProgress);
        PBCarbon = view.findViewById(R.id.PBCarbon);

        LinearLayout webView = view.findViewById(R.id.webView);
        ImageButton infoButton = view.findViewById(R.id.infoBtn);

        infoButton.setOnClickListener(v -> {
            WebView web = view.findViewById(R.id.carbonInfo);
            webView.setVisibility(View.VISIBLE);
            web.getSettings().setJavaScriptEnabled(true);
            web.getSettings().setLoadWithOverviewMode(true);
            web.getSettings().setUseWideViewPort(true);

            // Load your URL here
            web.loadUrl("https://clevercarbon.io/carbon-footprint-of-common-items/");
        });

        Button closeBtN = view.findViewById(R.id.closeBtn);
        closeBtN.setOnClickListener(v -> {webView.setVisibility(View.GONE);});

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // Change the child node to "carbon" under the user's UID
            databaseReference = FirebaseDatabase.getInstance().getReference("Points").child(currentUser.getUid()).child("carbon");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    totalCarbon = snapshot.getValue(Double.class);
                    TVResult.setText("Total carbon : "+totalCarbon + " g");
                    updateProgressBar();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        loadProgressFromFirebase();

        Button Calculate = view.findViewById(R.id.Calculate);
        Calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = ETInput.getText().toString();

                if (!userInput.isEmpty()) {
                    double carbonInput = Double.parseDouble(userInput);
                    totalCarbon += carbonInput;

                    TVResult.setText("Total Carbon: " + totalCarbon + " g");
                    ETInput.setText("");

                    updateProgressBar();
                    incrementCalculationCount();
                }
            }
        });

        Button btnReset = view.findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalCarbon = 0.0;
                TVResult.setText("Total Carbon: " + totalCarbon + " g");
                ETInput.setText("");
                updateProgressBar();
            }
        });

    }


    private void updateProgressBar() {
        double worldAverage = 13700;
        int progress = (int) ((totalCarbon / worldAverage) * 100);
        progress = Math.min(progress, PBCarbon.getMax());
        PBCarbon.setProgress(progress);
        TVProgress.setText("Progress: " + progress + "% out of 13700g");

        saveProgressToFirebase(totalCarbon);
    }

    private void saveProgressToFirebase(double progress) {
        if (currentUser != null && databaseReference != null) {
            databaseReference.setValue(progress);
        }
    }

    private void loadProgressFromFirebase() {
        if (currentUser != null && databaseReference != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    totalCarbon = snapshot.getValue(Double.class);
                    TVResult.setText("Total carbon : "+totalCarbon + " g");
                    updateProgressBar();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void incrementCalculationCount() {
        // Increment the count in Firebase under the user's UID
        DatabaseReference calculationCountRef = FirebaseDatabase.getInstance().getReference("Points")
                .child(currentUser.getUid())
                .child("CalculationCount");

        calculationCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int currentCount = dataSnapshot.getValue(Integer.class);
                    calculationCountRef.setValue(currentCount + 1);
                } else {
                    // If the count doesn't exist yet, create it with a value of 1
                    calculationCountRef.setValue(1);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }
}