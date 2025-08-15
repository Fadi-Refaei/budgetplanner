package com.example.budgetplanner;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private TextView titleText;
    private Spinner categorySpinner;
    private EditText amountEdit, descriptionEdit;
    private Button saveBtn;
    private String transactionType;
    private boolean isEditMode = false;
    private int transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        dbHelper = new DatabaseHelper(this);
        transactionType = getIntent().getStringExtra("TRANSACTION_TYPE");
        isEditMode = getIntent().getBooleanExtra("EDIT_MODE", false);

        if (isEditMode) {
            transactionId = getIntent().getIntExtra("TRANSACTION_ID", -1);
        }

        initializeViews();
        setupCategorySpinner();

        if (isEditMode) {
            loadTransactionData();
        }

        setupSaveButton();
    }

    private void initializeViews() {
        titleText = findViewById(R.id.titleText);
        categorySpinner = findViewById(R.id.categorySpinner);
        amountEdit = findViewById(R.id.amountEdit);
        descriptionEdit = findViewById(R.id.descriptionEdit);
        saveBtn = findViewById(R.id.saveBtn);

        if (isEditMode) {
            titleText.setText("Edit " + transactionType);
            saveBtn.setText("Update Transaction");
        } else {
            titleText.setText("Add " + transactionType);
        }
    }

    private void setupCategorySpinner() {
        List<String> categories = dbHelper.getCategoriesByType(transactionType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void loadTransactionData() {
        String category = getIntent().getStringExtra("TRANSACTION_CATEGORY");
        double amount = getIntent().getDoubleExtra("TRANSACTION_AMOUNT", 0);
        String description = getIntent().getStringExtra("TRANSACTION_DESCRIPTION");

        amountEdit.setText(String.valueOf(amount));
        descriptionEdit.setText(description);

        List<String> categories = dbHelper.getCategoriesByType(transactionType);
        int categoryPosition = categories.indexOf(category);
        if (categoryPosition >= 0) {
            categorySpinner.setSelection(categoryPosition);
        }
    }

    private void setupSaveButton() {
        saveBtn.setOnClickListener(v -> {
            if (isEditMode) {
                updateTransaction();
            } else {
                saveTransaction();
            }
        });
    }

    private void saveTransaction() {
        String category = categorySpinner.getSelectedItem().toString();
        String amountStr = amountEdit.getText().toString().trim();
        String description = descriptionEdit.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());

        Transaction transaction = new Transaction(transactionType, category, amount, description, currentDate);
        long result = dbHelper.addTransaction(transaction);

        if (result != -1) {
            Toast.makeText(this, transactionType + " added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error adding " + transactionType.toLowerCase(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTransaction() {
        String category = categorySpinner.getSelectedItem().toString();
        String amountStr = amountEdit.getText().toString().trim();
        String description = descriptionEdit.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());

        Transaction transaction = new Transaction(transactionType, category, amount, description, currentDate);
        transaction.setId(transactionId);

        long result = dbHelper.updateTransaction(transaction);

        if (result > 0) {
            Toast.makeText(this, transactionType + " updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error updating " + transactionType.toLowerCase(), Toast.LENGTH_SHORT).show();
        }
    }
}