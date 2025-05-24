package com.bizhan.auarai.fragments.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.bizhan.auarai.R;
import com.bizhan.auarai.authorization.LoginActivity;

public class ProfileFragment extends Fragment {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String TOKEN_KEY = "auth_token";
    private static final String THEME_KEY = "theme_mode";
    private SwitchMaterial switchTheme;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        View logoutButton = view.findViewById(R.id.logoutButton);
        switchTheme = view.findViewById(R.id.switchTheme);

        // Set up theme switch
        setupThemeSwitch();

        // Set up logout button
        logoutButton.setOnClickListener(v -> {
            SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().remove(TOKEN_KEY).apply();
            Toast.makeText(requireContext(), "Log out successful", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void setupThemeSwitch() {
        // Get current theme mode
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int currentTheme = prefs.getInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        
        // Set switch state based on current theme
        switchTheme.setChecked(currentTheme == AppCompatDelegate.MODE_NIGHT_YES);

        // Set up switch listener
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int newTheme = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            
            // Save theme preference
            prefs.edit().putInt(THEME_KEY, newTheme).apply();
            
            // Apply theme
            AppCompatDelegate.setDefaultNightMode(newTheme);
        });
    }
}