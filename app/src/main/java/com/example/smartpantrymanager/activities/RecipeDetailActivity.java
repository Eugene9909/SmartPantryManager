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

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    private RecipeDao recipeDao;

    public static Intent newIntent(Context context, long recipeId) {
        Intent intent = new Intent(context, RecipeDetailActivity.class);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        recipeDao = new RecipeDao(this);

        TextView textRecipeName = findViewById(R.id.textDetailRecipeName);
        TextView textIngredientsList = findViewById(R.id.textDetailIngredientsList);
        TextView textSteps = findViewById(R.id.textDetailSteps);

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        if (recipeId == -1) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Recipe recipe = recipeDao.getRecipeById(recipeId);
        if (recipe == null) {
            Toast.makeText(this, "Could not load recipe details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textRecipeName.setText(recipe.getName());
        textSteps.setText(recipe.getSteps());

        StringBuilder ingredientsBuilder = new StringBuilder();
        for (RecipeIngredient ri : recipe.getIngredients()) {
            ingredientsBuilder.append("• ")
                    .append(formatQuantity(ri.getRequiredQuantity()))
                    .append(" ")
                    .append(ri.getUnit())
                    .append(" ")
                    .append(ri.getIngredientName())
                    .append("\n");
        }
        textIngredientsList.setText(ingredientsBuilder.toString().trim());
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity) && !Double.isInfinite(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }
}
