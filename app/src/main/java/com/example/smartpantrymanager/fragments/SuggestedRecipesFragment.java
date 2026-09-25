package com.example.smartpantrymanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.activities.RecipeDetailActivity;
import com.example.smartpantrymanager.adapters.RecipeAdapter;
import com.example.smartpantrymanager.database.PantryDao;
import com.example.smartpantrymanager.database.RecipeDao;
import com.example.smartpantrymanager.logic.MatchingEngine;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * The "Suggested Recipes" screen (Section 2.2): runs MatchingEngine's
 * strict-matching rule against the current pantry and lists only the
 * recipes the user can make right now. Shows a clear empty-state message
 * when zero recipes match, rather than a blank screen.
 */
public class SuggestedRecipesFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyStateText;

    private PantryDao pantryDao;
    private RecipeDao recipeDao;
    private RecipeAdapter adapter;
    private final List<Recipe> suggestedRecipes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggested_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerSuggestedRecipes);
        emptyStateText = view.findViewById(R.id.textNoMatches);

        pantryDao = new PantryDao(requireContext());
        recipeDao = new RecipeDao(requireContext());

        adapter = new RecipeAdapter(suggestedRecipes, recipe -> {
            startActivity(RecipeDetailActivity.newIntent(requireContext(), recipe.getId()));
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recompute every time this screen becomes visible, so adding/removing
        // a pantry item on the other tab is immediately reflected here.
        refreshSuggestions();
    }

    private void refreshSuggestions() {
        List<PantryItem> pantry = pantryDao.getAll();
        List<Recipe> allRecipes = recipeDao.getAllRecipes();

        List<Recipe> matches = MatchingEngine.getSuggestedRecipes(pantry, allRecipes);
        adapter.updateData(matches);

        boolean isEmpty = matches.isEmpty();
        emptyStateText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}
