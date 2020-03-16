package com.example.newdiary.Models;

import java.io.Serializable;

public class Entry implements Serializable {

    private String text;
    private long date;
    private String title;
    private long entryId;

    public Entry() {
    }

    public Entry(String text, long date, String title) {
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getEntryId() {
        return entryId;
    }

    public void setEntryId(long entryId) {
        this.entryId = entryId;
    }
}
