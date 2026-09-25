package com.example.smartpantrymanager.model;

/**
 * One required ingredient line for a recipe (e.g. "200 g pasta").
 * ingredientName is stored normalized so it can be compared directly
 * against PantryItem.name in the matching engine.
 */
public class RecipeIngredient {

    private long id;
    private long recipeId;
    private String ingredientName; // normalized, e.g. "tomato"
    private double requiredQuantity;
    private String unit;

    public RecipeIngredient() {
    }

    public RecipeIngredient(long id, long recipeId, String ingredientName,
                            double requiredQuantity, String unit) {
        this.id = id;
        this.recipeId = recipeId;
        this.ingredientName = ingredientName;
        this.requiredQuantity = requiredQuantity;
        this.unit = unit;
    }

    // Convenience constructor used when seeding recipes (id not yet known).
    public RecipeIngredient(String ingredientName, double requiredQuantity, String unit) {
        this(-1, -1, ingredientName, requiredQuantity, unit);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public double getRequiredQuantity() {
        return requiredQuantity;
    }

    public void setRequiredQuantity(double requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
