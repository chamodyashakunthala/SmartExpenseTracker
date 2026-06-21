package com.example.smartexpensetracker.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.smartexpensetracker.model.CategoryTotal;
import com.example.smartexpensetracker.model.Expense;
import java.util.List;

@Dao
public interface ExpenseDao {

    @Insert
    void insert(Expense expense);

    @Update
    void update(Expense expense);

    @Delete
    void delete(Expense expense);

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    LiveData<List<Expense>> getAllExpenses();

    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    Expense getExpenseById(int expenseId);

    @Query("SELECT SUM(amount) FROM expenses")
    LiveData<Double> getTotalExpenses();

    @Query("SELECT category, SUM(amount) as total FROM expenses GROUP BY category")
    LiveData<List<CategoryTotal>> getCategoryTotals();

    @Query("SELECT * FROM expenses")
    List<Expense> getAllExpensesSync();

    @Query("SELECT category, SUM(amount) as total FROM expenses GROUP BY category")
    List<CategoryTotal> getCategoryTotalsSync();
}