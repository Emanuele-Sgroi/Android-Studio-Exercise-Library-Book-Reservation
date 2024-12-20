package com.example.library_book_reservation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerBooks;
    private Button buttonCheckout;
    private BookFirestoreAdapter adapter;

    boolean[] reservedStates; // track reserved state for all items

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerBooks = findViewById(R.id.recyclerBooks);
        buttonCheckout = findViewById(R.id.buttonCheckout);

        // Set up Firestore query
        Query query = FirebaseFirestore.getInstance().collection("books");

        // Configure FirestoreRecyclerOptions
        FirestoreRecyclerOptions<Book> options =
                new FirestoreRecyclerOptions.Builder<Book>()
                        .setQuery(query, Book.class)
                        .build();

        adapter = new BookFirestoreAdapter(options);

        recyclerBooks.setLayoutManager(new LinearLayoutManager(this));
        recyclerBooks.setAdapter(adapter);

        buttonCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });
    }

    public void showDateRangeDialog(Book book, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_date_range, null);

        DatePicker startDatePicker = view.findViewById(R.id.startDatePicker);
        DatePicker endDatePicker = view.findViewById(R.id.endDatePicker);
        Button confirmButton = view.findViewById(R.id.buttonConfirm);

        long now = System.currentTimeMillis();
        startDatePicker.setMinDate(now);
        endDatePicker.setMinDate(now);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        confirmButton.setOnClickListener(v -> {
            Calendar startCal = Calendar.getInstance();
            startCal.set(Calendar.YEAR, startDatePicker.getYear());
            startCal.set(Calendar.MONTH, startDatePicker.getMonth());
            startCal.set(Calendar.DAY_OF_MONTH, startDatePicker.getDayOfMonth());

            Calendar endCal = Calendar.getInstance();
            endCal.set(Calendar.YEAR, endDatePicker.getYear());
            endCal.set(Calendar.MONTH, endDatePicker.getMonth());
            endCal.set(Calendar.DAY_OF_MONTH, endDatePicker.getDayOfMonth());

            if (endCal.before(startCal)) {
                return;
            }

            long startMillis = startCal.getTimeInMillis();
            long endMillis = endCal.getTimeInMillis();

            showReservationConfirmationDialog(dialog, book, startMillis, endMillis, position);
        });
    }

    private void showReservationConfirmationDialog(AlertDialog parentDialog, Book book, long startMillis, long endMillis, int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_reservation_confirmation, null);

        TextView textDetails = view.findViewById(R.id.textReservationDetails);
        Button stayButton = view.findViewById(R.id.buttonStay);
        Button checkoutButton = view.findViewById(R.id.buttonGoCheckout);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(startMillis);
        String startStr = (c.get(Calendar.MONTH)+1) + "/" + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.YEAR);

        c.setTimeInMillis(endMillis);
        String endStr = (c.get(Calendar.MONTH)+1) + "/" + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.YEAR);

        String details = book.getBookName() + "\nFrom " + startStr + " to " + endStr;
        textDetails.setText(details);

        parentDialog.setContentView(view);

        stayButton.setOnClickListener(v -> {
            ReservationManager.getInstance().addReservation(new Reservation(book, startMillis, endMillis));
            BookFirestoreAdapter currentAdapter = (BookFirestoreAdapter) recyclerBooks.getAdapter();
            if (currentAdapter != null) {
                currentAdapter.setReserved(position, true);
            }
            parentDialog.dismiss();
        });

        checkoutButton.setOnClickListener(v -> {
            ReservationManager.getInstance().addReservation(new Reservation(book, startMillis, endMillis));
            BookFirestoreAdapter currentAdapter = (BookFirestoreAdapter) recyclerBooks.getAdapter();
            if (currentAdapter != null) {
                currentAdapter.setReserved(position, true);
            }
            parentDialog.dismiss();
            Intent intent = new Intent(MainActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView bookName, bookAuthors, bookPublisher;
        Button reserveButton;

        public BookViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.imageBook);
            bookName = itemView.findViewById(R.id.textBookName);
            bookAuthors = itemView.findViewById(R.id.textBookAuthors);
            bookPublisher = itemView.findViewById(R.id.textBookPublisher);
            reserveButton = itemView.findViewById(R.id.buttonReserve);
        }
    }
}
