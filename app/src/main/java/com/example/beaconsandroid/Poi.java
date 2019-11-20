package com.example.beaconsandroid;

public class Poi {
    private String title;
    private String htmlContent;

    /**
     * Constructor creates an instance of {@link Poi}
     * @param title title of the point of interest to create
     * @param htmlContent poi body content in html format
     */
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

    /**
     * Compares whether some other poi is "equal to" this one
     * @param obj the reference object with wich to compare
     * @return {@code true} if this object is the same as the obj to compare
     */
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Poi){
            return ((Poi) obj).getTitle().equals(this.title);
        }
        return false;
    }
}
