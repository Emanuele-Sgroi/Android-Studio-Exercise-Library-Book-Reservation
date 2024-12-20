package com.example.library_book_reservation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class BookFirestoreAdapter extends FirestoreRecyclerAdapter<Book, MainActivity.BookViewHolder> {
    private boolean[] reservedStates;

    public BookFirestoreAdapter(@NonNull FirestoreRecyclerOptions<Book> options) {
        super(options);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        reservedStates = new boolean[getItemCount()];

        for (int i = 0; i < getItemCount(); i++) {
            Book book = getItem(i);
            if (ReservationManager.getInstance().isBookReserved(book.getBookName())) {
                reservedStates[i] = true;
            }
        }
    }

    @NonNull
    @Override
    public MainActivity.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new MainActivity.BookViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull MainActivity.BookViewHolder holder, int position, @NonNull Book model) {
        if (reservedStates == null) {
            reservedStates = new boolean[getItemCount()];
        }

        holder.bookName.setText(model.getBookName());
        holder.bookAuthors.setText("By: " + model.getBookAuthors());
        holder.bookPublisher.setText("Publisher: " + model.getBookPublisher());

        Glide.with(holder.itemView.getContext())
                .load(model.getBookImage())
                .into(holder.bookImage);

        boolean isReserved = reservedStates[position];
        if (isReserved) {
            holder.reserveButton.setText("Remove Book");
            holder.reserveButton.setBackgroundResource(R.drawable.button_remove);
        } else {
            holder.reserveButton.setText("Reserve Book");
            holder.reserveButton.setBackgroundResource(R.drawable.button_reserve);
        }

        holder.reserveButton.setOnClickListener(v -> {
            if (!isReserved) {
                ((MainActivity) holder.itemView.getContext()).showDateRangeDialog(model, position);
            } else {
                setReserved(position, false);
            }
        });
    }

    public void setReserved(int position, boolean reserved) {
        if (reservedStates == null && getItemCount() > 0) {
            reservedStates = new boolean[getItemCount()];
        }
        reservedStates[position] = reserved;
        notifyItemChanged(position);
    }
}
