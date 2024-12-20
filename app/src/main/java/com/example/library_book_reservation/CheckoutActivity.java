package com.example.library_book_reservation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button buttonGoBack, buttonCloseApp;
    private TextView textNoReservations;

    private ReservationAdapter reservationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        recyclerView = findViewById(R.id.recyclerCheckout);
        textNoReservations = findViewById(R.id.textNoReservations);
        buttonGoBack = findViewById(R.id.buttonGoBack);
        buttonCloseApp = findViewById(R.id.buttonCloseApp);

        buttonGoBack.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        buttonCloseApp.setOnClickListener(v -> {
            finishAffinity(); // closes the app
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        List<Reservation> reservations = ReservationManager.getInstance().getReservations();

        if (reservations.isEmpty()) {
            textNoReservations.setVisibility(android.view.View.VISIBLE);
            recyclerView.setVisibility(android.view.View.GONE);
        } else {
            textNoReservations.setVisibility(android.view.View.GONE);
            recyclerView.setVisibility(android.view.View.VISIBLE);

            reservationAdapter = new ReservationAdapter(reservations, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(reservationAdapter);
        }
    }

    public static class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

        private List<Reservation> reservations;
        private CheckoutActivity activity;

        public ReservationAdapter(List<Reservation> reservations, CheckoutActivity activity) {
            this.reservations = reservations;
            this.activity = activity;
        }

        @NonNull
        @Override
        public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_reservation, parent, false);
            return new ReservationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
            Reservation r = reservations.get(position);

            holder.bookName.setText(r.getBook().getBookName());
            holder.bookAuthors.setText("By: " + r.getBook().getBookAuthors());
            holder.bookPublisher.setText("Publisher: " + r.getBook().getBookPublisher());

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(r.getStartDate());
            String startStr = (c.get(Calendar.MONTH)+1) + "/" + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.YEAR);
            c.setTimeInMillis(r.getEndDate());
            String endStr = (c.get(Calendar.MONTH)+1) + "/" + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.YEAR);

            holder.dateRange.setText("From: " + startStr + " To: " + endStr);

        }

        @Override
        public int getItemCount() {
            return reservations.size();
        }

        public static class ReservationViewHolder extends RecyclerView.ViewHolder {
            TextView bookName, bookAuthors, bookPublisher, dateRange;
            public ReservationViewHolder(@NonNull View itemView) {
                super(itemView);
                bookName = itemView.findViewById(R.id.textBookName);
                bookAuthors = itemView.findViewById(R.id.textBookAuthors);
                bookPublisher = itemView.findViewById(R.id.textBookPublisher);
                dateRange = itemView.findViewById(R.id.textDateRange);
            }
        }
    }

}
