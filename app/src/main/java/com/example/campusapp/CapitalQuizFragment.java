package com.example.campusapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class CapitalQuizFragment extends Fragment {

    private TextView questionCounter, questionText;
    private MaterialButton option1, option2, option3, option4;
    private List<Country> countries = new ArrayList<>();
    private int currentQuestion = 0;
    private int score = 0;
    private int totalQuestions = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capital_quiz, container, false);

        questionCounter = view.findViewById(R.id.questionCounter);
        questionText = view.findViewById(R.id.questionText);
        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);
        option3 = view.findViewById(R.id.option3);
        option4 = view.findViewById(R.id.option4);

        loadCountriesFromFirebase();

        return view;
    }

    private void loadCountriesFromFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot countrySnapshot : snapshot.getChildren()) {
                    Country country = countrySnapshot.getValue(Country.class);
                    if (country != null && country.getCapital() != null) {
                        countries.add(country);
                    }
                }
                Collections.shuffle(countries);
                totalQuestions = Math.min(countries.size(), 10);
                showNextQuestion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load questions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNextQuestion() {
        if (currentQuestion < totalQuestions) {
            updateQuestionCounter();
            Country currentCountry = countries.get(currentQuestion);
            questionText.setText(String.format("What is the capital of %s?", currentCountry.getName()));

            List<String> options = generateOptions(currentCountry);

            // Reset buttons before setting new values
            resetButtonStates();

            option1.setText(options.get(0));
            option2.setText(options.get(1));
            option3.setText(options.get(2));
            option4.setText(options.get(3));

            // Set click listeners with the correct answer
            setButtonClickListeners(currentCountry.getCapital());
        } else {
            showResults();
        }
    }

    private List<String> generateOptions(Country correctCountry) {
        List<String> options = new ArrayList<>();
        options.add(correctCountry.getCapital());

        Random random = new Random();
        while (options.size() < 4) {
            Country randomCountry = countries.get(random.nextInt(countries.size()));
            if (!options.contains(randomCountry.getCapital())) {
                options.add(randomCountry.getCapital());
            }
        }
        Collections.shuffle(options);
        return options;
    }

    private void resetButtonColors() {
        option1.setBackgroundColor(Color.parseColor("#3949AB"));
        option2.setBackgroundColor(Color.parseColor("#3949AB"));
        option3.setBackgroundColor(Color.parseColor("#3949AB"));
        option4.setBackgroundColor(Color.parseColor("#3949AB"));

        option1.setEnabled(true);
        option2.setEnabled(true);
        option3.setEnabled(true);
        option4.setEnabled(true);
    }

    private void resetButtonStates() {
        option1.setEnabled(true);
        option2.setEnabled(true);
        option3.setEnabled(true);
        option4.setEnabled(true);

        option1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.button_default));
        option2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.button_default));
        option3.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.button_default));
        option4.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.button_default));
    }

    private void setButtonClickListeners(final String correctAnswer) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialButton clickedButton = (MaterialButton) v;
                String selectedAnswer = clickedButton.getText().toString();

                // Disable all buttons to prevent multiple clicks
                option1.setEnabled(false);
                option2.setEnabled(false);
                option3.setEnabled(false);
                option4.setEnabled(false);

                if (selectedAnswer.equals(correctAnswer)) {
                    // Correct answer - turn green
                    clickedButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.correct_answer));
                    score++;
                } else {
                    // Wrong answer - turn red and show correct answer
                    clickedButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.wrong_answer));
                    highlightCorrectAnswer(correctAnswer);
                }

                // Move to next question after delay
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentQuestion++;
                        showNextQuestion();
                    }
                }, 1500); // 1.5 second delay
            }
        };

        option1.setOnClickListener(listener);
        option2.setOnClickListener(listener);
        option3.setOnClickListener(listener);
        option4.setOnClickListener(listener);
    }

    private void highlightCorrectAnswer(String correctAnswer) {
        if (option1.getText().toString().equals(correctAnswer)) {
            option1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.correct_answer));
        } else if (option2.getText().toString().equals(correctAnswer)) {
            option2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.correct_answer));
        } else if (option3.getText().toString().equals(correctAnswer)) {
            option3.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.correct_answer));
        } else if (option4.getText().toString().equals(correctAnswer)) {
            option4.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.correct_answer));
        }
    }

    private void updateQuestionCounter() {
        questionCounter.setText(String.format(Locale.getDefault(), "%d/%d", currentQuestion + 1, totalQuestions));
    }

    private void showResults() {
        // Implement your results display logic here
        // Example: show a dialog with the final score
    }
}