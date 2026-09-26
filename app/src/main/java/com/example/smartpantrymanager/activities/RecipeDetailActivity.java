package com.example.smartpantrymanager.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.database.RecipeDao;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * Recipe Detail screen (Section 2.2): shows the full ingredient list and
 * method for a single recipe, opened via an Intent extra carrying the
 * recipe's id (Section 3.1's "correct use of Intents to pass data").
 */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    /** Convenience factory so callers don't need to know the extra key by name. */
    public static Intent newIntent(Context context, long recipeId) {
        Intent intent = new Intent(context, RecipeDetailActivity.class);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        if (recipeId == -1) {
            Toast.makeText(this, "No recipe specified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        RecipeDao recipeDao = new RecipeDao(this);
        Recipe recipe = recipeDao.getRecipeById(recipeId);

        if (recipe == null) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView textIngredients = findViewById(R.id.textDetailIngredients);
        TextView textSteps = findViewById(R.id.textDetailSteps);
        MaterialToolbar toolbar = findViewById(R.id.toolbarRecipeDetail);

        toolbar.setTitle(recipe.getName());
        toolbar.setNavigationOnClickListener(v -> finish());

        textIngredients.setText(formatIngredients(recipe));
        textSteps.setText(recipe.getSteps());
    }

    /** Formats the ingredient list as "\u2022 200 g pasta" bullet lines. */
    private String formatIngredients(Recipe recipe) {
        StringBuilder builder = new StringBuilder();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            if (builder.length() > 0) {
                builder.append("\n");
            }
            builder.append("\u2022 ")
                    .append(formatQuantity(ingredient.getRequiredQuantity()))
                    .append(" ")
                    .append(ingredient.getUnit())
                    .append(" ")
                    .append(ingredient.getIngredientName());
        }
        return builder.toString();
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity) && !Double.isInfinite(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }
}
