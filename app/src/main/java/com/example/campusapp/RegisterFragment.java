package com.example.campusapp;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        Log.d("REGISTER_FRAG", "Fragment inflated successfully");

        EditText etEmail = view.findViewById(R.id.et_email);
        EditText etNickname = view.findViewById(R.id.et_nickname);
        EditText etPassword = view.findViewById(R.id.et_password);
        EditText etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        Button btnRegister = view.findViewById(R.id.btn_register);
        Button btnLoginRedirect = view.findViewById(R.id.btn_login_redirect);

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (validateInputs(email, password, confirmPassword)) {
                registerUser(email, password);
            }
        });

        btnLoginRedirect.setOnClickListener(v -> {
            // Navigate to LoginFragment
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

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Save nickname to Firebase Realtime Database
                        saveUserData(mAuth.getCurrentUser().getUid(),
                                requireView().<EditText>findViewById(R.id.et_nickname).getText().toString());

                        Toast.makeText(getContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                        navigateToMainApp();
                    } else {
                        Toast.makeText(getContext(), "Registration failed: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String userId, String nickname) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");
        database.child(userId).child("nickname").setValue(nickname);
    }

    private void navigateToMainApp() {
        // Replace with your main app navigation
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new QuizFragment())
                .commit();
    }
}