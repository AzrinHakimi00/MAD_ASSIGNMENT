package com.example.mad_assignment.HomePage;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ActivityTrackerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActivityTrackerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivityTrackerFragment newInstance(String param1, String param2) {
        ActivityTrackerFragment fragment = new ActivityTrackerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // Change the child node to "carbon" under the user's UID
            databaseReference = FirebaseDatabase.getInstance().getReference("Points").child(currentUser.getUid()).child("carbon");
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

                    TVResult.setText("Total Carbon: " + totalCarbon + " ppm");
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
                TVResult.setText("Total Carbon: " + totalCarbon + " ppm");
                ETInput.setText("");
                updateProgressBar();
            }
        });

    }


    private void updateProgressBar() {
        double worldAverage = 421.0;
        int progress = (int) ((totalCarbon / worldAverage) * 100);
        progress = Math.min(progress, PBCarbon.getMax());
        PBCarbon.setProgress(progress);
        TVProgress.setText("Progress: " + progress + "% out of 421ppm");

        saveProgressToFirebase(progress);
    }

    private void saveProgressToFirebase(int progress) {
        if (currentUser != null && databaseReference != null) {
            databaseReference.setValue(progress);
        }
    }

    private void loadProgressFromFirebase() {
        if (currentUser != null && databaseReference != null) {
            databaseReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Integer savedProgress = task.getResult().getValue(Integer.class);
                    if (savedProgress != null) {
                        PBCarbon.setProgress(savedProgress);
                        TVProgress.setText("Progress: " + savedProgress + "% out of 421ppm");
                    }
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