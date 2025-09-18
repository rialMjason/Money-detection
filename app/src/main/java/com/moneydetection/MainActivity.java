package com.moneydetection;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    // Transaction model
    public static class Transaction {
        public String amount;
        public String merchant;
        public String category;
        public long timestamp;

        public Transaction(String amount, String merchant, String category, long timestamp) {
            this.amount = amount;
            this.merchant = merchant;
            this.category = category;
            this.timestamp = timestamp;
        }
    }

    // In-memory transaction list
    private final java.util.List<Transaction> transactionList = new java.util.ArrayList<>();

    private android.widget.ArrayAdapter<String> adapter;
    private android.widget.ListView transactionListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register broadcast receiver for TNG transactions
        android.content.IntentFilter filter = new android.content.IntentFilter("com.moneydetection.TNG_TRANSACTION");
        registerReceiver(tngReceiver, filter);

        transactionListView = findViewById(R.id.transactionListView);
        adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getTransactionStrings());
        transactionListView.setAdapter(adapter);

        private final android.content.BroadcastReceiver tngReceiver = new android.content.BroadcastReceiver() {
            @Override
            public void onReceive(android.content.Context context, android.content.Intent intent) {
                String amount = intent.getStringExtra("amount");
                String merchant = intent.getStringExtra("merchant");
                showCategoryDialog(amount, merchant);
            }
        };

        @Override
        protected void onDestroy() {
            super.onDestroy();
            unregisterReceiver(tngReceiver);
        }

        // Show a dialog to select transaction category
        public void showCategoryDialog(String amount, String merchant) {
            String[] categories = {"Food", "Transport", "Shopping", "Bills", "Other"};
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Select Transaction Category")
                .setMessage("Amount: RM" + amount + "\nMerchant: " + merchant)
                .setItems(categories, (dialog, which) -> {
                    String selectedCategory = categories[which];
                    // Store transaction with selected category
                    Transaction transaction = new Transaction(amount, merchant, selectedCategory, System.currentTimeMillis());
                    transactionList.add(transaction);
                    updateTransactionList();
                })
                .setCancelable(false)
                .show();
        }

        // Helper to get transaction strings for ListView
        private java.util.List<String> getTransactionStrings() {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
            java.util.List<String> list = new java.util.ArrayList<>();
            for (Transaction t : transactionList) {
                String time = sdf.format(new java.util.Date(t.timestamp));
                list.add("RM" + t.amount + " | " + t.merchant + " | " + t.category + " | " + time);
            }
            return list;
        }

        // Update ListView when new transaction is added
        private void updateTransactionList() {
            adapter.clear();
            adapter.addAll(getTransactionStrings());
            adapter.notifyDataSetChanged();
    }
}
