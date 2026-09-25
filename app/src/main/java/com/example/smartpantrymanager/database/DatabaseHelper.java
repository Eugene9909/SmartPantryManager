package com.example.smartpantrymanager.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Central place for the SQLite schema and first-run seed data.
 * Tables: pantry_items, recipes, recipe_ingredients (see project plan for the ER diagram).
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 2;

    // Table names
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    // pantry_items columns
    public static final String COL_PANTRY_ID = "id";
    public static final String COL_PANTRY_NAME = "name";
    public static final String COL_PANTRY_DISPLAY_NAME = "display_name";
    public static final String COL_PANTRY_QUANTITY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiry_date";

    // recipes columns
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_STEPS = "steps";

    // recipe_ingredients columns
    public static final String COL_RI_ID = "id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_INGREDIENT_NAME = "ingredient_name";
    public static final String COL_RI_QUANTITY = "required_quantity";
    public static final String COL_RI_UNIT = "unit";

    private static final String CREATE_TABLE_PANTRY =
            "CREATE TABLE " + TABLE_PANTRY + " (" +
                    COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_PANTRY_NAME + " TEXT NOT NULL, " +
                    COL_PANTRY_DISPLAY_NAME + " TEXT NOT NULL, " +
                    COL_PANTRY_QUANTITY + " REAL NOT NULL, " +
                    COL_PANTRY_UNIT + " TEXT NOT NULL, " +
                    COL_PANTRY_EXPIRY + " TEXT" +
                    ")";

    private static final String CREATE_TABLE_RECIPES =
            "CREATE TABLE " + TABLE_RECIPES + " (" +
                    COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_RECIPE_NAME + " TEXT NOT NULL, " +
                    COL_RECIPE_STEPS + " TEXT NOT NULL" +
                    ")";

    private static final String CREATE_TABLE_RECIPE_INGREDIENTS =
            "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                    COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                    COL_RI_INGREDIENT_NAME + " TEXT NOT NULL, " +
                    COL_RI_QUANTITY + " REAL NOT NULL, " +
                    COL_RI_UNIT + " TEXT NOT NULL, " +
                    "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES " +
                    TABLE_RECIPES + "(" + COL_RECIPE_ID + ") ON DELETE CASCADE" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PANTRY);
        db.execSQL(CREATE_TABLE_RECIPES);
        db.execSQL(CREATE_TABLE_RECIPE_INGREDIENTS);
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Inserts one recipe row and returns its generated id.
     */
    private long insertRecipe(SQLiteDatabase db, String name, String steps) {
        ContentValues values = new ContentValues();
        values.put(COL_RECIPE_NAME, name);
        values.put(COL_RECIPE_STEPS, steps);
        return db.insert(TABLE_RECIPES, null, values);
    }

    /**
     * Inserts one ingredient requirement row for a given recipe id.
     * ingredientName must already be normalized (lowercase, singular) so it
     * lines up with how pantry items are normalized before matching.
     */
    private void insertIngredient(SQLiteDatabase db, long recipeId, String ingredientName,
                                  double quantity, String unit) {
        ContentValues values = new ContentValues();
        values.put(COL_RI_RECIPE_ID, recipeId);
        values.put(COL_RI_INGREDIENT_NAME, ingredientName);
        values.put(COL_RI_QUANTITY, quantity);
        values.put(COL_RI_UNIT, unit);
        db.insert(TABLE_RECIPE_INGREDIENTS, null, values);
    }

    /**
     * Seeds 16 everyday recipes on first run. Ingredient names are simple,
     * common pantry staples so testing the strict-matching rule live in the
     * video demo is easy (add/remove one item and watch a recipe appear/disappear).
     */
    private void seedRecipes(SQLiteDatabase db) {
        long id;

        id = insertRecipe(db, "Cheese Omelette",
                "1. Whisk eggs with a pinch of salt.\n" +
                        "2. Melt butter in a pan over medium heat.\n" +
                        "3. Pour in eggs, sprinkle cheese, fold when set.");
        insertIngredient(db, id, "egg", 2, "unit");
        insertIngredient(db, id, "cheese", 30, "g");
        insertIngredient(db, id, "butter", 10, "g");
        insertIngredient(db, id, "salt", 1, "pinch");

        id = insertRecipe(db, "Grilled Cheese Sandwich",
                "1. Butter one side of each bread slice.\n" +
                        "2. Place cheese between the unbuttered sides.\n" +
                        "3. Grill in a pan until golden on both sides.");
        insertIngredient(db, id, "bread", 2, "unit");
        insertIngredient(db, id, "cheese", 40, "g");
        insertIngredient(db, id, "butter", 10, "g");

        id = insertRecipe(db, "Pasta Aglio e Olio",
                "1. Boil pasta until al dente.\n" +
                        "2. Gently fry sliced garlic in olive oil.\n" +
                        "3. Toss pasta through the garlic oil with salt.");
        insertIngredient(db, id, "pasta", 200, "g");
        insertIngredient(db, id, "garlic", 3, "unit");
        insertIngredient(db, id, "olive oil", 30, "ml");
        insertIngredient(db, id, "salt", 1, "pinch");

        id = insertRecipe(db, "Tomato Pasta",
                "1. Boil pasta until al dente.\n" +
                        "2. Simmer chopped tomato with garlic and oil.\n" +
                        "3. Toss pasta through the sauce.");
        insertIngredient(db, id, "pasta", 200, "g");
        insertIngredient(db, id, "tomato", 3, "unit");
        insertIngredient(db, id, "garlic", 2, "unit");
        insertIngredient(db, id, "olive oil", 20, "ml");

        id = insertRecipe(db, "Fried Rice",
                "1. Scramble egg in a hot wok.\n" +
                        "2. Add cooked rice and soy sauce, stir-fry.\n" +
                        "3. Mix in chopped onion, serve hot.");
        insertIngredient(db, id, "rice", 300, "g");
        insertIngredient(db, id, "egg", 2, "unit");
        insertIngredient(db, id, "onion", 1, "unit");
        insertIngredient(db, id, "soy sauce", 15, "ml");

        id = insertRecipe(db, "Pancakes",
                "1. Mix flour, milk, egg and sugar into a batter.\n" +
                        "2. Pour rounds of batter onto a hot buttered pan.\n" +
                        "3. Flip once bubbles form, cook until golden.");
        insertIngredient(db, id, "flour", 150, "g");
        insertIngredient(db, id, "milk", 200, "ml");
        insertIngredient(db, id, "egg", 1, "unit");
        insertIngredient(db, id, "sugar", 20, "g");
        insertIngredient(db, id, "butter", 10, "g");

        id = insertRecipe(db, "French Toast",
                "1. Whisk egg with milk and a little sugar.\n" +
                        "2. Dip bread slices, coating both sides.\n" +
                        "3. Fry in butter until golden on each side.");
        insertIngredient(db, id, "bread", 2, "unit");
        insertIngredient(db, id, "egg", 1, "unit");
        insertIngredient(db, id, "milk", 60, "ml");
        insertIngredient(db, id, "butter", 10, "g");
        insertIngredient(db, id, "sugar", 10, "g");

        id = insertRecipe(db, "Scrambled Eggs",
                "1. Whisk eggs with milk and a pinch of salt.\n" +
                        "2. Melt butter in a pan over low heat.\n" +
                        "3. Stir gently until softly set.");
        insertIngredient(db, id, "egg", 3, "unit");
        insertIngredient(db, id, "milk", 20, "ml");
        insertIngredient(db, id, "butter", 10, "g");
        insertIngredient(db, id, "salt", 1, "pinch");

        id = insertRecipe(db, "Garlic Bread",
                "1. Mix softened butter with crushed garlic.\n" +
                        "2. Spread over sliced bread.\n" +
                        "3. Bake or grill until crisp.");
        insertIngredient(db, id, "bread", 4, "unit");
        insertIngredient(db, id, "butter", 40, "g");
        insertIngredient(db, id, "garlic", 3, "unit");

        id = insertRecipe(db, "Peanut Butter Toast",
                "1. Toast the bread.\n" +
                        "2. Spread peanut butter generously.\n" +
                        "3. Slice banana on top if desired.");
        insertIngredient(db, id, "bread", 2, "unit");
        insertIngredient(db, id, "peanut butter", 30, "g");
        insertIngredient(db, id, "banana", 1, "unit");

        id = insertRecipe(db, "Banana Smoothie",
                "1. Add banana and milk to a blender.\n" +
                        "2. Add a spoon of sugar to taste.\n" +
                        "3. Blend until smooth and serve chilled.");
        insertIngredient(db, id, "banana", 2, "unit");
        insertIngredient(db, id, "milk", 250, "ml");
        insertIngredient(db, id, "sugar", 10, "g");

        id = insertRecipe(db, "Vegetable Stir Fry",
                "1. Heat oil in a wok until shimmering.\n" +
                        "2. Add chopped onion and garlic, stir-fry briefly.\n" +
                        "3. Add tomato, season with soy sauce, cook until tender.");
        insertIngredient(db, id, "onion", 1, "unit");
        insertIngredient(db, id, "garlic", 2, "unit");
        insertIngredient(db, id, "tomato", 2, "unit");
        insertIngredient(db, id, "olive oil", 20, "ml");
        insertIngredient(db, id, "soy sauce", 10, "ml");

        id = insertRecipe(db, "Tomato Soup",
                "1. Simmer chopped tomato and onion in a pot with water.\n" +
                        "2. Blend until smooth.\n" +
                        "3. Season with salt and a little butter.");
        insertIngredient(db, id, "tomato", 5, "unit");
        insertIngredient(db, id, "onion", 1, "unit");
        insertIngredient(db, id, "butter", 10, "g");
        insertIngredient(db, id, "salt", 1, "pinch");

        id = insertRecipe(db, "Beans on Toast",
                "1. Toast the bread.\n" +
                        "2. Heat the beans in a small pot.\n" +
                        "3. Pour beans over toast and serve.");
        insertIngredient(db, id, "bread", 2, "unit");
        insertIngredient(db, id, "beans", 200, "g");

        id = insertRecipe(db, "Mashed Potatoes",
                "1. Boil potato until soft.\n" +
                        "2. Mash with butter and milk.\n" +
                        "3. Season with salt to taste.");
        insertIngredient(db, id, "potato", 400, "g");
        insertIngredient(db, id, "butter", 20, "g");
        insertIngredient(db, id, "milk", 50, "ml");
        insertIngredient(db, id, "salt", 1, "pinch");

        id = insertRecipe(db, "Cheese Quesadilla",
                "1. Sprinkle cheese over one tortilla.\n" +
                        "2. Top with a second tortilla, press together.\n" +
                        "3. Cook in a dry pan until cheese melts and browns.");
        insertIngredient(db, id, "tortilla", 2, "unit");
        insertIngredient(db, id, "cheese", 50, "g");
    }
}
