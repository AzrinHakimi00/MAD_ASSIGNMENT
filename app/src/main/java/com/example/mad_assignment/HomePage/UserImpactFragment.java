package com.example.mad_assignment.HomePage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mad_assignment.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserImpactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserImpactFragment extends Fragment {

    private Spinner activitiesSpinner;
    private Button BtnBackToAT;
    private ImageView[] treeImageViews;
    private final int[] activityPoints = {50, 20, 20, 20, 100, 100, 50, 20, 50, 30, 50, 50, 10, 30};
    private int totalPoints = 0;
    private int plantCount = 0;
    private TextView pointsTextView;
    private DatabaseReference totalPointsReference;
    private DatabaseReference plantCountReference;
    private FirebaseUser currentUser;
    private ProgressBar progressBarPlant;
    private static final int PLANT_GOAL = 5;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserImpactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserImpactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserImpactFragment newInstance(String param1, String param2) {
        UserImpactFragment fragment = new UserImpactFragment();
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
        return inflater.inflate(R.layout.fragment_user_impact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activitiesSpinner = view.findViewById(R.id.activitiesSpinner);
        Button submitButton = view.findViewById(R.id.submitButton);
        Button resetTreesButton = view.findViewById(R.id.resetTreesButton);
        pointsTextView =  view.findViewById(R.id.pointsTextView);
        progressBarPlant =  view.findViewById(R.id.progressBarPlant);

        Button cbcalc = view.findViewById(R.id.carbonCalc);
        cbcalc.setOnClickListener(v -> {
            Navigation.findNavController(getView()).navigate(R.id.activityTrackerFragment);
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            totalPointsReference = FirebaseDatabase.getInstance().getReference("Points")
                    .child(currentUser.getUid())
                    .child("TotalPoints");

            totalPointsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        totalPoints = dataSnapshot.getValue(Integer.class);
                        updateTreeVisuals(totalPoints);
                        pointsTextView.setText("Total Points: " + totalPoints);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error
                }
            });

            plantCountReference = FirebaseDatabase.getInstance().getReference("Points")
                    .child(currentUser.getUid())
                    .child("PlantCount");

            plantCountReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        plantCount = dataSnapshot.getValue(Integer.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error
                }
            });
        }

        treeImageViews = new ImageView[9];
        for (int i = 1; i <= 9; i++) {
            @SuppressLint("DiscouragedApi") int treeId = getResources().getIdentifier("tree" + i, "id", requireActivity().getPackageName());
            treeImageViews[i - 1] = view.findViewById(treeId);
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.activities_array, android.R.layout.simple_dropdown_item_1line);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitiesSpinner.setAdapter(adapter);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedActivity = activitiesSpinner.getSelectedItem().toString();

                int activityIndex = getResources().getStringArray(R.array.activities_array).length - 1;
                String[] activitiesArray = getResources().getStringArray(R.array.activities_array);
                for (int i = 0; i < activitiesArray.length; i++) {
                    if (activitiesArray[i].equals(selectedActivity)) {
                        activityIndex = i;
                        break;
                    }
                }

                // Update plant count if the selected activity is "Plant a Tree"
                if (selectedActivity.equals("Plant a Tree")) {
                    plantCount++;
                    // You can use plantCount as needed
                    Log.d("UserImpact", "Plant Count: " + plantCount);
                }

                int points = activityPoints[activityIndex];

                totalPoints += points;

                Toast.makeText(getActivity(), "Points earned: " + points, Toast.LENGTH_SHORT).show();

                updateTreeVisuals(totalPoints);
                pointsTextView.setText("Total Points: " + totalPoints);

                saveTotalPointsToFirebase(totalPoints);

                // Update PlantCount in Firebase
                if (currentUser != null && plantCountReference != null) {
                    plantCountReference.setValue(plantCount);
                }
            }
        });

        resetTreesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalPoints = 0;
                updateTreeVisuals(totalPoints);
                Toast.makeText(getActivity(), "Trees reset!", Toast.LENGTH_SHORT).show();
                pointsTextView.setText("Total Points: " + totalPoints);
                saveTotalPointsToFirebase(totalPoints);
            }
        });

//        BtnBackToAT.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(UserImpact.this, UserAchievement.class);
//                startActivity(intent);
//            }
//        });
    }

    private void updateTreeVisuals(int points) {
        int numTrees = points / 200;

        hideAllTrees();

        for (int i = 0; i < numTrees && i < treeImageViews.length; i++) {
            treeImageViews[i].setVisibility(View.VISIBLE);
        }

        showExtraTrees(numTrees);

        showToastAndSetText(points);

        saveTotalPointsToFirebase(points);
        updateProgressBar(progressBarPlant, plantCount, PLANT_GOAL);
    }

    private void hideAllTrees() {
        for (ImageView treeImageView : treeImageViews) {
            treeImageView.setVisibility(View.INVISIBLE);
        }
    }

    private void showExtraTrees(int numTrees) {
        int MAX_TREES = 9;
        if (numTrees == MAX_TREES) {
            Toast.makeText(getActivity(), "You have reached your daily goal!", Toast.LENGTH_SHORT).show();
        }

    }

    private void showToastAndSetText(int points) {
        Toast.makeText(getActivity(), "Points: " + points, Toast.LENGTH_SHORT).show();
        pointsTextView.setText("Total Points: " + points);

    }

    private void saveTotalPointsToFirebase(int points) {
        if (currentUser != null && totalPointsReference != null) {
            totalPointsReference.setValue(points);
        }
    }

    private void updateProgressBar(ProgressBar progressBar, int calculationCount, int targetCount) {
        if (calculationCount >= targetCount) {
            progressBar.setProgress(progressBar.getMax());
        } else {
            progressBar.setProgress(calculationCount * (progressBar.getMax() / targetCount));
        }
    }
}