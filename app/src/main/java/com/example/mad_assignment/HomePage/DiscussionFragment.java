package com.example.mad_assignment.HomePage;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mad_assignment.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class DiscussionFragment extends Fragment {

    private TextView topicTextView , topicCreatorTV;
    private EditText commentEditText;
    private Button addCommentButton;
    private DatabaseReference discussionReference;
    private ArrayList<Comment> commentList;
    private CustomCommentAdapter commentAdapter;
    RecyclerView commentsRecyclerView;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
        setExitTransition(inflater.inflateTransition(R.transition.slide_left));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discussion, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        topicTextView = view.findViewById(R.id.topicTextView);
        commentEditText = view.findViewById(R.id.commentEditText);
        addCommentButton = view.findViewById(R.id.addCommentButton);
        topicCreatorTV = view.findViewById(R.id.topicCreatorTextView);

        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        commentList = new ArrayList<>();
        commentAdapter = new CustomCommentAdapter(requireContext(), commentList);
        commentsRecyclerView.setAdapter(commentAdapter);


        Bundle bundle = getArguments();
        if (bundle != null) {
            String topic = bundle.getString("Topic");
            String creator = bundle.getString("Username");
            if (topic != null) {
                topicTextView.setText("Topic : "+topic);
                topicCreatorTV.setText("by "+creator);
                // Replace "discussion" with your Firebase discussion node name
                discussionReference = FirebaseDatabase.getInstance().getReference("discussion").child(topic);
            }
        }

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle adding a new comment
                String newComment = commentEditText.getText().toString().trim();

                if (!TextUtils.isEmpty(newComment)) {
                    assert firebaseUser != null;
                    String uid = firebaseUser.getUid();

                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users Account/"+uid+"/username");

                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and whenever data at this location is updated
                            // Parse the data from dataSnapshot
                            String username = dataSnapshot.getValue(String.class);
                            long timestamp = System.currentTimeMillis();

                            DatabaseReference newCommentRef = discussionReference.push();
                            newCommentRef.child("comment").setValue(newComment);
                            newCommentRef.child("username").setValue(username);
                            newCommentRef.child("uid").setValue(uid);
                            newCommentRef.child("timestamp").setValue(timestamp);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Failed to read value
                            Log.w("Firebase", "Failed to read value.", error.toException());
                        }
                    });




                    commentEditText.setText(""); // Clear the comment EditText after adding the comment
                }
            }
        });

        // Read comments from Firebase
        discussionReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String comment = snapshot.child("comment").getValue(String.class);
                    String username = snapshot.child("username").getValue(String.class);
                    Long timestamp = snapshot.child("timestamp").getValue(Long.class);
                    String uid = snapshot.child("uid").getValue(String.class);

                    if (comment != null && username != null && timestamp != null) {
                        Comment commentItem = new Comment(comment, username, timestamp,uid);
                        commentList.add(commentItem);
                    }
                }

                Collections.sort(commentList, (comment1, comment2) -> Long.compare(comment2.timestamp, comment1.timestamp));

                // Ensure UI updates are on the UI thread
                requireActivity().runOnUiThread(() -> {
                    commentAdapter.notifyDataSetChanged();
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error reading data from Firebase", databaseError.toException());
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }

    private void getUsername(){
        assert firebaseUser != null;
        String uid = firebaseUser.getUid();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users Account/"+uid+"/username");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and whenever data at this location is updated
                // Parse the data from dataSnapshot
                String username = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }




    // ... (other methods, if any)

    public static class Comment {
        String comment;
        String username;
        long timestamp;
        String uid;

        public Comment(String comment, String username, long timestamp, String uid) {
            this.comment = comment;
            this.username = username;
            this.timestamp = timestamp;
            this.uid = uid;
        }
    }

    private class CustomCommentAdapter extends RecyclerView.Adapter<CustomCommentAdapter.ViewHolder> {

        private ArrayList<Comment> commentItems;

        public CustomCommentAdapter(Context context, ArrayList<Comment> items) {
            this.commentItems = items;
        }

        // ... (Other methods)

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Comment currentComment = commentItems.get(position);
            holder.loadProfilePicture(currentComment.uid);

            holder.usernameTextView.setText(currentComment.username);
            holder.commentTextView.setText(currentComment.comment);

            // Format timestamp as needed
            String formattedTimestamp = formatTimestamp(currentComment.timestamp);
            holder.timestampTextView.setText(formattedTimestamp);

            // Set different background colors based on position
            int colorRes;
            switch (position % 2) {
                case 0:
                    colorRes = R.color.color1;
                    break;
                case 1:
                    colorRes = R.color.color2;
                    break;
                default:
                    colorRes = R.color.color3; // Add a default color if needed
            }
            holder.itemView.setBackgroundColor(ContextCompat.getColor(requireContext(), colorRes));
        }

        @Override
        public int getItemCount() {
            return commentItems.size();
        }

        private String formatTimestamp(long timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView usernameTextView, commentTextView, timestampTextView;
            CircleImageView profilePicture;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                usernameTextView = itemView.findViewById(R.id.nameTextView);
                commentTextView = itemView.findViewById(R.id.postTextView);
                timestampTextView = itemView.findViewById(R.id.timestampTextView);
                profilePicture = itemView.findViewById(R.id.profilePicture);
            }



            // Add this method to load profile picture
            private void loadProfilePicture(String uid) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users Account").child(uid);
                userRef.child("profilePicture").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String profilePictureUrl = task.getResult().getValue(String.class);
                        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                            // Load the existing profile picture using Picasso or any other image loading library
                            Picasso.get().load(profilePictureUrl).into(profilePicture);
                        }
                    }
                });
            }
        }
    }

}
