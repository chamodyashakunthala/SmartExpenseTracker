package com.example.smartexpensetracker.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.example.smartexpensetracker.BuildConfig;
import com.example.smartexpensetracker.R;
import com.example.smartexpensetracker.ai.GeminiApiService;
import com.example.smartexpensetracker.ai.GeminiRequest;
import com.example.smartexpensetracker.ai.GeminiResponse;
import com.example.smartexpensetracker.ai.RetrofitClient;
import com.example.smartexpensetracker.database.AppDatabase;
import com.example.smartexpensetracker.model.CategoryTotal;
import com.example.smartexpensetracker.model.Expense;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiAdviceActivity extends AppCompatActivity {

    private TextView tvAiResult, tvBack;
    private MaterialButton btnGetAdvice;
    private ProgressBar progressBarAi;
    private AppDatabase db;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_advice);

        db = AppDatabase.getInstance(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        tvAiResult = findViewById(R.id.tvAiResult);
        tvBack = findViewById(R.id.tvBack);
        btnGetAdvice = findViewById(R.id.btnGetAdvice);
        progressBarAi = findViewById(R.id.progressBarAi);

        tvBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });        btnGetAdvice.setOnClickListener(v -> fetchExpensesAndAnalyze());
    }

    private void fetchExpensesAndAnalyze() {
        setLoading(true);

        executorService.execute(() -> {
            List<Expense> expenses = db.expenseDao().getAllExpensesSync();
            List<CategoryTotal> categoryTotals = db.expenseDao().getCategoryTotalsSync();

            String prompt = buildPrompt(expenses, categoryTotals);

            runOnUiThread(() -> callGeminiApi(prompt));
        });
    }

    private String buildPrompt(List<Expense> expenses, List<CategoryTotal> categoryTotals) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a friendly financial advisor for a student. ");
        sb.append("Here is their expense data:\n\n");

        if (expenses == null || expenses.isEmpty()) {
            sb.append("No expenses recorded yet.\n");
        } else {
            sb.append("Category-wise totals:\n");
            for (CategoryTotal ct : categoryTotals) {
                sb.append("- ").append(ct.category).append(": Rs. ")
                        .append(String.format(Locale.getDefault(), "%.2f", ct.total)).append("\n");
            }
            sb.append("\nTotal recorded expenses: ").append(expenses.size()).append("\n");
        }

        sb.append("\nBased on this, give 3 short, practical, encouraging tips ");
        sb.append("(2-3 sentences each) to help this student manage their money better. ");
        sb.append("Keep the tone friendly and simple, no markdown formatting, just plain text.");

        return sb.toString();
    }

    private void callGeminiApi(String prompt) {
        GeminiApiService service = RetrofitClient.getGeminiApiService();
        GeminiRequest request = new GeminiRequest(prompt);

        service.generateContent(BuildConfig.GEMINI_API_KEY, request).enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null
                        && response.body().candidates != null && !response.body().candidates.isEmpty()) {
                    String resultText = response.body().candidates.get(0).content.parts.get(0).text;
                    tvAiResult.setText(resultText);
                } else {
            
                    tvAiResult.setText("Sorry , Couldn't get advice right now.Please try again later."");
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                setLoading(false);
                tvAiResult.setText("Network error: " + t.getMessage());
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBarAi.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnGetAdvice.setEnabled(!loading);
        if (loading) {
            tvAiResult.setText("Analyzing your spending...");
        }
    }
}
