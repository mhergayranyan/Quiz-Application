package com.example.campusapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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

public class FlagQuizFragment extends Fragment {

    private TextView questionCounter, questionText;
    private ImageView flagImage;
    private MaterialButton option1, option2, option3, option4;
    private List<Country> countries = new ArrayList<>();
    private int currentQuestion = 0;
    private int score = 0;
    private int totalQuestions = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flag_quiz, container, false);

        questionCounter = view.findViewById(R.id.questionCounter);
        questionText = view.findViewById(R.id.questionText);
        flagImage = view.findViewById(R.id.flagImage);
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
                    if (country != null && country.getFlag() != null) {
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

            // Load flag image using Glide
            Glide.with(this)
                    .load(currentCountry.getFlag())
                    .into(flagImage);

            List<String> options = generateOptions(currentCountry);
            option1.setText(options.get(0));
            option2.setText(options.get(1));
            option3.setText(options.get(2));
            option4.setText(options.get(3));

            resetButtonColors();
            setButtonClickListeners(currentCountry.getName());
        } else {
            showResults();
        }
    }

    private List<String> generateOptions(Country correctCountry) {
        List<String> options = new ArrayList<>();
        options.add(correctCountry.getName());

        Random random = new Random();
        while (options.size() < 4) {
            Country randomCountry = countries.get(random.nextInt(countries.size()));
            if (!options.contains(randomCountry.getName())) {
                options.add(randomCountry.getName());
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

    private void setButtonClickListeners(final String correctAnswer) {
        View.OnClickListener answerListener = v -> {
            MaterialButton selectedButton = (MaterialButton) v;
            boolean isCorrect = selectedButton.getText().toString().equals(correctAnswer);

            // Disable all buttons
            option1.setEnabled(false);
            option2.setEnabled(false);
            option3.setEnabled(false);
            option4.setEnabled(false);

            // Highlight answers
            if (isCorrect) {
                selectedButton.setBackgroundColor(Color.GREEN);
                score++;
            } else {
                selectedButton.setBackgroundColor(Color.RED);
                highlightCorrectAnswer(correctAnswer);
            }

            // Move to next question after delay
            new Handler().postDelayed(() -> {
                currentQuestion++;
                showNextQuestion();
            }, 1500);
        };

        option1.setOnClickListener(answerListener);
        option2.setOnClickListener(answerListener);
        option3.setOnClickListener(answerListener);
        option4.setOnClickListener(answerListener);
    }

    private void highlightCorrectAnswer(String correctAnswer) {
        if (option1.getText().toString().equals(correctAnswer)) {
            option1.setBackgroundColor(Color.GREEN);
        } else if (option2.getText().toString().equals(correctAnswer)) {
            option2.setBackgroundColor(Color.GREEN);
        } else if (option3.getText().toString().equals(correctAnswer)) {
            option3.setBackgroundColor(Color.GREEN);
        } else if (option4.getText().toString().equals(correctAnswer)) {
            option4.setBackgroundColor(Color.GREEN);
        }
    }

    private void updateQuestionCounter() {
        questionCounter.setText(String.format(Locale.getDefault(), "%d/%d", currentQuestion + 1, totalQuestions));
    }

    private void showResults() {
        // Show results dialog
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Quiz Finished")
                .setMessage(String.format(Locale.getDefault(),
                        "Your score: %d/%d", score, totalQuestions))
                .setPositiveButton("Done", (dialog, which) -> {
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .setCancelable(false)
                .show();
    }
}