package com.example.smartpantrymanager.model;
import java.util.ArrayList;
import java.util.List;

/**
 * A recipe: a name, its prep steps, and the list of ingredients it requires.
 * The ingredients list is populated separately by RecipeDao (it lives in
 * its own table), so it defaults to an empty list here.
 */
public class Recipe {

    private long id;
    private String name;
    private String steps; // newline-separated prep steps
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    public Recipe() {
    }

    public Recipe(long id, String name, String steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
    }

    public Recipe(String name, String steps, List<RecipeIngredient> ingredients) {
        this.id = -1;
        this.name = name;
        this.steps = steps;
        this.ingredients = ingredients;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return name;
    }
}
