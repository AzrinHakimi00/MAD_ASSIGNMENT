package com.example.mad_assignment.HomePage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.mad_assignment.AccountManagement.First_page;
import com.example.mad_assignment.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    FirebaseAuth firebaseAuth;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    LinearLayout AboutAppBtn;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.fade));



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout AboutAppBtn = view.findViewById(R.id.aboutAppLayout);
        AboutAppBtn.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.AAFragment)
        );

        LinearLayout PrivacyPolicyBtn = view.findViewById(R.id.privacyPolicyLayout);
        PrivacyPolicyBtn.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.PPFragment)
        );

        LinearLayout TermCondBtn = view.findViewById(R.id.termsConditionsLayout);
        TermCondBtn.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.TCFragment);
        });

        LinearLayout profileBtn = view.findViewById(R.id.ProfileView);
        profileBtn.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.userProfile);
        });

        ExtendedFloatingActionButton logout = view.findViewById(R.id.logoutBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                userSignout();
            }
        });

    }

    private void userSignout() {

        Intent intent = new Intent(getActivity(), First_page.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    private void openSettingsDirectory(int layoutResId) {
        Intent intent = new Intent(getActivity(), SettingsDirectory.class);
        intent.putExtra("layoutResId", layoutResId);
        startActivity(intent);
    }
}