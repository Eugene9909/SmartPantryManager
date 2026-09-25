package com.example.smartpantrymanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.activities.AddEditIngredientActivity;
import com.example.smartpantrymanager.adapters.PantryAdapter;
import com.example.smartpantrymanager.database.PantryDao;
import com.example.smartpantrymanager.model.PantryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows every pantry item in a RecyclerView, with a FAB to add a new one
 * and edit/delete actions per row. This is the "Pantry List" screen from
 * the assignment brief (Section 2.2).
 *
 * Note: PantryDao calls run on the main thread here for simplicity, which
 * is acceptable at this app's scale (a handful of pantry rows). If this
 * were a larger dataset, these calls would move to a background thread
 * (e.g. via ExecutorService) to avoid blocking the UI.
 */
public class PantryListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyStateText;
    private FloatingActionButton fabAdd;

    private PantryDao pantryDao;
    private PantryAdapter adapter;
    private final List<PantryItem> pantryItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pantry_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerPantryList);
        emptyStateText = view.findViewById(R.id.textEmptyPantry);
        fabAdd = view.findViewById(R.id.fabAddIngredient);

        pantryDao = new PantryDao(requireContext());

        adapter = new PantryAdapter(pantryItems, new PantryAdapter.OnPantryItemActionListener() {
            @Override
            public void onEditClicked(PantryItem item) {
                startActivity(AddEditIngredientActivity.newIntentForEdit(
                        requireContext(), item.getId()));
            }

            @Override
            public void onDeleteClicked(PantryItem item) {
                confirmDelete(item);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v ->
                startActivity(AddEditIngredientActivity.newIntentForAdd(requireContext())));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload every time the screen becomes visible, so changes made on
        // the Add/Edit screen (once wired up) are reflected immediately.
        loadPantryItems();
    }

    private void loadPantryItems() {
        List<PantryItem> latest = pantryDao.getAll();
        adapter.updateData(latest);
        updateEmptyState(latest.isEmpty());
    }

    private void updateEmptyState(boolean isEmpty) {
        emptyStateText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void confirmDelete(PantryItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete ingredient")
                .setMessage("Remove \"" + item.getDisplayName() + "\" from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    pantryDao.delete(item.getId());
                    loadPantryItems();
                    Toast.makeText(requireContext(),
                            item.getDisplayName() + " deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
