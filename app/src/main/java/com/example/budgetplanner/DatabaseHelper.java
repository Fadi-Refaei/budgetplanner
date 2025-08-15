package com.example.budgetplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BudgetPlanner.db";
    private static final int DATABASE_VERSION = 1;

    // Tables
    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String TABLE_CATEGORIES = "categories";

    // Transaction columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DATE = "date";

    // Category columns
    private static final String COLUMN_CATEGORY_NAME = "name";
    private static final String COLUMN_CATEGORY_TYPE = "type";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTransactionsTable = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TYPE + " TEXT,"
                + COLUMN_CATEGORY + " TEXT,"
                + COLUMN_AMOUNT + " REAL,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_DATE + " TEXT" + ")";

        String createCategoriesTable = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CATEGORY_NAME + " TEXT,"
                + COLUMN_CATEGORY_TYPE + " TEXT" + ")";

        db.execSQL(createTransactionsTable);
        db.execSQL(createCategoriesTable);

        // Insert default categories
        insertDefaultCategories(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }

    private void insertDefaultCategories(SQLiteDatabase db) {
        String[] incomeCategories = {"Salary", "Freelance", "Investment", "Other Income"};
        String[] expenseCategories = {"Food", "Transportation", "Entertainment", "Utilities",
                "Healthcare", "Shopping", "Education", "Other Expense"};

        for (String category : incomeCategories) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY_NAME, category);
            values.put(COLUMN_CATEGORY_TYPE, "Income");
            db.insert(TABLE_CATEGORIES, null, values);
        }

        for (String category : expenseCategories) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY_NAME, category);
            values.put(COLUMN_CATEGORY_TYPE, "Expense");
            db.insert(TABLE_CATEGORIES, null, values);
        }
    }

    // Transaction CRUD operations
    public long addTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TYPE, transaction.getType());
        values.put(COLUMN_CATEGORY, transaction.getCategory());
        values.put(COLUMN_AMOUNT, transaction.getAmount());
        values.put(COLUMN_DESCRIPTION, transaction.getDescription());
        values.put(COLUMN_DATE, transaction.getDate());

        long result = db.insert(TABLE_TRANSACTIONS, null, values);
        db.close();
        return result;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRANSACTIONS, null, null, null, null, null, COLUMN_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();
                transaction.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                transaction.setType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
                transaction.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));
                transaction.setAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT)));
                transaction.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                transaction.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));

                transactions.add(transaction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return transactions;
    }

    public void deleteTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public double getTotalIncome() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_TRANSACTIONS +
                " WHERE " + COLUMN_TYPE + " = 'Income'", null);

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        db.close();
        return total;
    }

    public double getTotalExpense() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_TRANSACTIONS +
                " WHERE " + COLUMN_TYPE + " = 'Expense'", null);

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        db.close();
        return total;
    }

    // Category operations
    public List<String> getCategoriesByType(String type) {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CATEGORIES, new String[]{COLUMN_CATEGORY_NAME},
                COLUMN_CATEGORY_TYPE + " = ?", new String[]{type},
                null, null, COLUMN_CATEGORY_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return categories;
    }

    public void addCategory(String name, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, name);
        values.put(COLUMN_CATEGORY_TYPE, type);
        db.insert(TABLE_CATEGORIES, null, values);
        db.close();
    }
    public long updateTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TYPE, transaction.getType());
        values.put(COLUMN_CATEGORY, transaction.getCategory());
        values.put(COLUMN_AMOUNT, transaction.getAmount());
        values.put(COLUMN_DESCRIPTION, transaction.getDescription());
        values.put(COLUMN_DATE, transaction.getDate());

        long result = db.update(TABLE_TRANSACTIONS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(transaction.getId())});
        db.close();
        return result;
    }

}