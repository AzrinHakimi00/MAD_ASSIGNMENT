package com.example.mad_assignment.HomePage;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mad_assignment.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;


public class ForumFragment extends Fragment {

    private DatabaseReference databaseReference;
    private ArrayList<Adapter> itemList;

    CustomAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);



        ListView listView = view.findViewById(R.id.listView);
        itemList = new ArrayList<>();
        adapter = new CustomAdapter(requireContext(), R.layout.forum_list_item, itemList);
        listView.setAdapter(adapter);

        // Replace "forum" with your Firebase database node name
        databaseReference = FirebaseDatabase.getInstance().getReference("forum");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton addTopicButton = view.findViewById(R.id.addTopicButton);
        addTopicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the dialog for adding a new topic
                showAddTopicDialog();
            }
        });

        // Read data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String post = snapshot.child("post").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    Long timestamp = snapshot.child("timestamp").getValue(Long.class);

                    if (post != null && name != null && timestamp != null) {
                        Adapter adapterItem = new Adapter(post, name, timestamp);
                        itemList.add(adapterItem);
                    }
                }

                Collections.sort(itemList, (item1, item2) -> Long.compare(item2.timestamp, item1.timestamp));

                // Ensure UI updates are on the UI thread
                requireActivity().runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error reading data from Firebase", databaseError.toException());
            }
        });
    }

    private void showAddTopicDialog() {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_topic_dialog, null);

        // Find views in the dialog layout
        EditText postEditText = dialogView.findViewById(R.id.postEditText);
        EditText authorName = dialogView.findViewById(R.id.nameAuthor);
        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        builder.setTitle("Add New Topic");

        // Set the positive button click listener
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle adding a new topic when the "Add" button is clicked
                String newPost = postEditText.getText().toString().trim();
                String name = authorName.getText().toString().trim();

                long timestamp = System.currentTimeMillis();


                DatabaseReference newTopicRef = databaseReference.push();
                newTopicRef.child("post").setValue(newPost);
                newTopicRef.child("name").setValue(name);
                newTopicRef.child("timestamp").setValue(timestamp);



                    // Ensure UI updates are on the UI thread
                    requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
            }
        });

        // Set the negative button click listener
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancel the dialog
                dialog.dismiss();
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    public static class Adapter {
        String post;
        String name;
        long timestamp;

        public Adapter(String post, String name, long timestamp) {
            this.name = name;
            this.post = post;
            this.timestamp = timestamp;
        }
    }

    private class CustomAdapter extends ArrayAdapter<Adapter> {
        private ArrayList<Adapter> items;

        public CustomAdapter(Context context, int resourceId, ArrayList<Adapter> items) {
            super(context, resourceId, items);
            this.items = items;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.forum_list_item, parent, false);
            }

            TextView nameTextView = convertView.findViewById(R.id.nameTextView);
            TextView postTextView = convertView.findViewById(R.id.postTextView);
            TextView timestampTextView = convertView.findViewById(R.id.timestampTextView);

            Adapter currentItem = items.get(position);

            nameTextView.setText(currentItem.name);
            postTextView.setText(currentItem.post);

            // Format timestamp as needed
            String formattedTimestamp = formatTimestamp(currentItem.timestamp);
            timestampTextView.setText(formattedTimestamp);



            // Set different background colors based on position
            int colorRes;
            switch (position % 5) {
                case 0:
                    colorRes = R.color.color1;
                    break;
                case 1:
                    colorRes = R.color.color2;
                    break;
                case 2:
                    colorRes = R.color.color3;
                    break;
                case 3:
                    colorRes = R.color.color4;
                    break;
                case 4:
                    colorRes = R.color.color5;
                    break;
                default:
                    colorRes = R.color.color1; // Add a default color if needed
            }
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), colorRes));

            return convertView;
        }

        // Add a method to format the timestamp
        private String formatTimestamp(long timestamp) {
            // Implement the logic to format the time (e.g., using SimpleDateFormat)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }
    }

    public interface UserNameCallback {
        void onUserNameReceived(String userName);
    }

    public void getuserName(UserNameCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users Account");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);

                // Pass the result to the callback
                callback.onUserNameReceived(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle cancellation
            }
        });
    }

}






