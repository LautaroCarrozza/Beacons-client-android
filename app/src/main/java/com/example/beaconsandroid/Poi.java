package com.example.beaconsandroid;

public class Poi {
    private String title;
    private String htmlContent;

    public Poi(String title, String htmlContent) {
        this.title = title;
        this.htmlContent = htmlContent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }
}
