package com.example.budgetplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private TextView totalIncomeText, totalExpenseText, remainingBudgetText;
    private RecyclerView transactionsRecyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactions;
    private List<Transaction> allTransactions; // Keep original list for search
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        initializeViews();
        setupRecyclerView();
        setupButtonListeners();
        setupSearchView(); // NEW METHOD
        updateBudgetSummary();
    }

    private void initializeViews() {
        totalIncomeText = findViewById(R.id.totalIncomeText);
        totalExpenseText = findViewById(R.id.totalExpenseText);
        remainingBudgetText = findViewById(R.id.remainingBudgetText);
        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView);
        searchView = findViewById(R.id.searchView); // NEW LINE

        Button addIncomeBtn = findViewById(R.id.addIncomeBtn);
        Button addExpenseBtn = findViewById(R.id.addExpenseBtn);
        Button viewCategoriesBtn = findViewById(R.id.viewCategoriesBtn);

        addIncomeBtn.setOnClickListener(v -> openAddTransaction("Income"));
        addExpenseBtn.setOnClickListener(v -> openAddTransaction("Expense"));
        viewCategoriesBtn.setOnClickListener(v -> openCategoriesActivity());
    }

    private void setupRecyclerView() {
        allTransactions = dbHelper.getAllTransactions(); // CHANGED: Store all transactions
        transactions = new ArrayList<>(allTransactions); // CHANGED: Create copy for filtering
        transactionAdapter = new TransactionAdapter(transactions, this::deleteTransaction);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionsRecyclerView.setAdapter(transactionAdapter);
    }

    private void setupButtonListeners() {
        // Already handled in initializeViews()
    }

    // NEW METHOD: Setup search functionality
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterTransactions(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTransactions(newText);
                return false;
            }
        });
    }

    // NEW METHOD: Filter transactions based on search query
    private void filterTransactions(String query) {
        transactions.clear();

        if (query.isEmpty()) {
            // If search is empty, show all transactions
            transactions.addAll(allTransactions);
        } else {
            // Filter transactions containing the search query
            String lowerCaseQuery = query.toLowerCase();
            for (Transaction transaction : allTransactions) {
                if (transaction.getDescription().toLowerCase().contains(lowerCaseQuery) ||
                        transaction.getCategory().toLowerCase().contains(lowerCaseQuery) ||
                        String.valueOf(transaction.getAmount()).contains(query)) {
                    transactions.add(transaction);
                }
            }
        }
        transactionAdapter.notifyDataSetChanged();
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
        allTransactions.clear();
        allTransactions.addAll(dbHelper.getAllTransactions()); // CHANGED: Refresh all transactions

        // Reapply current search filter
        String currentQuery = searchView.getQuery().toString();
        filterTransactions(currentQuery);
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