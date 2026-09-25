package com.example.smartpantrymanager.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.database.PantryDao;
import com.example.smartpantrymanager.model.PantryItem;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

/**
 * Add/Edit Ingredient screen (Section 2.2). Handles both modes depending on
 * whether EXTRA_ITEM_ID was passed in:
 *  - No extra (or -1)  -> "Add" mode, creates a new pantry item.
 *  - Valid id passed    -> "Edit" mode, loads and updates the existing item.
 *
 * Validation: name must not be blank, quantity must be a positive number.
 * Expiry date is optional.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";
    private static final long NO_ID = -1;

    private TextInputLayout layoutName;
    private TextInputLayout layoutQuantity;
    private TextInputEditText editName;
    private TextInputEditText editQuantity;
    private Spinner spinnerUnit;
    private TextView textExpiryDate;
    private TextView textScreenTitle;

    private PantryDao pantryDao;
    private long editingItemId = NO_ID;
    private String selectedExpiryDate = null; // ISO 8601, null if not set

    /** Convenience factory so callers don't need to know the extra key by name. */
    public static Intent newIntentForAdd(Context context) {
        return new Intent(context, AddEditIngredientActivity.class);
    }

    public static Intent newIntentForEdit(Context context, long itemId) {
        Intent intent = new Intent(context, AddEditIngredientActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity_add_edit_ingredient);

        pantryDao = new PantryDao(this);

        textScreenTitle = findViewById(R.id.textScreenTitle);
        layoutName = findViewById(R.id.layoutName);
        layoutQuantity = findViewById(R.id.layoutQuantity);
        editName = findViewById(R.id.editIngredientName);
        editQuantity = findViewById(R.id.editQuantity);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        textExpiryDate = findViewById(R.id.textExpiryDate);
        Button buttonPickDate = findViewById(R.id.buttonPickDate);
        Button buttonClearDate = findViewById(R.id.buttonClearDate);
        Button buttonSave = findViewById(R.id.buttonSave);

        editingItemId = getIntent().getLongExtra(EXTRA_ITEM_ID, NO_ID);

        if (editingItemId != NO_ID) {
            loadExistingItem(editingItemId);
        }

        buttonPickDate.setOnClickListener(v -> showDatePicker());
        buttonClearDate.setOnClickListener(v -> {
            selectedExpiryDate = null;
            textExpiryDate.setText("No expiry date set");
        });

        buttonSave.setOnClickListener(v -> attemptSave());
    }

    private void loadExistingItem(long itemId) {
        PantryItem item = pantryDao.getById(itemId);
        if (item == null) {
            Toast.makeText(this, "Could not find that item", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textScreenTitle.setText("Edit Ingredient");
        editName.setText(item.getDisplayName());
        editQuantity.setText(formatQuantity(item.getQuantity()));

        String[] units = getResources().getStringArray(R.array.units_array);
        for (int i = 0; i < units.length; i++) {
            if (units[i].equalsIgnoreCase(item.getUnit())) {
                spinnerUnit.setSelection(i);
                break;
            }
        }

        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
            selectedExpiryDate = item.getExpiryDate();
            textExpiryDate.setText(selectedExpiryDate);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedExpiryDate = String.format(Locale.ROOT, "%04d-%02d-%02d",
                    year, month + 1, dayOfMonth);
            textExpiryDate.setText(selectedExpiryDate);
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    /**
     * Validates the form and, if valid, saves (insert or update depending on mode).
     * Returns early on the first validation failure, showing an inline error.
     */
    private void attemptSave() {
        layoutName.setError(null);
        layoutQuantity.setError(null);

        String name = editName.getText() != null ? editName.getText().toString().trim() : "";
        String quantityText = editQuantity.getText() != null
                ? editQuantity.getText().toString().trim() : "";

        boolean isValid = true;

        if (name.isEmpty()) {
            layoutName.setError("Ingredient name is required");
            isValid = false;
        }

        double quantity = 0;
        if (quantityText.isEmpty()) {
            layoutQuantity.setError("Quantity is required");
            isValid = false;
        } else {
            try {
                quantity = Double.parseDouble(quantityText);
                if (quantity <= 0) {
                    layoutQuantity.setError("Quantity must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                layoutQuantity.setError("Enter a valid number");
                isValid = false;
            }
        }

        if (!isValid) {
            return;
        }

        String unit = (String) spinnerUnit.getSelectedItem();

        if (editingItemId == NO_ID) {
            PantryItem newItem = new PantryItem(name, name, quantity, unit, selectedExpiryDate);
            pantryDao.insert(newItem);
            Toast.makeText(this, name + " added to pantry", Toast.LENGTH_SHORT).show();
        } else {
            PantryItem updated = new PantryItem(
                    editingItemId, name.toLowerCase(Locale.ROOT), name, quantity, unit, selectedExpiryDate);
            pantryDao.update(updated);
            Toast.makeText(this, name + " updated", Toast.LENGTH_SHORT).show();
        }

        // PantryListFragment reloads its data in onResume, so simply finishing
        // this Activity is enough to reflect the change on the list screen.
        finish();
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity) && !Double.isInfinite(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }
}
