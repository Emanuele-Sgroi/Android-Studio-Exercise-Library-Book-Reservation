package com.example.library_book_reservation;

public class Book {
    private String bookName;
    private String bookImage;
    private String bookAuthors;
    private String bookPublisher;

    public Book() {}

    public Book(String bookName, String bookImage, String bookAuthors, String bookPublisher) {
        this.bookName = bookName;
        this.bookImage = bookImage;
        this.bookAuthors = bookAuthors;
        this.bookPublisher = bookPublisher;
    }

    public String getBookName() {
        return bookName;
    }

    public String getBookImage() {
        return bookImage;
    }

    public String getBookAuthors() {
        return bookAuthors;
    }

    public String getBookPublisher() {
        return bookPublisher;
    }
}
