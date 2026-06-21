package com.example.smartexpensetracker.ui;

import android.graphics.drawable.GradientDrawable;
import com.example.smartexpensetracker.utils.CategoryStyleHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartexpensetracker.R;
import com.example.smartexpensetracker.model.Expense;

import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList;
    private final OnExpenseDeleteListener deleteListener;

    public interface OnExpenseDeleteListener {
        void onDelete(Expense expense);
    }

    public ExpenseAdapter(List<Expense> expenseList, OnExpenseDeleteListener deleteListener) {
        this.expenseList = expenseList;
        this.deleteListener = deleteListener;
    }
    public void setExpenseList(List<Expense> newList) {
        this.expenseList = newList;
        notifyDataSetChanged();
    }
    public Expense getExpenseAt(int position) {
        if (expenseList != null && position >= 0 && position < expenseList.size()) {
            return expenseList.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);

        holder.tvItemTitle.setText(expense.title);
        holder.tvItemCategoryDate.setText(expense.category + " • " + expense.date);
        holder.tvItemAmount.setText(String.format(Locale.getDefault(), "Rs. %.2f", expense.amount));

        holder.tvItemIcon.setText(CategoryStyleHelper.getEmoji(expense.category));
        int categoryColor = CategoryStyleHelper.getColor(holder.itemView.getContext(), expense.category);

        GradientDrawable circleBackground = new GradientDrawable();
        circleBackground.setShape(GradientDrawable.OVAL);
        circleBackground.setColor(adjustAlpha(categoryColor, 0.15f));
        holder.tvItemIcon.setBackground(circleBackground);

        holder.tvItemDelete.setOnClickListener(v -> deleteListener.onDelete(expense));
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(android.graphics.Color.alpha(color) * factor);
        int red = android.graphics.Color.red(color);
        int green = android.graphics.Color.green(color);
        int blue = android.graphics.Color.blue(color);
        return android.graphics.Color.argb(alpha, red, green, blue);
    }

    @Override
    public int getItemCount() {
        return expenseList == null ? 0 : expenseList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemTitle, tvItemCategoryDate, tvItemAmount, tvItemDelete, tvItemIcon;

        ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvItemCategoryDate = itemView.findViewById(R.id.tvItemCategoryDate);
            tvItemAmount = itemView.findViewById(R.id.tvItemAmount);
            tvItemDelete = itemView.findViewById(R.id.tvItemDelete);
            tvItemIcon = itemView.findViewById(R.id.tvItemIcon);
        }
    }
}