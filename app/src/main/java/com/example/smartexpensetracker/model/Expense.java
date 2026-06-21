package com.example.smartexpensetracker.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "expenses")
public class Expense {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String title;

    public double amount;

    @NonNull
    public String category;

    @NonNull
    public String date; // stored as "yyyy-MM-dd"

    public String note;

    public Expense(@NonNull String title, double amount, @NonNull String category,
                   @NonNull String date, String note) {
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.note = note;
    }
}