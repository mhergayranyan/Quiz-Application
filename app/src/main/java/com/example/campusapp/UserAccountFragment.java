package com.example.campusapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserAccountFragment extends Fragment {

    private TextView totalQuizzesTextView;
    private TextView highestScoreTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference resultsRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_account, container, false);
        super.onViewCreated(view, savedInstanceState);

        totalQuizzesTextView = view.findViewById(R.id.total_quizzes_number);  // your TextView IDs
        highestScoreTextView = view.findViewById(R.id.highest_score_number);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        totalQuizzesTextView = view.findViewById(R.id.total_quizzes_number);
        highestScoreTextView = view.findViewById(R.id.highest_score_number);

        if (user != null) {
            String uid = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("results");

            databaseReference.child(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        long totalQuizzes = snapshot.child("totalQuizzes").getValue(Long.class) != null
                                ? snapshot.child("totalQuizzes").getValue(Long.class)
                                : 0;
                        long highestScore = snapshot.child("highestScore").getValue(Long.class) != null
                                ? snapshot.child("highestScore").getValue(Long.class)
                                : 0;

                        totalQuizzesTextView.setText(String.valueOf(totalQuizzes));
                        highestScoreTextView.setText(String.valueOf(highestScore));
                    }
                }
            });
        }




        if (user != null) {
            TextView tvUsername = view.findViewById(R.id.et_nickname);
            TextView tvEmail = view.findViewById(R.id.et_email);



// Load pr

            // Set username (use display name if available, otherwise email prefix)
            String username = user.getDisplayName();
            if (username == null || username.isEmpty()) {
                String email = user.getEmail();
                username = email != null ? email.split("@")[0] : "User";
            }

            tvUsername.setText(username);
            tvEmail.setText(user.getEmail());



            // Set stats (replace with your actual data)
            TextView tvQuizzesCompleted = view.findViewById(R.id.total_quizzes_number);
            TextView tvHighestScore = view.findViewById(R.id.highest_score_number);



        }

        // Sign Out button
        view.findViewById(R.id.btn_sign_out).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            ((MainActivity) requireActivity()).navigateToProfileState();
            Toast.makeText(getContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void loadStats() {
        resultsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot snapshot = task.getResult();

                Long totalQuizzes = snapshot.child("totalQuizzes").getValue(Long.class);
                Long highestScore = snapshot.child("highestScore").getValue(Long.class);

                totalQuizzesTextView.setText(totalQuizzes != null ? String.valueOf(totalQuizzes) : "0");
                highestScoreTextView.setText(highestScore != null ? String.valueOf(highestScore) : "0");
            } else {
                totalQuizzesTextView.setText("0");
                highestScoreTextView.setText("0");
            }
        });
    }
}