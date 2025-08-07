package com.example.budgetplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Transaction> transactions;
    private OnTransactionDeleteListener deleteListener;

    public interface OnTransactionDeleteListener {
        void onDelete(Transaction transaction);
    }

    public TransactionAdapter(List<Transaction> transactions, OnTransactionDeleteListener deleteListener) {
        this.transactions = transactions;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.typeText.setText(transaction.getType());
        holder.categoryText.setText(transaction.getCategory());
        holder.amountText.setText(String.format("$%.2f", transaction.getAmount()));
        holder.descriptionText.setText(transaction.getDescription());
        holder.dateText.setText(transaction.getDate());

        // Color coding based on type
        if ("Income".equals(transaction.getType())) {
            holder.amountText.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_green_dark));
        } else {
            holder.amountText.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_red_dark));
        }

        holder.deleteBtn.setOnClickListener(v -> deleteListener.onDelete(transaction));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView typeText, categoryText, amountText, descriptionText, dateText;
        Button deleteBtn;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            typeText = itemView.findViewById(R.id.typeText);
            categoryText = itemView.findViewById(R.id.categoryText);
            amountText = itemView.findViewById(R.id.amountText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            dateText = itemView.findViewById(R.id.dateText);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}