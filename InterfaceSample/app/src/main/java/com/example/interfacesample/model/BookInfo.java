package com.example.interfacesample.model;

/**
 * Created by HenryHo on 2016/9/11.
 */
public class BookInfo {

    private String title;
    private String price;
    private String date;

    public BookInfo(String title, String price, String date) {
        this.title = title;
        this.price = price;
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

}
