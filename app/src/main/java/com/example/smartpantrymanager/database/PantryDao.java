package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.smartpantrymanager.model.PantryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Data-access object for the pantry_items table.
 * Full CRUD: insert (Create), getAll/getById (Read), update (Update), delete (Delete).
 *
 * Every insert/update normalizes the ingredient name (lowercase, trimmed, naive
 * singular form) into the `name` column while keeping the user's original typing
 * in `display_name`. This is what lets MatchingEngine compare pantry items against
 * recipe ingredients reliably later on.
 */
public class PantryDao {

    private final DatabaseHelper dbHelper;

    public PantryDao(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * Naive normalization: lowercase, trim, strip a single trailing "s" or "es"
     * for a basic singular form. Good enough for common cases like
     * "Tomatoes" -> "tomato", "Onions" -> "onion". Refined further in
     * IngredientNormalizer once that's wired in for recipe matching.
     */
    private String normalize(String rawName) {
        String s = rawName.trim().toLowerCase(Locale.ROOT);
        if (s.endsWith("es") && s.length() > 3) {
            s = s.substring(0, s.length() - 2);
        } else if (s.endsWith("s") && !s.endsWith("ss") && s.length() > 2) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    /** Create: inserts a new pantry item and returns its generated id, or -1 on failure. */
    public long insert(PantryItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_PANTRY_NAME, normalize(item.getDisplayName()));
        values.put(DatabaseHelper.COL_PANTRY_DISPLAY_NAME, item.getDisplayName().trim());
        values.put(DatabaseHelper.COL_PANTRY_QUANTITY, item.getQuantity());
        values.put(DatabaseHelper.COL_PANTRY_UNIT, item.getUnit());
        values.put(DatabaseHelper.COL_PANTRY_EXPIRY, item.getExpiryDate()); // may be null
        long id = db.insert(DatabaseHelper.TABLE_PANTRY, null, values);
        db.close();
        return id;
    }

    /** Read: returns every pantry item, ordered by display name. */
    public List<PantryItem> getAll() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PANTRY,
                null,
                null, null, null, null,
                DatabaseHelper.COL_PANTRY_DISPLAY_NAME + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                items.add(cursorToPantryItem(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }

    /** Read: returns a single pantry item by id, or null if not found. */
    public PantryItem getById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PANTRY,
                null,
                DatabaseHelper.COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        PantryItem item = null;
        if (cursor.moveToFirst()) {
            item = cursorToPantryItem(cursor);
        }
        cursor.close();
        db.close();
        return item;
    }

    /** Update: saves changes to an existing pantry item (matched by id). Returns rows affected. */
    public int update(PantryItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_PANTRY_NAME, normalize(item.getDisplayName()));
        values.put(DatabaseHelper.COL_PANTRY_DISPLAY_NAME, item.getDisplayName().trim());
        values.put(DatabaseHelper.COL_PANTRY_QUANTITY, item.getQuantity());
        values.put(DatabaseHelper.COL_PANTRY_UNIT, item.getUnit());
        values.put(DatabaseHelper.COL_PANTRY_EXPIRY, item.getExpiryDate());

        int rows = db.update(
                DatabaseHelper.TABLE_PANTRY,
                values,
                DatabaseHelper.COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(item.getId())}
        );
        db.close();
        return rows;
    }

    /** Delete: removes a pantry item by id. Returns rows affected (0 or 1). */
    public int delete(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                DatabaseHelper.TABLE_PANTRY,
                DatabaseHelper.COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }

    private PantryItem cursorToPantryItem(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_NAME));
        String displayName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_DISPLAY_NAME));
        double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_QUANTITY));
        String unit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_UNIT));
        String expiry = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_EXPIRY));
        return new PantryItem(id, name, displayName, quantity, unit, expiry);
    }
}