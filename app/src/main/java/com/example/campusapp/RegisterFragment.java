package com.example.campusapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {

    private FirebaseAuth auth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        EditText etEmail = view.findViewById(R.id.et_email);
        EditText etNickname = view.findViewById(R.id.et_nickname);
        EditText etPassword = view.findViewById(R.id.et_password);
        EditText etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        Button btnRegister = view.findViewById(R.id.btn_register);
        Button btnLoginRedirect = view.findViewById(R.id.btn_login_redirect);

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String nickname = etNickname.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (!validateInputs(email, password, confirmPassword)) {
                return;
            }

            ProgressDialog progress = new ProgressDialog(getContext());
            progress.setMessage("Registering...");
            progress.setCancelable(false);
            progress.show();
            ((MainActivity) requireActivity()).navigateToProfileState();



            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), task -> {
                        progress.dismiss(); // Dismiss dialog when done

                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                // Save additional user data
                                DatabaseReference userRef = FirebaseDatabase.getInstance()
                                        .getReference("users")
                                        .child(user.getUid());
                                ((MainActivity) requireActivity()).navigateToProfileState();

                                Map<String, Object> userData = new HashMap<>();
                                userData.put("email", email);
                                userData.put("nickname", nickname);

                                userRef.setValue(userData)
                                        .addOnSuccessListener(aVoid -> {
                                            // Only navigate after ALL data is saved
                                            handleRegistrationSuccess(email, nickname);
                                            ((MainActivity) requireActivity()).navigateToProfileState();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Failed to save profile", Toast.LENGTH_SHORT).show();
                                            Log.e("Firebase", "Save failed", e);
                                        });
                            }
                        } else {
                            Toast.makeText(getContext(),
                                    "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        btnLoginRedirect.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private boolean validateInputs(String email, String password, String confirmPassword) {
        if (email.isEmpty()) {
            Toast.makeText(getContext(), "Email is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(getContext(), "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void handleRegistrationSuccess(String email, String nickname) {
        // 1. Double-check we have a valid activity
        if (getActivity() == null || isDetached()) return;

        // 2. Save user data to SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        prefs.edit()
                .putString("user_email", email)
                .putString("user_nickname", nickname)
                .apply();

        // 3. Navigate to ProfileFragment with animation
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.fade_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.fade_out  // popExit
        );
        ((MainActivity) requireActivity()).navigateToProfileState();
    }
}