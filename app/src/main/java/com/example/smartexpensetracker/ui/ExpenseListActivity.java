package com.example.smartexpensetracker.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartexpensetracker.R;
import com.example.smartexpensetracker.database.AppDatabase;
import com.example.smartexpensetracker.model.Expense;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewExpenses;
    private TextView tvEmptyMessage, tvBack;
    private ExpenseAdapter adapter;
    private AppDatabase db;
    private ExecutorService executorService;

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@androidx.annotation.NonNull RecyclerView recyclerView,
                                  @androidx.annotation.NonNull RecyclerView.ViewHolder viewHolder,
                                  @androidx.annotation.NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@androidx.annotation.NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Expense expense = adapter.getExpenseAt(position);
                if (expense != null) {
                    deleteExpense(expense);
                }
            }

            @Override
            public void onChildDraw(@androidx.annotation.NonNull Canvas c,
                                    @androidx.annotation.NonNull RecyclerView recyclerView,
                                    @androidx.annotation.NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;

                if (dX < 0) {
                    Paint paint = new Paint();
                    paint.setColor(Color.parseColor("#D32F2F"));
                    RectF background = new RectF(
                            itemView.getRight() + dX, itemView.getTop(),
                            itemView.getRight(), itemView.getBottom());
                    c.drawRoundRect(background, 14f, 14f, paint);

                    Paint textPaint = new Paint();
                    textPaint.setColor(Color.WHITE);
                    textPaint.setTextSize(40f);
                    textPaint.setTextAlign(Paint.Align.CENTER);
                    float textX = itemView.getRight() - 60f;
                    float textY = itemView.getTop() + (itemView.getHeight() / 2f) + 14f;
                    c.drawText("🗑", textX, textY, textPaint);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerViewExpenses);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        db = AppDatabase.getInstance(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        recyclerViewExpenses = findViewById(R.id.recyclerViewExpenses);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        tvBack = findViewById(R.id.tvBack);

        tvBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        adapter = new ExpenseAdapter(new ArrayList<>(), this::deleteExpense);
        recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewExpenses.setAdapter(adapter);
        setupSwipeToDelete();

        observeExpenses();
    }

    private void observeExpenses() {
        db.expenseDao().getAllExpenses().observe(this, expenses -> {
            if (expenses == null || expenses.isEmpty()) {
                tvEmptyMessage.setVisibility(View.VISIBLE);
                recyclerViewExpenses.setVisibility(View.GONE);
            } else {
                tvEmptyMessage.setVisibility(View.GONE);
                recyclerViewExpenses.setVisibility(View.VISIBLE);
                adapter.setExpenseList(expenses);
            }
        });
    }

    private void deleteExpense(Expense expense) {
        executorService.execute(() -> {
            db.expenseDao().delete(expense);
            runOnUiThread(() ->
                    Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show());
        });
    }
}