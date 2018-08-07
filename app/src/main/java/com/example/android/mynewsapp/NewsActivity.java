/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.mynewsapp;


import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Application;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderCallbacks<List<News>> {

    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;

    /** Tag for the log messages */
    public static final String LOG_TAG = NewsActivity.class.getName();

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    /**
     * Adapter for the list of news stories
     */
    private NewsAdapter mAdapter;

    /**
     * URL to query the Guardian dataset for news information.  The setup of this url is to
     * identify "Arts" items, however the API seems to provide pillarName's that are not just "Arts"
     * This URL orders items by newest first and shows all fields in order to use "byline"
     */
    //private static final String GUARDIAN_REQUEST_URL =
      //      "https://content.guardianapis.com/search?show-fields=all&order-by=newest&q=art&api-key=d68b6411-edc7-4fec-908a-ab8516493860";

    /**
     * Modified the API url to make user preferences applicable for 2nd version of this app.
     */
    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search?show-fields=all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);

        //hook up the TextView as the empty view of the ListView. We can use the ListView
        // setEmptyView() method. We can also make the empty state TextView be a global variable,
        // so we can refer to it in a later method.
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of news stories as input
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        //Set the adapter on the (@link listview)
        //so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news story.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // Find the current news story that was clicked on
                News currentNews = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getUrl());

                // Create a new intent to view the news URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)

                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    // Required for  when the LoaderManager has determined that the loader with our specified ID
    // isn't running, so we should create a new one.
    @Override
    // onCreateLoader instantiates and returns a new Loader for the given ID
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);


        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String page = sharedPrefs.getString(
                getString(R.string.settings_page_key),
                getString(R.string.settings_page_default));

       //String orderBy  = sharedPrefs.getString(
        //       getString(R.string.settings_order_by_key),
        //       getString(R.string.settings_order_by_default));

        String orderByTime = sharedPrefs.getString(
                getString(R.string.settings_order_by_time_value),
                getString(R.string.settings_order_by_time_default));

        Log.i(LOG_TAG, "This is the string we built for TIME:" + orderByTime);
        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `order-by=newest`
        uriBuilder.appendQueryParameter("page", page); //pass in the option a user picked for the page
        uriBuilder.appendQueryParameter("q", "art"); //this is what I set it to be
        uriBuilder.appendQueryParameter("order-by", orderByTime);
        uriBuilder.appendQueryParameter("api-key","d68b6411-edc7-4fec-908a-ab8516493860"); //my key


        String testString = uriBuilder.toString();
        //String testString = uriBuilder.toString();
        Log.i(LOG_TAG, "This is the string we built:" + testString);

        // Return the correct URI
        //"https://content.guardianapis.com/search?show-fields=all&order-by=newest&q=art&api-key=d68b6411-edc7-4fec-908a-ab8516493860";
        return new NewsLoader(this, uriBuilder.toString());
    }
    //We need onLoadFinished(), where we'll do exactly what we did in onPostExecute(), and use the
    // news data to update our UI - by updating the dataset in the adapter
    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No news stories found."
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous news data
        mAdapter.clear();

        // If there is a valid list of {@link news}, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        //Commenting out adapter.addAll(news); will show the "No news found"
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    //we're being informed that the data from our loader is no longer valid. This isn't actually a
    // case that's going to come up with our simple loader, but the correct thing to do is to remove
    // all the news data from our UI by clearing out the adapter’s data set.
    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
