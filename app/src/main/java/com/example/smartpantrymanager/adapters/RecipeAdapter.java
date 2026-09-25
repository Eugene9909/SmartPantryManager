package com.example.smartpantrymanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.Recipe;

import java.util.List;

/**
 * Binds a List<Recipe> to a RecyclerView for the Suggested Recipes screen.
 * Tapping a row is delegated to the hosting Fragment via a listener,
 * which will open RecipeDetailActivity once that screen exists.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClicked(Recipe recipe);
    }

    private final List<Recipe> recipes;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.textRecipeName.setText(recipe.getName());

        int count = recipe.getIngredients().size();
        String label = count + (count == 1 ? " ingredient" : " ingredients") + " \u00b7 you have them all";
        holder.textRecipeIngredientCount.setText(label);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRecipeClicked(recipe);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    /** Replaces the adapter's data set (e.g. after the pantry changes) and refreshes the list. */
    public void updateData(List<Recipe> newRecipes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notifyDataSetChanged();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView textRecipeName;
        TextView textRecipeIngredientCount;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textRecipeName = itemView.findViewById(R.id.textRecipeName);
            textRecipeIngredientCount = itemView.findViewById(R.id.textRecipeIngredientCount);
        }
    }
}
