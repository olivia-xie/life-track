package com.example.newdiary.Models;

public class Entry {

    private String text;
    private String date;
    private String title;
    private int entryId;

    public Entry() {
    }

    public Entry(String text, String date, String title) {
        this.text = text;
        this.date = date;
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }
}
