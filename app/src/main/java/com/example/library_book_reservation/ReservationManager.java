package com.example.library_book_reservation;

import java.util.ArrayList;
import java.util.List;

public class ReservationManager {
    private static ReservationManager instance;
    private List<Reservation> reservations;

    private ReservationManager() {
        reservations = new ArrayList<>();
    }

    public static ReservationManager getInstance() {
        if (instance == null) {
            instance = new ReservationManager();
        }
        return instance;
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }


    public boolean isBookReserved(String bookName) {
        for (Reservation r : reservations) {
            if (r.getBook().getBookName().equals(bookName)) {
                return true;
            }
        }
        return false;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public boolean hasReservations() {
        return !reservations.isEmpty();
    }
}
