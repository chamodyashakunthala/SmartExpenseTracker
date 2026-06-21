package com.example.smartexpensetracker.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.smartexpensetracker.R;
import com.example.smartexpensetracker.database.AppDatabase;
import com.example.smartexpensetracker.model.Expense;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddExpenseActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etAmount, etNote;
    private Spinner spinnerCategory;
    private TextView tvDate, tvBack;
    private MaterialButton btnSaveExpense;

    private String selectedDate = "";
    private AppDatabase db;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        db = AppDatabase.getInstance(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        etTitle = findViewById(R.id.etTitle);
        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etNote);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        tvDate = findViewById(R.id.tvDate);
        tvBack = findViewById(R.id.tvBack);
        btnSaveExpense = findViewById(R.id.btnSaveExpense);

        setupCategorySpinner();

        tvDate.setOnClickListener(v -> showDatePicker());

        btnSaveExpense.setOnClickListener(v -> saveExpense());

        tvBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });    }

    private void setupCategorySpinner() {
        String[] rawCategories = {"Food", "Transportation", "Entertainment", "Education", "Bills", "Other"};
        String[] displayCategories = new String[rawCategories.length];

        for (int i = 0; i < rawCategories.length; i++) {
            displayCategories[i] = com.example.smartexpensetracker.utils.CategoryStyleHelper.getEmoji(rawCategories[i])
                    + "  " + rawCategories[i];
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, displayCategories);
        spinnerCategory.setAdapter(adapter);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    tvDate.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void saveExpense() {
        String title = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString().replaceAll("^[^a-zA-Z]+", "").trim();        String note = etNote.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Please enter a title.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Please enter an amount.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(this, "Please select a date.", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount.", Toast.LENGTH_SHORT).show();
            return;
        }

        Expense expense = new Expense(title, amount, category, selectedDate, note);

        executorService.execute(() -> {
            db.expenseDao().insert(expense);
            runOnUiThread(() -> {
                Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            });
        });
    }
}