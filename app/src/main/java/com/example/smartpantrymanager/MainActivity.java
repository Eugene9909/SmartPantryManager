package com.example.smartpantrymanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.smartpantrymanager.fragments.PantryListFragment;
import com.example.smartpantrymanager.fragments.SettingsFragment;
import com.example.smartpantrymanager.fragments.SuggestedRecipesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Single host Activity with a BottomNavigationView switching between the
 * three main screens (Section 3.1's "working navigation element").
 *
 * Uses a show/hide pattern rather than replace() on every tap: all three
 * fragments are created once and added to the container, then only the
 * selected one is made visible. This keeps each screen's state (e.g. scroll
 * position) intact when switching back to it, and each screen's onResume()
 * still fires correctly when it becomes visible (PantryListFragment and
 * SuggestedRecipesFragment both rely on that to refresh their data).
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG_PANTRY = "pantry";
    private static final String TAG_SUGGESTED = "suggested";
    private static final String TAG_SETTINGS = "settings";

    private Fragment pantryFragment;
    private Fragment suggestedFragment;
    private Fragment settingsFragment;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            // First launch: create all three fragments once and add them,
            // showing only the Pantry screen initially.
            pantryFragment = new PantryListFragment();
            suggestedFragment = new SuggestedRecipesFragment();
            settingsFragment = new SettingsFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, settingsFragment, TAG_SETTINGS).hide(settingsFragment)
                    .add(R.id.fragmentContainer, suggestedFragment, TAG_SUGGESTED).hide(suggestedFragment)
                    .add(R.id.fragmentContainer, pantryFragment, TAG_PANTRY)
                    .commit();

            activeFragment = pantryFragment;
        } else {
            // Activity was recreated (e.g. rotation): re-find the existing
            // fragment instances by tag instead of creating new ones.
            pantryFragment = fragmentManager.findFragmentByTag(TAG_PANTRY);
            suggestedFragment = fragmentManager.findFragmentByTag(TAG_SUGGESTED);
            settingsFragment = fragmentManager.findFragmentByTag(TAG_SETTINGS);
            activeFragment = pantryFragment; // reasonable default; state is preserved either way
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                switchTo(pantryFragment);
                return true;
            } else if (id == R.id.nav_suggested) {
                switchTo(suggestedFragment);
                return true;
            } else if (id == R.id.nav_settings) {
                switchTo(settingsFragment);
                return true;
            }
            return false;
        });
    }

    private void switchTo(Fragment target) {
        if (target == activeFragment) {
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(activeFragment);
        transaction.show(target);
        transaction.commit();
        activeFragment = target;
    }
}
