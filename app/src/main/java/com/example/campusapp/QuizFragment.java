package com.example.campusapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
public class QuizFragment extends Fragment {

    public QuizFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        // Set click listeners for quiz cards
        CardView capitalCard = view.findViewById(R.id.capitalCitiesQuiz);
        CardView flagsCard = view.findViewById(R.id.countryFlagsQuiz);
        CardView timerCard = view.findViewById(R.id.geographyTimerChallenge);

        capitalCard.setOnClickListener(v -> startQuiz("capital"));
        flagsCard.setOnClickListener(v -> startQuiz("flags"));
        timerCard.setOnClickListener(v -> startQuiz("timer"));

        // Set info button click listeners
        view.findViewById(R.id.capitalInfoBtn).setOnClickListener(v -> showInfoDialog(
                "Capital Cities Quiz",
                "Test your knowledge of world capitals! You'll be given a country name and must select its capital from 4 options."
        ));

        view.findViewById(R.id.flagsInfoBtn).setOnClickListener(v -> showInfoDialog(
                "Country Flags Quiz",
                "Identify countries by their flags! You'll be shown a national flag and must select the correct country from 4 options."
        ));

        view.findViewById(R.id.timerInfoBtn).setOnClickListener(v -> showInfoDialog(
                "Geography Timer Challenge",
                "Race against time! Answer as many geography questions as you can in 90 seconds. Mix of capital cities and flag questions."
        ));

        return view;
    }

    private void startQuiz(String quizType) {
        Fragment quizFragment;
        switch (quizType) {
            case "capital":
                quizFragment = new CapitalQuizFragment();
                break;
            case "flags":
                quizFragment = new FlagQuizFragment();
                break;
            case "timer":
                quizFragment = new TimerQuizFragment();
                break;
            default:
                return;
        }

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, quizFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showInfoDialog(String title, String message) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Got it", null)
                .show();
    }
}