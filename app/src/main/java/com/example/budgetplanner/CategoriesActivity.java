package com.example.budgetplanner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private TextView incomeCategoriesText, expenseCategoriesText;
    private EditText newCategoryEdit;
    private RadioGroup categoryTypeGroup;
    private Button addCategoryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        dbHelper = new DatabaseHelper(this);

        initializeViews();
        displayCategories();
        setupAddCategoryButton();
    }

    private void initializeViews() {
        incomeCategoriesText = findViewById(R.id.incomeCategoriesText);
        expenseCategoriesText = findViewById(R.id.expenseCategoriesText);
        newCategoryEdit = findViewById(R.id.newCategoryEdit);
        categoryTypeGroup = findViewById(R.id.categoryTypeGroup);
        addCategoryBtn = findViewById(R.id.addCategoryBtn);
    }

    private void displayCategories() {
        List<String> incomeCategories = dbHelper.getCategoriesByType("Income");
        List<String> expenseCategories = dbHelper.getCategoriesByType("Expense");

        StringBuilder incomeText = new StringBuilder("Income Categories:\n");
        for (String category : incomeCategories) {
            incomeText.append("• ").append(category).append("\n");
        }

        StringBuilder expenseText = new StringBuilder("Expense Categories:\n");
        for (String category : expenseCategories) {
            expenseText.append("• ").append(category).append("\n");
        }

        incomeCategoriesText.setText(incomeText.toString());
        expenseCategoriesText.setText(expenseText.toString());
    }

    private void setupAddCategoryButton() {
        addCategoryBtn.setOnClickListener(v -> {
            String categoryName = newCategoryEdit.getText().toString().trim();

            if (categoryName.isEmpty()) {
                Toast.makeText(this, "Please enter category name", Toast.LENGTH_SHORT).show();
                return;
            }

            String categoryType = "Income";
            if (categoryTypeGroup.getCheckedRadioButtonId() == R.id.expenseRadio) {
                categoryType = "Expense";
            }

            dbHelper.addCategory(categoryName, categoryType);
            Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();

            newCategoryEdit.setText("");
            displayCategories();
        });
    }
}