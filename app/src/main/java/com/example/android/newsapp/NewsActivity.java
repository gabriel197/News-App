package com.example.android.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    //Store the url in a static final String because it won't change and will be used in local methods
    private static final String QUERY_URL = "https://content.guardianapis.com/search";

    //Create a reference to our custom adapter
    private NewsAdapter mAdapter;

    //Create a reference to a empty view
    private TextView mEmptyTextView;

    //Retry button to avoid killing the app and start again
    //when we gain network access
    private Button retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);


        //Find a reference to the list view
        ListView listView = findViewById(R.id.list);

        mEmptyTextView = findViewById(R.id.empty_view_text);
        listView.setEmptyView(mEmptyTextView);

        //Create a new adapter that takes in an empty list of News as input
        mAdapter = new NewsAdapter(NewsActivity.this, new ArrayList<News>());
        listView.setAdapter(mAdapter);

        //Set on click listener to open a specific webpage for each element of the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Find the current News article that was clicked on
                News currentArticle = mAdapter.getItem(i);

                //Convert the string URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentArticle.getWebsiteUrl());

                //Create an implicit intent that to view News article URI
                Intent webIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                //Make the intent launch a new activity
                startActivity(webIntent);

            }
        });

        //Get a reference to ConnectivityManager to check the state of network connectivity
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get details of the current active data network
        NetworkInfo info = manager.getActiveNetworkInfo();

        //If there is a network connection, fetch data
        if (info != null && info.isConnected()) {

            //Get a reference to LoaderMAnager in order to interact with loaders
            LoaderManager loaderManager = getSupportLoaderManager();

            // Start the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(0, null, this);

        } else {
            //Hide the spinner and show the error message
            View loadingSpinner = findViewById(R.id.loading_indicator);
            loadingSpinner.setVisibility(View.GONE);

            //Make the button restart the activity
            retry = findViewById(R.id.retry_button);
            retry.setVisibility(View.VISIBLE);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    startActivity(getIntent());
                }
            });

            mEmptyTextView.setText(R.string.no_internet);
        }
    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int i, @Nullable Bundle bundle) {
        String apiKey = "456e9b55-0abb-4def-9915-6b318cbc73a1";

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the value for this preference.
        String minPageSize = sharedPrefs.getString(
                getString(R.string.settings_page_size_key),
                getString(R.string.settings_page_size_default));

        String section = sharedPrefs.getString(
                getString(R.string.settings_section_key),
                getString(R.string.settings_section_default));

        //Parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(QUERY_URL);

        //buildUpon prepares the baseUri so we can add query parameters
        Uri.Builder uriBuilder = baseUri.buildUpon();

        //Append query parameter and it's value
        uriBuilder.appendQueryParameter("section", section);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        uriBuilder.appendQueryParameter("page-size", minPageSize);
        uriBuilder.appendQueryParameter("api-key", apiKey);

        //Create a new loader from the given url
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> newsList) {
        //Hide loading indicator because data loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        //Hide the button when data is loaded
        retry = findViewById(R.id.retry_button);
        retry.setVisibility(View.GONE);

        // If newsList is empty or null, emptyTextView will be displayed
        mEmptyTextView.setText(R.string.no_articles);

        if (newsList != null && !newsList.isEmpty()) {
            mAdapter.addAll(newsList);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        //Clear the data  when loader is reset
        mAdapter.clear();
    }

    @Override
    //This method initialize the option menu
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu we created in xml
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    //Method called whenever an option from Option Menu is selected
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_setting) {
            Intent actionSettings = new Intent(this, SettingsActivity.class);
            startActivity(actionSettings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}