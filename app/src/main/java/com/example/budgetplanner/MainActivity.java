package com.example.budgetplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private TextView totalIncomeText, totalExpenseText, remainingBudgetText;
    private RecyclerView transactionsRecyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        initializeViews();
        setupRecyclerView();
        setupButtonListeners();
        updateBudgetSummary();
    }

    private void initializeViews() {
        totalIncomeText = findViewById(R.id.totalIncomeText);
        totalExpenseText = findViewById(R.id.totalExpenseText);
        remainingBudgetText = findViewById(R.id.remainingBudgetText);
        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView);

        Button addIncomeBtn = findViewById(R.id.addIncomeBtn);
        Button addExpenseBtn = findViewById(R.id.addExpenseBtn);
        Button viewCategoriesBtn = findViewById(R.id.viewCategoriesBtn);

        addIncomeBtn.setOnClickListener(v -> openAddTransaction("Income"));
        addExpenseBtn.setOnClickListener(v -> openAddTransaction("Expense"));
        viewCategoriesBtn.setOnClickListener(v -> openCategoriesActivity());
    }

    private void setupRecyclerView() {
        transactions = dbHelper.getAllTransactions();
        transactionAdapter = new TransactionAdapter(transactions, this::deleteTransaction);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionsRecyclerView.setAdapter(transactionAdapter);
    }

    private void setupButtonListeners() {
        // Already handled in initializeViews()
    }

    private void openAddTransaction(String type) {
        Intent intent = new Intent(this, AddTransactionActivity.class);
        intent.putExtra("TRANSACTION_TYPE", type);
        startActivity(intent);
    }

    private void openCategoriesActivity() {
        Intent intent = new Intent(this, CategoriesActivity.class);
        startActivity(intent);
    }

    private void deleteTransaction(Transaction transaction) {
        dbHelper.deleteTransaction(transaction.getId());
        refreshTransactions();
        updateBudgetSummary();
    }

    private void refreshTransactions() {
        transactions.clear();
        transactions.addAll(dbHelper.getAllTransactions());
        transactionAdapter.notifyDataSetChanged();
    }

    private void updateBudgetSummary() {
        double totalIncome = dbHelper.getTotalIncome();
        double totalExpense = dbHelper.getTotalExpense();
        double remaining = totalIncome - totalExpense;

        totalIncomeText.setText(String.format("Income: $%.2f", totalIncome));
        totalExpenseText.setText(String.format("Expenses: $%.2f", totalExpense));
        remainingBudgetText.setText(String.format("Remaining: $%.2f", remaining));

        // Color coding for remaining budget
        if (remaining < 0) {
            remainingBudgetText.setTextColor(getColor(android.R.color.holo_red_dark));
        } else {
            remainingBudgetText.setTextColor(getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTransactions();
        updateBudgetSummary();
    }
}