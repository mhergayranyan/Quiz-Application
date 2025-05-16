package com.example.campusapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class QuizFragment extends Fragment {

    public QuizFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        // Set up click listeners for each quiz mode
        LinearLayout capitalCitiesQuiz = view.findViewById(R.id.capitalCitiesQuiz);
        LinearLayout countryFlagsQuiz = view.findViewById(R.id.countryFlagsQuiz);
        LinearLayout geographyTimerChallenge = view.findViewById(R.id.geographyTimerChallenge);

        capitalCitiesQuiz.setOnClickListener(v -> startCapitalCitiesQuiz());
        countryFlagsQuiz.setOnClickListener(v -> startCountryFlagsQuiz());
        geographyTimerChallenge.setOnClickListener(v -> startGeographyTimerChallenge());

        // Set up info buttons
        ImageButton capitalInfoBtn = view.findViewById(R.id.capitalInfoBtn);
        ImageButton flagsInfoBtn = view.findViewById(R.id.flagsInfoBtn);
        ImageButton timerInfoBtn = view.findViewById(R.id.timerInfoBtn);

        capitalInfoBtn.setOnClickListener(v -> showQuizInfo("Capital Cities Quiz",
                "Test your knowledge of world capitals! You'll be shown a country name and need to select its capital from 4 options."));

        flagsInfoBtn.setOnClickListener(v -> showQuizInfo("Country Flags Quiz",
                "Identify countries by their flags! You'll be shown a flag and need to select the correct country from 4 options."));

        timerInfoBtn.setOnClickListener(v -> showQuizInfo("Geography Timer Challenge",
                "A timed challenge mixing both capital cities and flag questions! Answer as many as you can before time runs out."));

        return view;
    }

    private void startCapitalCitiesQuiz() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new CapitalCitiesQuizFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void startCountryFlagsQuiz() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new CountryFlagsQuizFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void startGeographyTimerChallenge() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new GeographyTimerChallengeFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showQuizInfo(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}