package com.example.smartexpensetracker.utils;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.example.smartexpensetracker.R;

public class CategoryStyleHelper {

    public static int getColor(Context context, String category) {
        switch (category) {
            case "Food": return ContextCompat.getColor(context, R.color.cat_food);
            case "Transportation": return ContextCompat.getColor(context, R.color.cat_transport);
            case "Entertainment": return ContextCompat.getColor(context, R.color.cat_entertainment);
            case "Education": return ContextCompat.getColor(context, R.color.cat_education);
            case "Bills": return ContextCompat.getColor(context, R.color.cat_bills);
            default: return ContextCompat.getColor(context, R.color.cat_other);
        }
    }

    public static String getEmoji(String category) {
        switch (category) {
            case "Food": return "🍔";
            case "Transportation": return "🚌";
            case "Entertainment": return "🎬";
            case "Education": return "📚";
            case "Bills": return "💡";
            default: return "💰";
        }
    }
}