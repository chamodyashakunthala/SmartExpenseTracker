package com.example.smartexpensetracker.ui;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.components.Legend;
import com.example.smartexpensetracker.model.CategoryTotal;
import com.example.smartexpensetracker.utils.CategoryStyleHelper;
import java.util.ArrayList;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartexpensetracker.R;
import com.example.smartexpensetracker.database.AppDatabase;

import java.util.ArrayList;
import java.util.Locale;
import java.util.List;

public class SummaryActivity extends AppCompatActivity {

    private TextView tvSummaryTotal, tvSummaryEmpty, tvBack;
    private RecyclerView recyclerViewCategorySummary;
    private CategorySummaryAdapter adapter;
    private AppDatabase db;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        db = AppDatabase.getInstance(getApplicationContext());

        tvSummaryTotal = findViewById(R.id.tvSummaryTotal);
        tvSummaryEmpty = findViewById(R.id.tvSummaryEmpty);
        tvBack = findViewById(R.id.tvBack);
        recyclerViewCategorySummary = findViewById(R.id.recyclerViewCategorySummary);
        pieChart = findViewById(R.id.pieChart);

        tvBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        adapter = new CategorySummaryAdapter(new ArrayList<>());
        recyclerViewCategorySummary.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCategorySummary.setAdapter(adapter);

        observeTotal();
        observeCategoryBreakdown();
    }

    private void observeTotal() {
        db.expenseDao().getTotalExpenses().observe(this, total -> {
            double value = (total == null) ? 0.0 : total;
            tvSummaryTotal.setText(String.format(Locale.getDefault(), "Rs. %.2f", value));
        });
    }

    private void observeCategoryBreakdown() {
        db.expenseDao().getCategoryTotals().observe(this, categoryTotals -> {
            if (categoryTotals == null || categoryTotals.isEmpty()) {
                tvSummaryEmpty.setVisibility(View.VISIBLE);
                recyclerViewCategorySummary.setVisibility(View.GONE);
                pieChart.setVisibility(View.GONE);
            } else {
                tvSummaryEmpty.setVisibility(View.GONE);
                recyclerViewCategorySummary.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.VISIBLE);
                adapter.setCategoryTotals(categoryTotals);
                updatePieChart(categoryTotals);
            }
        });
    }

    private void updatePieChart(List<CategoryTotal> categoryTotals) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for (CategoryTotal ct : categoryTotals) {
            entries.add(new PieEntry((float) ct.total, ct.category));
            colors.add(CategoryStyleHelper.getColor(this, ct.category));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(android.graphics.Color.WHITE);
        dataSet.setSliceSpace(2f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setCenterText("Spending");
        pieChart.setCenterTextSize(14f);
        pieChart.setEntryLabelTextSize(11f);
        pieChart.getDescription().setEnabled(false);

        Legend legend = pieChart.getLegend();
        legend.setTextSize(12f);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setWordWrapEnabled(true);

        pieChart.animateY(800);
        pieChart.invalidate();
    }
}