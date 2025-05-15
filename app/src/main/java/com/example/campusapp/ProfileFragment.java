package com.example.campusapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;


public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button btnLogin = view.findViewById(R.id.btnLogin);
        Button btnCreateAccount = view.findViewById(R.id.btnCreateAccount);

        btnLogin.setOnClickListener(v -> replaceFragment(new LoginFragment()));
        btnCreateAccount.setOnClickListener(v -> replaceFragment(new CreateAccountFragment()));
        btnLogin.setBackgroundColor(0xFFFF0000); // Red background
        btnLogin.setTextColor(0xFFFFFFFF); // White text
        btnCreateAccount.setBackgroundColor(0xFFFF0000); // Red background
        btnCreateAccount.setTextColor(0xFFFFFFFF);

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}