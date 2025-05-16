package com.example.campusapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;


public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        EditText etEmail = view.findViewById(R.id.et_email);
        EditText etPassword = view.findViewById(R.id.et_password);
        Button btnLogin = view.findViewById(R.id.btn_login);
        Button btnCreateAccount = view.findViewById(R.id.btn_create_account);
        Button btnBack = view.findViewById(R.id.btn_back);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email is required");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password is required");
                return;
            }

            loginUser(email, password);
        });

        btnCreateAccount.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        view.findViewById(R.id.btn_forgot_password).setOnClickListener(v -> {
            showForgotPasswordDialog();
        });

        return view;



    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);
        builder.setView(dialogView);

        TextInputEditText etEmail = dialogView.findViewById(R.id.et_email);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnSend = dialogView.findViewById(R.id.btn_send);

        // Auto-fill email if available
        EditText loginEmail = getView().findViewById(R.id.et_email);
        etEmail.setText(loginEmail.getText().toString());

        AlertDialog dialog = builder.create();
        dialog.show();



        // Set click listeners
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSend.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email required");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Invalid email format");
            } else {
                sendPasswordResetEmail(email);
                dialog.dismiss();
            }
        });
    }
    private void sendPasswordResetEmail(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(),
                                "Password reset email sent",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(),
                                "Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                        navigateToMainApp();
                    } else {
                        Toast.makeText(getContext(), "Login failed: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToMainApp() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new QuizFragment())
                .commit();
    }
}