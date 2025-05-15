package com.example.campusapp;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.campusapp.databinding.ActivityMainBinding;

import android.transition.Fade;
import android.view.Window;



public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean isFirstLaunch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply splash screen theme before super.onCreate
        setTheme(R.style.CampusAppTheme_NoActionBar);

        // Handle dark mode
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? MODE_NIGHT_YES : MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);

        // Set enter transition for activity
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Fade());
        getWindow().setExitTransition(new Fade());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initial fragment load with special animation
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new QuizFragment())
                    .commit();
            loadInitialFragment();
        }

        setupBottomNavigation();
    }

    private void loadInitialFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new QuizFragment())
                .commit();
    }

    private void setupBottomNavigation() {
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment = null;

            if (itemId == R.id.quiz) {
                selectedFragment = new QuizFragment();
            } else if (itemId == R.id.profile) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                replaceFragmentWithAnimation(selectedFragment);
            }
            return true;
        });
    }

    private void replaceFragmentWithAnimation(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}