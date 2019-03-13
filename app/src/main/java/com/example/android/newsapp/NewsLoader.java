package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    // Tag for log messages
    private static final String LOG_TAG = NewsLoader.class.getSimpleName();

    // Query URL
    private String mUrl;

    /**
     * Constructs a new NewsLoader
     * @param context of the activity
     * @param url to load data from
     */
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<News> loadInBackground() {
        // Cancel loading if url is null
        if (mUrl == null) {
            return null;
        }

        // Make the request, parse the JSON response and extract a list of articles
        List<News> newsList = QueryUtils.fetchNewsData(mUrl);
        return  newsList;
    }
}