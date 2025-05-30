package com.example.campusapp;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class QuizModesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz_modes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Mode Buttons Click Listeners
        view.findViewById(R.id.btnCapitalCities).setOnClickListener(v -> {
            navigateToFragment(new CapitalCitiesQuizFragment());
        });

        view.findViewById(R.id.btnCountryFlags).setOnClickListener(v -> {
            navigateToFragment(new CountryFlagsQuizFragment());
        });

        view.findViewById(R.id.btnTimerChallenge).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Timer Quiz will be added soon", Toast.LENGTH_SHORT).show();
        });

        // Info Buttons Click Listeners
        setupInfoButton(view, R.id.info_capital,
                "Capital Cities Quiz",
                "Test your knowledge of world capitals!");

        setupInfoButton(view, R.id.info_flags,
                "Country Flags Quiz",
                "Identify countries by their flags!");

        setupInfoButton(view, R.id.info_timer,
                "Geography Timer Challenge",
                "Answer quickly before time runs out!");
    }

    private void navigateToFragment(Fragment fragment) {
        try {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.fade_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.fade_out
                    )
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            showToast("Error: " + e.getMessage());
        }
    }

    private void setupInfoButton(View rootView, int buttonId, String title, String message) {
        ImageView infoButton = rootView.findViewById(buttonId);
        infoButton.setOnClickListener(v -> showInfoDialog(title, message));
    }

    private void showInfoDialog(String title, String message) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("GOT IT", null)
                .show();
    }

    private void showToast(String text) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show();
    }
}