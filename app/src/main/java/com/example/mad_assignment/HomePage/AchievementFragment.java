package com.example.mad_assignment.HomePage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

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

 * create an instance of this fragment.
 */
public class AchievementFragment extends Fragment {

    private ProgressBar progressBar10;
    private ProgressBar progressBar20;
    private ProgressBar progressBar30;
    private DatabaseReference calculationCountRef;
    private FirebaseUser currentUser;
    private ProgressBar progressBarPlant;
    private int plantCount;
    private ProgressBar progressBarPlant10;
    private DatabaseReference plantCountReference;
    private static final int PLANT_GOAL = 5;
    private static final int NOTIFICATION_ID = 1, NOTIFICATION_ID_2 = 2, NOTIFICATION_ID_3 = 3, NOTIFICATION_ID_4 = 4, NOTIFICATION_ID_5 = 5;

    public AchievementFragment() {
        // Required empty public constructor
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
        return inflater.inflate(R.layout.fragment_achievement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button BtnShare = view.findViewById(R.id.BtnShare);
        Button BtnNotify = view.findViewById(R.id.BtnNotify);
        Button BtnNotify2 = view.findViewById(R.id.BtnNotify2);
        Button BtnNotify3 = view.findViewById(R.id.BtnNotify3);
        Button BtnNotify4 = view.findViewById(R.id.BtnNotify4);
        Button BtnNotify5 = view.findViewById(R.id.BtnNotify5);


        progressBar10 = view.findViewById(R.id.PBCC10);
        progressBar20 = view.findViewById(R.id.PBCC20);
        progressBar30 = view.findViewById(R.id.PBCC30);
        progressBarPlant = view.findViewById(R.id.progressBarPlant);

        progressBarPlant10 = view.findViewById(R.id.progressBarPlant10);


        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notif", "Hello", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = requireActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        BtnNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "My Notif");
                builder.setContentTitle("Green Beginner");
                builder.setContentText("Do carbon calculation for 10 times");
                builder.setSmallIcon(R.drawable.bell);
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(requireContext());
                if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                managerCompat.notify(NOTIFICATION_ID, builder.build());
            }
        });

        BtnNotify2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.Builder builder2 = new NotificationCompat.Builder(requireContext(), "My Notif");
                builder2.setContentTitle("Eco Warrior");
                builder2.setContentText("Do carbon calculation for 10 times");
                builder2.setSmallIcon(R.drawable.bell);
                builder2.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(requireContext());
                if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                managerCompat.notify(NOTIFICATION_ID_2 , builder2.build());
            }
        });

        BtnNotify3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.Builder builder3 = new NotificationCompat.Builder(requireContext(), "My Notif");
                builder3.setContentTitle("Carbon Champion");
                builder3.setContentText("Do carbon calculation for 30 times");
                builder3.setSmallIcon(R.drawable.bell);
                builder3.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(requireContext());
                if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                managerCompat.notify(NOTIFICATION_ID_3, builder3.build());
            }
        });

        BtnNotify4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.Builder builder4 = new NotificationCompat.Builder(requireContext(), "My Notif");
                builder4.setContentTitle("Tree Hugger");
                builder4.setContentText("Plant a tree 5 times");
                builder4.setSmallIcon(R.drawable.bell);
                builder4.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(requireContext());
                if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                managerCompat.notify(NOTIFICATION_ID_4, builder4.build());
            }
        });

        BtnNotify5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.Builder builder5 = new NotificationCompat.Builder(requireContext(), "My Notif");
                builder5.setContentTitle("Eco Stewart");
                builder5.setContentText("Plant a tree 10 times");
                builder5.setSmallIcon(R.drawable.bell);
                builder5.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(requireContext());
                if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                managerCompat.notify(NOTIFICATION_ID_5, builder5.build());
            }
        });

        BtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "I have successfully completed the activity and I am so happy");
                intent.setType("text/plain");

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        if (currentUser != null) {
            calculationCountRef = FirebaseDatabase.getInstance().getReference("Points")
                    .child(currentUser.getUid())
                    .child("CalculationCount");

            calculationCountRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int calculationCount = dataSnapshot.getValue(Integer.class);

                        // Update the progress bars based on the calculation count
                        updateProgressBars(calculationCount, plantCount);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error
                }
            });
        }

        if (currentUser != null) {
            plantCountReference = FirebaseDatabase.getInstance().getReference("Points")
                    .child(currentUser.getUid())
                    .child("PlantCount");

            // Add ValueEventListener for PlantCount
            plantCountReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        plantCount = dataSnapshot.getValue(Integer.class);
                        updateProgressBar(progressBarPlant, plantCount, PLANT_GOAL);

                        updateProgressBar(progressBarPlant10, plantCount, 10);


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error
                }
            });
        }
    }

    private void updateProgressBars(int calculationCount, int plantCount) {
        updateProgressBar(progressBar10, calculationCount, 10);
        updateProgressBar(progressBar20, calculationCount, 20);
        updateProgressBar(progressBar30, calculationCount, 30);
        updateProgressBar(progressBarPlant, plantCount, PLANT_GOAL);
    }

    private void updateProgressBar(ProgressBar progressBar, int count, int targetCount) {
        if (count >= targetCount) {
            progressBar.setProgress(progressBar.getMax());
        } else {
            progressBar.setProgress(count * (progressBar.getMax() / targetCount));
        }
    }

}