package ru.yandex.mobilization.models;

import java.util.Date;

/**
 * Created by Mike on 22.04.2017.
 */

public class FavoritesItem {

    private String sourceText;
    private int id;
    private String text;
    private String from;
    private String to;
    private Date date;
    private int historyId;

    public FavoritesItem(int id, String text, String sourceText, String from, String to, long date, int historyId) {
        this.id = id;
        this.text = text;
        this.sourceText = sourceText;
        this.from = from;
        this.to = to;
        this.date = new Date(date);
        this.historyId = historyId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Date getDate() {
        return date;
    }

    public long getLongDate() {
        return date.getTime();
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDate(long date) {
        this.date = new Date(date);
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }
}
