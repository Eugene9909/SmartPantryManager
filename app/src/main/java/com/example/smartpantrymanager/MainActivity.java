package com.example.smartpantrymanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.fragments.PantryListFragment;

/**
 * Single host Activity. For now it just loads PantryListFragment into the
 * fragment container. Once SuggestedRecipesFragment and SettingsFragment
 * exist, this will grow a BottomNavigationView to switch between all three
 * (see the project plan) — no point adding that with only one destination yet.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new PantryListFragment())
                    .commit();
        }
    }
}
