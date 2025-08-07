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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        dbHelper = new DatabaseHelper(this);
        transactionType = getIntent().getStringExtra("TRANSACTION_TYPE");

        initializeViews();
        setupCategorySpinner();
        setupSaveButton();
    }

    private void initializeViews() {
        titleText = findViewById(R.id.titleText);
        categorySpinner = findViewById(R.id.categorySpinner);
        amountEdit = findViewById(R.id.amountEdit);
        descriptionEdit = findViewById(R.id.descriptionEdit);
        saveBtn = findViewById(R.id.saveBtn);

        titleText.setText("Add " + transactionType);
    }

    private void setupCategorySpinner() {
        List<String> categories = dbHelper.getCategoriesByType(transactionType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void setupSaveButton() {
        saveBtn.setOnClickListener(v -> saveTransaction());
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

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        Transaction transaction = new Transaction(transactionType, category, amount, description, currentDate);
        long result = dbHelper.addTransaction(transaction);

        if (result != -1) {
            Toast.makeText(this, transactionType + " added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error adding " + transactionType.toLowerCase(), Toast.LENGTH_SHORT).show();
        }
    }
}