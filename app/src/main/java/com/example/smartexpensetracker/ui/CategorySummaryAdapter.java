package com.example.smartexpensetracker.ui;

import com.example.smartexpensetracker.utils.CategoryStyleHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartexpensetracker.R;
import com.example.smartexpensetracker.model.CategoryTotal;

import java.util.List;
import java.util.Locale;

public class CategorySummaryAdapter extends RecyclerView.Adapter<CategorySummaryAdapter.CategoryViewHolder> {

    private List<CategoryTotal> categoryTotals;

    public CategorySummaryAdapter(List<CategoryTotal> categoryTotals) {
        this.categoryTotals = categoryTotals;
    }

    public void setCategoryTotals(List<CategoryTotal> newList) {
        this.categoryTotals = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_summary, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryTotal item = categoryTotals.get(position);
        holder.tvCategoryName.setText(item.category);
        holder.tvCategoryAmount.setText(String.format(Locale.getDefault(), "Rs. %.2f", item.total));

        int color = CategoryStyleHelper.getColor(holder.itemView.getContext(), item.category);
        holder.viewCategoryDot.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return categoryTotals == null ? 0 : categoryTotals.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvCategoryAmount;
        View viewCategoryDot;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryAmount = itemView.findViewById(R.id.tvCategoryAmount);
            viewCategoryDot = itemView.findViewById(R.id.viewCategoryDot);
        }
    }
}