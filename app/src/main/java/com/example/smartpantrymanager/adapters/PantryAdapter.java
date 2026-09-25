package com.example.smartpantrymanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.PantryItem;

import java.util.List;

/**
 * Binds a List<PantryItem> to a RecyclerView, with edit/delete actions
 * delegated back to whoever owns the adapter (the fragment) via a
 * listener interface, keeping this class free of any database calls.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    /** Callback so the hosting Fragment/Activity decides what edit/delete actually does. */
    public interface OnPantryItemActionListener {
        void onEditClicked(PantryItem item);
        void onDeleteClicked(PantryItem item);
    }

    private final List<PantryItem> items;
    private final OnPantryItemActionListener listener;

    public PantryAdapter(List<PantryItem> items, OnPantryItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);

        holder.textItemName.setText(item.getDisplayName());
        holder.textItemQuantity.setText(
                formatQuantity(item.getQuantity()) + " " + item.getUnit());

        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
            holder.textItemExpiry.setVisibility(View.VISIBLE);
            holder.textItemExpiry.setText("Expires: " + item.getExpiryDate());
        } else {
            holder.textItemExpiry.setVisibility(View.GONE);
        }

        holder.buttonEditItem.setOnClickListener(v -> {
            if (listener != null) listener.onEditClicked(item);
        });

        holder.buttonDeleteItem.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClicked(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /** Drops a trailing ".0" for whole numbers (e.g. "3" instead of "3.0"). */
    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity) && !Double.isInfinite(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    /** Replaces the adapter's data set (e.g. after a CRUD change) and refreshes the list. */
    public void updateData(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView textItemName;
        TextView textItemQuantity;
        TextView textItemExpiry;
        ImageButton buttonEditItem;
        ImageButton buttonDeleteItem;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            textItemName = itemView.findViewById(R.id.textItemName);
            textItemQuantity = itemView.findViewById(R.id.textItemQuantity);
            textItemExpiry = itemView.findViewById(R.id.textItemExpiry);
            buttonEditItem = itemView.findViewById(R.id.buttonEditItem);
            buttonDeleteItem = itemView.findViewById(R.id.buttonDeleteItem);
        }
    }
}
