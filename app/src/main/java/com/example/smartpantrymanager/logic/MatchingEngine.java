package com.example.smartpantrymanager.logic;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;
import com.example.smartpantrymanager.util.IngredientNormalizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements the assignment's core business logic (Section 2.3): a recipe
 * is "suggested" only if the user's pantry has EVERY required ingredient,
 * in at least the required quantity. One missing or insufficient ingredient
 * excludes the whole recipe from the strict list.
 *
 * Deliberately isolated from any Activity/Fragment/database code so it's
 * easy to point at in the video demo and easy to test in isolation.
 */
public class MatchingEngine {

    /** Aggregated pantry quantity for one normalized ingredient name, in its base unit. */
    private static class AvailableQuantity {
        double baseQuantity;
        IngredientNormalizer.UnitCategory category;

        AvailableQuantity(double baseQuantity, IngredientNormalizer.UnitCategory category) {
            this.baseQuantity = baseQuantity;
            this.category = category;
        }
    }

    private MatchingEngine() {
        // Utility class - no instances.
    }

    /**
     * Returns only the recipes the user can make RIGHT NOW: every required
     * ingredient present in the pantry, in at least the required quantity.
     * This is the main "Suggested Recipes" list (Section 2.2).
     */
    public static List<Recipe> getSuggestedRecipes(List<PantryItem> pantry, List<Recipe> recipes) {
        return getRecipesWithAtMostNMissing(pantry, recipes, 0);
    }

    /**
     * Optional stretch feature (Section 8): recipes missing exactly one
     * ingredient, so they can be shown in a separate "Almost There" list.
     * Must never be mixed into the strict suggestions list above.
     */
    public static List<Recipe> getAlmostThereRecipes(List<PantryItem> pantry, List<Recipe> recipes) {
        List<Recipe> missingAtMostOne = getRecipesWithAtMostNMissing(pantry, recipes, 1);
        List<Recipe> strictMatches = getSuggestedRecipes(pantry, recipes);
        missingAtMostOne.removeAll(strictMatches); // keep only the ones missing exactly 1
        return missingAtMostOne;
    }

    /**
     * Core routine: returns recipes missing at most maxMissing ingredients
     * (0 = strict matching, 1 = used to derive "Almost There").
     */
    private static List<Recipe> getRecipesWithAtMostNMissing(List<PantryItem> pantry,
                                                               List<Recipe> recipes,
                                                               int maxMissing) {
        Map<String, AvailableQuantity> pantryMap = buildPantryMap(pantry);
        List<Recipe> results = new ArrayList<>();

        for (Recipe recipe : recipes) {
            int missingCount = countMissingIngredients(pantryMap, recipe);
            if (missingCount <= maxMissing) {
                results.add(recipe);
            }
        }
        return results;
    }

    /** Counts how many of a recipe's required ingredients the pantry does NOT sufficiently cover. */
    private static int countMissingIngredients(Map<String, AvailableQuantity> pantryMap, Recipe recipe) {
        int missing = 0;
        for (RecipeIngredient required : recipe.getIngredients()) {
            if (!pantryCovers(pantryMap, required)) {
                missing++;
            }
        }
        return missing;
    }

    /**
     * True if the pantry has this one required ingredient in at least the
     * required quantity. Ingredients in incompatible unit categories (e.g.
     * required in grams but pantry only has a count-based entry) are treated
     * as not covered, since they can't be safely compared.
     */
    private static boolean pantryCovers(Map<String, AvailableQuantity> pantryMap, RecipeIngredient required) {
        String normalizedName = IngredientNormalizer.normalizeName(required.getIngredientName());
        AvailableQuantity available = pantryMap.get(normalizedName);
        if (available == null) {
            return false; // ingredient not in pantry at all
        }

        IngredientNormalizer.UnitCategory requiredCategory =
                IngredientNormalizer.categoryOf(IngredientNormalizer.normalizeUnit(required.getUnit()));
        if (available.category != requiredCategory) {
            return false; // can't compare grams to a count, etc.
        }

        double requiredBaseQuantity =
                IngredientNormalizer.toBaseQuantity(required.getRequiredQuantity(), required.getUnit());

        return available.baseQuantity >= requiredBaseQuantity;
    }

    /**
     * Aggregates pantry items into normalized-name -> total available quantity
     * (in base units). If the same ingredient appears in the pantry more than
     * once (e.g. two separate "tomato" entries), their quantities are summed,
     * provided they're in the same unit category.
     */
    private static Map<String, AvailableQuantity> buildPantryMap(List<PantryItem> pantry) {
        Map<String, AvailableQuantity> map = new HashMap<>();

        for (PantryItem item : pantry) {
            String normalizedName = IngredientNormalizer.normalizeName(item.getName());
            String normalizedUnit = IngredientNormalizer.normalizeUnit(item.getUnit());
            IngredientNormalizer.UnitCategory category = IngredientNormalizer.categoryOf(normalizedUnit);
            double baseQuantity = IngredientNormalizer.toBaseQuantity(item.getQuantity(), item.getUnit());

            AvailableQuantity existing = map.get(normalizedName);
            if (existing == null) {
                map.put(normalizedName, new AvailableQuantity(baseQuantity, category));
            } else if (existing.category == category) {
                existing.baseQuantity += baseQuantity;
            }
            // If categories conflict (rare data-entry edge case), the first
            // entry wins rather than crashing; good enough for this app's scope.
        }
        return map;
    }
}
