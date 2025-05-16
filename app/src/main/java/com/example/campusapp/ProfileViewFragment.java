package com.example.campusapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ProfileViewFragment extends Fragment {

    private static final String ARG_EMAIL = "email";
    private static final String ARG_NICKNAME = "nickname";

    public static ProfileViewFragment newInstance(String email, String nickname) {
        ProfileViewFragment fragment = new ProfileViewFragment();
        Bundle args = new Bundle();
        args.putString("email", email);
        args.putString("nickname", nickname);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_view, container, false);

        // Get user data from arguments
        String email = getArguments().getString(ARG_EMAIL, "");
        String nickname = getArguments().getString(ARG_NICKNAME, "");

        // Set dynamic data
        TextView tvEmail = view.findViewById(R.id.tv_email);
        TextView tvNickname = view.findViewById(R.id.tv_nickname);

        tvEmail.setText(email);
        tvNickname.setText(nickname);

        // Set up button click listeners
        view.findViewById(R.id.btn_edit_profile).setOnClickListener(v -> {
            // Handle edit profile
        });

        view.findViewById(R.id.btn_logout).setOnClickListener(v -> {
            // Handle logout
        });

        return view;
    }
}