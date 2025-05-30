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
import com.google.firebase.auth.FirebaseAuth;

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
        boolean darkMode = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);


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

            if (itemId == R.id.quiz) {
                replaceFragmentWithAnimation(new QuizFragment());
            }
            else if (itemId == R.id.profile) {
                navigateToProfileState(); // This will show the correct page
            }
            return true;
        });
    }

    public void navigateToProfileState() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // User is logged in - show account page
            replaceFragmentWithAnimation(new UserAccountFragment());
        } else {
            // User is logged out - show your original profile fragment with buttons
            replaceFragmentWithAnimation(new ProfileFragment());
        }
    }

    private void replaceFragmentWithAnimation(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out
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

    // Add this new method to open user account
    public void openUserAccountPage() {
        Fragment accountFragment = new Fragment(); // Create temporary fragment
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out
                )
                .replace(R.id.fragment_container, accountFragment)
                .addToBackStack(null)
                .commit();
    }

    public void checkAuthAndNavigate() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            openUserAccountPage();
        } else {
            replaceFragmentWithAnimation(new ProfileFragment());
        }
    }
}