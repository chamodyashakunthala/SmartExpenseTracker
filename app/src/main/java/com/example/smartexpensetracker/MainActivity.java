package com.example.smartexpensetracker;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartexpensetracker.database.AppDatabase;
import com.example.smartexpensetracker.ui.AddExpenseActivity;
import com.example.smartexpensetracker.ui.AiAdviceActivity;
import com.example.smartexpensetracker.ui.ExpenseListActivity;
import com.example.smartexpensetracker.ui.LoginActivity;
import com.example.smartexpensetracker.ui.SummaryActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import android.app.AlarmManager;
import android.app.PendingIntent;
import com.example.smartexpensetracker.utils.ReminderReceiver;
import java.util.Calendar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome, tvLogout, tvTotalExpense;
    private MaterialButton btnAddExpense, btnViewExpenses, btnViewSummary, btnAiAdvice;
    private FirebaseAuth mAuth;
    private AppDatabase db;

    private void requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
    }
    private void scheduleDailyReminder() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20); // 8:00 PM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // If 8 PM has already passed today, schedule for tomorrow
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (alarmManager != null) {
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestNotificationPermission();
        scheduleDailyReminder();

        mAuth = FirebaseAuth.getInstance();
        db = AppDatabase.getInstance(getApplicationContext());


        tvWelcome = findViewById(R.id.tvWelcome);
        tvLogout = findViewById(R.id.tvLogout);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnViewExpenses = findViewById(R.id.btnViewExpenses);
        btnViewSummary = findViewById(R.id.btnViewSummary);
        btnAiAdvice = findViewById(R.id.btnAiAdvice);

        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail() != null) {
            tvWelcome.setText("Welcome, " + mAuth.getCurrentUser().getEmail().split("@")[0]);
        }

        btnAddExpense.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddExpenseActivity.class)));
             overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

        btnViewExpenses.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ExpenseListActivity.class)));
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);


        btnViewSummary.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SummaryActivity.class)));
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);


        btnAiAdvice.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AiAdviceActivity.class)));
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);


        tvLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            finish();
        });

        observeTotalExpense();
    }

    private void observeTotalExpense() {
        db.expenseDao().getTotalExpenses().observe(this, total -> {
            double value = (total == null) ? 0.0 : total;
            tvTotalExpense.setText(String.format(Locale.getDefault(), "Rs. %.2f", value));
        });
    }
}