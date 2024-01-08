package com.example.mad_assignment.HomePage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfile extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private CircleImageView profileImageView;
    private Button btnChangeProfile;
    private Uri imageUri;
    private DatabaseReference databaseReference;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    FirebaseDatabase database;


    public UserProfile() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static UserProfile newInstance(String param1, String param2) {
        UserProfile fragment = new UserProfile();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadProfilePicture();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        profileImageView = view.findViewById(R.id.profilePicture);
        btnChangeProfile = view.findViewById(R.id.btnChangeProfile);
//        getUsername();
//        getEmail();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users Account").child(user.getUid());
        }

        // Load existing profile picture if available



        btnChangeProfile.setOnClickListener(v -> openImageChooser());

    }

    private void loadProfilePicture() {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Check if the user has a profile picture URL in the database
        databaseReference.child("profilePictureUrl").get().addOnCompleteListener(task -> {
            progressDialog.dismiss(); // Dismiss the progress dialog

            if (task.isSuccessful() && task.getResult() != null) {
                String profilePictureUrl = task.getResult().getValue(String.class);
                if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                    // Load the existing profile picture using Picasso or any other image loading library
                    Picasso.get().load(profilePictureUrl).into(profileImageView);
                }
            }
        });
    }


    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);

            uploadImage();
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            ProgressDialog progressDialog = new ProgressDialog(requireContext());
            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pictures")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            storageReference.putFile(imageUri)
                    .addOnCompleteListener(requireActivity(), task -> {
                        progressDialog.dismiss(); // Dismiss the progress dialog

                        if (task.isSuccessful()) {
                            // Image uploaded successfully, get download URL
                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                // Update user's profile picture URL in the database
                                databaseReference.child("profilePictureUrl").setValue(uri.toString());
                                Toast.makeText(requireActivity(), "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            // If the upload fails, log an error
                            Toast.makeText(requireActivity(), "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(snapshot -> {
                        // Update the progress of the upload
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploading... ");
                    });
        }
    }


//    private void getUsername(){
//        assert firebaseUser != null;
//        String uid = firebaseUser.getUid();
//        DatabaseReference myRef = database.getReference("Users Account/"+uid+"/usename");
//
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and whenever data at this location is updated
//                // Parse the data from dataSnapshot
//                String username = dataSnapshot.getValue(String.class);
//                TextView name = requireView().findViewById(R.id.ETusername);
//                name.setText(username);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Failed to read value
//                Log.w("Firebase", "Failed to read value.", error.toException());
//            }
//        });
//    }
//
//
//
//    private void getEmail(){
//        assert firebaseUser != null;
//        String uid = firebaseUser.getUid();
//        DatabaseReference myRef = database.getReference("Users Account/"+uid+"/email");
//
//        myRef.addValueEventListener(new ValueEventListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and whenever data at this location is updated
//                // Parse the data from dataSnapshot
//                String username = dataSnapshot.getValue(String.class);
//                TextView name = requireView().findViewById(R.id.email);
//                name.setText("Email : "+username);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Failed to read value
//                Log.w("Firebase", "Failed to read value.", error.toException());
//            }
//        });
//    }




}