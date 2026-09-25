package com.example.smartpantrymanager.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Handles the "reasonably robust to real-world messiness" requirement from
 * Section 2.3: singular/plural differences ("tomato" vs "tomatoes") and
 * unit differences (e.g. 500 g vs 0.5 kg) should not break matching.
 *
 * This is NOT full NLP — it's a small, explainable rule set, which is what
 * the brief asks for. Two responsibilities:
 *   1. normalizeName()  - lowercase, trim, naive singularize + irregular exceptions.
 *   2. toBaseQuantity()  - converts a (quantity, unit) into a common base unit
 *                          per unit "category" (weight/volume/count) so
 *                          quantities in different units can be compared.
 */
public class IngredientNormalizer {

    /** Broad category a unit belongs to. Quantities can only be compared within the same category. */
    public enum UnitCategory {
        WEIGHT, VOLUME, COUNT
    }

    // Common irregular plurals that the naive "strip trailing s/es" rule gets wrong.
    private static final Map<String, String> IRREGULAR_SINGULARS = new HashMap<>();
    static {
        IRREGULAR_SINGULARS.put("leaves", "leaf");
        IRREGULAR_SINGULARS.put("loaves", "loaf");
        IRREGULAR_SINGULARS.put("potatoes", "potato");
        IRREGULAR_SINGULARS.put("tomatoes", "tomato");
        IRREGULAR_SINGULARS.put("cherries", "cherry");
        IRREGULAR_SINGULARS.put("berries", "berry");
    }

    private IngredientNormalizer() {
        // Utility class - no instances.
    }

    /**
     * Normalizes an ingredient name for comparison: lowercase, trimmed,
     * collapsed internal whitespace, and singularized (irregulars first,
     * then a naive trailing s/es rule as a fallback).
     */
    public static String normalizeName(String rawName) {
        if (rawName == null) {
            return "";
        }
        String s = rawName.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");

        if (IRREGULAR_SINGULARS.containsKey(s)) {
            return IRREGULAR_SINGULARS.get(s);
        }

        if (s.endsWith("ies") && s.length() > 4) {
            // e.g. "berries" already handled above, but covers unseen -ies words too.
            return s.substring(0, s.length() - 3) + "y";
        }
        if (s.endsWith("es") && s.length() > 3) {
            return s.substring(0, s.length() - 2);
        }
        if (s.endsWith("s") && !s.endsWith("ss") && s.length() > 2) {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    /** Normalizes a unit string: lowercase, trimmed, common synonyms collapsed. */
    public static String normalizeUnit(String rawUnit) {
        if (rawUnit == null) {
            return "unit";
        }
        String u = rawUnit.trim().toLowerCase(Locale.ROOT);
        switch (u) {
            case "gram":
            case "grams":
            case "gs":
                return "g";
            case "kilogram":
            case "kilograms":
            case "kgs":
                return "kg";
            case "millilitre":
            case "milliliter":
            case "millilitres":
            case "milliliters":
            case "mls":
                return "ml";
            case "litre":
            case "liter":
            case "litres":
            case "liters":
                return "l";
            case "units":
            case "pieces":
            case "piece":
                return "unit";
            default:
                return u;
        }
    }

    public static UnitCategory categoryOf(String normalizedUnit) {
        switch (normalizedUnit) {
            case "g":
            case "kg":
                return UnitCategory.WEIGHT;
            case "ml":
            case "l":
            case "tsp":
            case "tbsp":
            case "cup":
                return UnitCategory.VOLUME;
            default:
                return UnitCategory.COUNT; // "unit", "pinch", etc.
        }
    }

    /**
     * Converts a quantity into its category's base unit:
     * grams for WEIGHT, millilitres for VOLUME, and the quantity itself for COUNT.
     * This is what lets "500 g" and "0.5 kg" compare as equal.
     */
    public static double toBaseQuantity(double quantity, String rawUnit) {
        String unit = normalizeUnit(rawUnit);
        switch (unit) {
            case "kg":
                return quantity * 1000.0; // -> grams
            case "g":
                return quantity;
            case "l":
                return quantity * 1000.0; // -> millilitres
            case "ml":
                return quantity;
            case "tsp":
                return quantity * 5.0;    // -> millilitres (approx)
            case "tbsp":
                return quantity * 15.0;   // -> millilitres (approx)
            case "cup":
                return quantity * 250.0;  // -> millilitres (approx)
            default:
                return quantity; // COUNT-based units compare directly
        }
    }
}
