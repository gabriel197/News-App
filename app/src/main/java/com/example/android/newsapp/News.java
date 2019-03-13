package com.example.android.newsapp;

public class News {

    //Name of the category
    private String mCategoryName;

    //Title of the article
    private String mTitle;

    //Author of the article
    private String mAuthor;

    //Url of the specific article
    private String mWebsiteUrl;

    //Date when the article was published
    private String mDateAndTime;


    //Create an object of type News to store information about each article
    public News(String categoryName, String title, String author, String websiteUrl, String dateAndTime) {
        mCategoryName = categoryName;
        mTitle = title;
        mAuthor = author;
        mWebsiteUrl = websiteUrl;
        mDateAndTime = dateAndTime;
    }

    //Returns the category name
    public String getCategoryName() {
        return mCategoryName;
    }

    //Returns title of article
    public String getTitle() {
        return mTitle;
    }

    //Returns the author of the article
    public String getAuthor() {
        return mAuthor;
    }

    //Returns url of the article
    public String getWebsiteUrl() {
        return mWebsiteUrl;
    }

    //Return date and time
    public String getDateAndTime() {
        return mDateAndTime;
    }
}