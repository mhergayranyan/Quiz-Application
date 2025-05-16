package com.example.campusapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CountryFlagsQuizFragment extends Fragment {

    private TextView questionCounter, questionText;
    private ImageView flagImage;
    private Button option1, option2, option3, option4;
    private List<Country> countries = new ArrayList<>();
    private int currentQuestion = 0;
    private int score = 0;
    private int totalQuestions = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_country_flags_quiz, container, false);

        questionCounter = view.findViewById(R.id.questionCounter);
        questionText = view.findViewById(R.id.questionText);
        flagImage = view.findViewById(R.id.flagImage);
        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);
        option3 = view.findViewById(R.id.option3);
        option4 = view.findViewById(R.id.option4);

        loadQuestionsFromFirebase();

        return view;
    }

    private void loadQuestionsFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("countries");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Country country = snapshot.getValue(Country.class);
                    if (country != null && country.getFlagUrl() != null) {
                        countries.add(country);
                    }
                }
                Collections.shuffle(countries);
                totalQuestions = Math.min(countries.size(), 10);
                showNextQuestion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void showNextQuestion() {
        if (currentQuestion < totalQuestions) {
            questionCounter.setText((currentQuestion + 1) + "/" + totalQuestions);

            Country currentCountry = countries.get(currentQuestion);
            Glide.with(this).load(currentCountry.getFlagUrl()).into(flagImage);

            // Get 3 random wrong country names
            List<String> options = new ArrayList<>();
            options.add(currentCountry.getName());

            Random random = new Random();
            while (options.size() < 4) {
                int randomIndex = random.nextInt(countries.size());
                String randomName = countries.get(randomIndex).getName();
                if (!options.contains(randomName)) {
                    options.add(randomName);
                }
            }

            Collections.shuffle(options);

            option1.setText(options.get(0));
            option2.setText(options.get(1));
            option3.setText(options.get(2));
            option4.setText(options.get(3));

            option1.setBackgroundTintList(null);
            option2.setBackgroundTintList(null);
            option3.setBackgroundTintList(null);
            option4.setBackgroundTintList(null);

            option1.setOnClickListener(v -> checkAnswer(option1, currentCountry.getName()));
            option2.setOnClickListener(v -> checkAnswer(option2, currentCountry.getName()));
            option3.setOnClickListener(v -> checkAnswer(option3, currentCountry.getName()));
            option4.setOnClickListener(v -> checkAnswer(option4, currentCountry.getName()));
        } else {
            // Quiz finished
            showResults();
        }
    }

    private void checkAnswer(Button selectedButton, String correctAnswer) {
        String selectedAnswer = selectedButton.getText().toString();

        if (selectedAnswer.equals(correctAnswer)) {
            selectedButton.setBackgroundColor(Color.GREEN);
            score++;
        } else {
            selectedButton.setBackgroundColor(Color.RED);
            // Highlight correct answer
            if (option1.getText().toString().equals(correctAnswer)) option1.setBackgroundColor(Color.GREEN);
            if (option2.getText().toString().equals(correctAnswer)) option2.setBackgroundColor(Color.GREEN);
            if (option3.getText().toString().equals(correctAnswer)) option3.setBackgroundColor(Color.GREEN);
            if (option4.getText().toString().equals(correctAnswer)) option4.setBackgroundColor(Color.GREEN);
        }

        // Disable all buttons after answer is selected
        option1.setEnabled(false);
        option2.setEnabled(false);
        option3.setEnabled(false);
        option4.setEnabled(false);

        // Move to next question after delay
        option1.postDelayed(() -> {
            currentQuestion++;
            showNextQuestion();
        }, 1500);
    }

    private void showResults() {
        // Show results dialog
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Quiz Finished")
                .setMessage("Your score: " + score + "/" + totalQuestions)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Go back to quiz selection
                    getParentFragmentManager().popBackStack();
                })
                .setCancelable(false)
                .show();
    }
}