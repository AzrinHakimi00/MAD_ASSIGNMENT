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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;

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

import de.hdodenhof.circleimageview.CircleImageView;


public class ForumFragment extends Fragment {

    private static final int YOUR_MAX_POST_LENGTH = 150;
    private DatabaseReference databaseReference;
    private ArrayList<Adapter> itemList;
    RecyclerView recyclerView;
    CustomAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_left));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);



        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        itemList = new ArrayList<>();
        adapter = new CustomAdapter(requireContext(), itemList);
        recyclerView.setAdapter(adapter);

        // Replace "forum" with your Firebase database node name
        databaseReference = FirebaseDatabase.getInstance().getReference("forum");

        adapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Adapter item) {
                DiscussionFragment discussionFragment = new DiscussionFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Topic", item.post);
                bundle.putString("Username", item.name);
                discussionFragment.setArguments(bundle);

                Navigation.findNavController(requireView()).navigate(R.id.discussionFragment, bundle);
            }
        });


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

                // Check if the post is too long
                if (newPost.length() > YOUR_MAX_POST_LENGTH) {
                    // Display an error message or handle the situation accordingly
                    Toast.makeText(requireContext(), "Post is too long", Toast.LENGTH_SHORT).show();
                    return;
                }

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




    private static class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        private ArrayList<Adapter> items;
        private OnItemClickListener onItemClickListener;

        public CustomAdapter(Context context, ArrayList<Adapter> items) {
            this.items = items;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.onItemClickListener = listener;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Adapter currentItem = items.get(position);

            holder.nameTextView.setText(currentItem.name);
            holder.postTextView.setText(currentItem.post);
            holder.timestampTextView.setText(formatTimestamp(currentItem.timestamp));

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
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), colorRes));
        }

        public interface OnItemClickListener {
            void onItemClick(Adapter item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        // Add a method to format the timestamp
        private String formatTimestamp(long timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView, postTextView, timestampTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.nameTextView);
                postTextView = itemView.findViewById(R.id.postTextView);
                timestampTextView = itemView.findViewById(R.id.timestampTextView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                onItemClickListener.onItemClick(items.get(position));
                            }
                        }
                    }
                });
            }
        }
    }

}






