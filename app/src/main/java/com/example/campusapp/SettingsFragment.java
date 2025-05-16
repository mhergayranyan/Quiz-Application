package com.example.campusapp;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Switch;


import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;


public class SettingsFragment extends Fragment {

    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        Switch darkModeSwitch = view.findViewById(R.id.switch_dark_mode);
        Switch soundSwitch = view.findViewById(R.id.switch_sound);
        Switch vibrationSwitch = view.findViewById(R.id.switch_vibration);

        // Load saved preferences
        darkModeSwitch.setChecked(prefs.getBoolean("dark_mode", false));
        soundSwitch.setChecked(prefs.getBoolean("sound_effects", true));
        vibrationSwitch.setChecked(prefs.getBoolean("vibration", true));

        // Set listeners
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            updateDarkMode(isChecked);
        });

        soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("sound_effects", isChecked).apply();
        });

        vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("vibration", isChecked).apply();
        });



        return view;
    }

    private void updateDarkMode(boolean isDarkMode) {
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
        requireActivity().recreate();
    }
}