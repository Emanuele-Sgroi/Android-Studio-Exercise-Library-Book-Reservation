package com.example.library_book_reservation;

import java.util.Date;

public class Reservation {
    private Book book;
    private long startDate;
    private long endDate;

    public Reservation(Book book, long startDate, long endDate) {
        this.book = book;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Book getBook() {
        return book;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }
}
