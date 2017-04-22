package ru.yandex.mobilization.models;

import java.util.Date;

public class TranslationItem {
    private String sourceText;
    private String text;
    private Language from;
    private Language to;
    private Date date;

    public TranslationItem(String sourceText, String text, Language from, Language to, Date date) {
        this.sourceText = sourceText;
        this.text = text;
        this.from = from;
        this.to = to;
        this.date = date;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Language getFrom() {
        return from;
    }

    public void setFrom(Language from) {
        this.from = from;
    }

    public Language getTo() {
        return to;
    }

    public void setTo(Language to) {
        this.to = to;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
