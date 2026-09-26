package com.example.smartpantrymanager.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartpantrymanager.R;

/**
 * Settings screen (Section 2.2's "settings or profile screen" requirement).
 * Two preferences, both persisted via SharedPreferences so they survive
 * app restarts:
 *  - Expiring-soon alerts toggle.
 *  - Units preference (metric/imperial), used as a default elsewhere later.
 */
public class SettingsFragment extends Fragment {

    private static final String PREFS_NAME = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS_ENABLED = "expiry_alerts_enabled";
    private static final String KEY_UNITS_PREFERENCE = "units_preference"; // "metric" or "imperial"

    private SharedPreferences prefs;
    private Switch switchExpiryAlerts;
    private RadioGroup radioGroupUnits;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        switchExpiryAlerts = view.findViewById(R.id.switchExpiryAlerts);
        radioGroupUnits = view.findViewById(R.id.radioGroupUnits);

        loadSavedPreferences();

        switchExpiryAlerts.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(KEY_EXPIRY_ALERTS_ENABLED, isChecked).apply());

        radioGroupUnits.setOnCheckedChangeListener((group, checkedId) -> {
            String value = (checkedId == R.id.radioImperial) ? "imperial" : "metric";
            prefs.edit().putString(KEY_UNITS_PREFERENCE, value).apply();
        });
    }

    private void loadSavedPreferences() {
        // Expiry alerts default to ON.
        boolean alertsEnabled = prefs.getBoolean(KEY_EXPIRY_ALERTS_ENABLED, true);
        switchExpiryAlerts.setChecked(alertsEnabled);

        // Units default to metric.
        String units = prefs.getString(KEY_UNITS_PREFERENCE, "metric");
        radioGroupUnits.check("imperial".equals(units) ? R.id.radioImperial : R.id.radioMetric);
    }

    /** Static helper other screens can use later to check the alert setting without duplicating the key. */
    public static boolean areExpiryAlertsEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_EXPIRY_ALERTS_ENABLED, true);
    }
}
