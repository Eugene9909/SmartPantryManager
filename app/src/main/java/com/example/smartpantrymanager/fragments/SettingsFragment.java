package com.example.smartpantrymanager.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartpantrymanager.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    private static final String PREF_NAME = "pantry_prefs";
    private static final String KEY_EXPIRING_ALERTS = "expiring_alerts";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwitchMaterial switchAlerts = view.findViewById(R.id.switchExpiringAlerts);

        SharedPreferences prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isAlertsEnabled = prefs.getBoolean(KEY_EXPIRING_ALERTS, false);
        switchAlerts.setChecked(isAlertsEnabled);

        switchAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_EXPIRING_ALERTS, isChecked).apply();
            String status = isChecked ? "enabled" : "disabled";
            Toast.makeText(requireContext(), "Expiring alerts " + status, Toast.LENGTH_SHORT).show();
        });
    }
}
