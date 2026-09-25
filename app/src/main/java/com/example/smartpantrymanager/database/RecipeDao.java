package com.example.smartpantrymanager.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Data-access object for recipes and recipe_ingredients.
 * Read-only by design: recipes are seeded once in DatabaseHelper and the
 * app never lets the user create/edit/delete them (only pantry items get
 * full CRUD, per Section 2.2 — recipes are a fixed collection).
 */
public class RecipeDao {

    private final DatabaseHelper dbHelper;

    public RecipeDao(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /** Returns every recipe, each with its full ingredient list populated. */
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_RECIPES,
                null, null, null, null, null,
                DatabaseHelper.COL_RECIPE_NAME + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = cursorToRecipe(cursor);
                recipe.setIngredients(getIngredientsForRecipe(db, recipe.getId()));
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recipes;
    }

    /** Returns a single recipe with its ingredients populated, or null if not found. */
    public Recipe getRecipeById(long recipeId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_RECIPES,
                null,
                DatabaseHelper.COL_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null, null, null
        );

        Recipe recipe = null;
        if (cursor.moveToFirst()) {
            recipe = cursorToRecipe(cursor);
            recipe.setIngredients(getIngredientsForRecipe(db, recipe.getId()));
        }
        cursor.close();
        db.close();
        return recipe;
    }

    /** Reuses an already-open db (avoids reopening per recipe when loading a whole list). */
    private List<RecipeIngredient> getIngredientsForRecipe(SQLiteDatabase db, long recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_RECIPE_INGREDIENTS,
                null,
                DatabaseHelper.COL_RI_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RI_ID));
                String ingredientName = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RI_INGREDIENT_NAME));
                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RI_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RI_UNIT));

                ingredients.add(new RecipeIngredient(id, recipeId, ingredientName, quantity, unit));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ingredients;
    }

    private Recipe cursorToRecipe(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RECIPE_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RECIPE_NAME));
        String steps = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RECIPE_STEPS));
        return new Recipe(id, name, steps);
    }
}
