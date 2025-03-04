package com.example.demo;


public class CreateBookInput {

    private String title;

    private String author;

    // Constructors, getters, and setters
    public CreateBookInput() {}

    public CreateBookInput(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
