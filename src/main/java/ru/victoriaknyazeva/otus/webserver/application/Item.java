package ru.victoriaknyazeva.otus.webserver.application;

public class Item {
    private int id;
    private String title;
    private int price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Item() {
    }

    public Item(String title, int price) {
        this.title = title;
        this.price = price;
    }
}