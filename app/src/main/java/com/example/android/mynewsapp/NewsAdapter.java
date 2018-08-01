package com.example.android.mynewsapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
        * {@link NewsAdapter} is an {@link ArrayAdapter} that can provide the layout for each list item
        * based on a data source, which is a list of {@link News} objects.
        */

public class NewsAdapter extends ArrayAdapter<News> {

    //Variable defined for use in string manipulation for the extraction of the date from the time.
    private static final String LOCATION_SEPARATOR = "T";


    /** Tag for the log messages */
    public static final String LOG_TAG = NewsAdapter.class.getName();

    /**
     * Create a new {@link NewsAdapter} object.
     *
     * @param context is the current context (i.e. Activity) that the adapter is being created in.
     * @param news    is the list of {@link News} to be displayed.
     */
    public NewsAdapter(Context context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        //Find the news at the given position in the list of news stories
        News currentNews = getItem(position);

        Log.i(LOG_TAG, "In NewsAdapter at: " + position);


        //Find the textview in the news_list_item layout with the ID newsTitle
        TextView newsTitleTextView = (TextView) listItemView.findViewById(R.id.newsTitle);
        newsTitleTextView.setText(currentNews.getNewsTitle());
       // Log.i(LOG_TAG, "MyClass.getView() — get item number " + position);

        //Create 2 location strings from the location string and check if there is an offset.

        String originalDate = currentNews.getNewsDate();
        String webPublicationDate;
        //String webPublicationTime;


        if (originalDate.contains(LOCATION_SEPARATOR)) {
            String[] parts = originalDate.split(LOCATION_SEPARATOR);
            webPublicationDate = parts[0] + LOCATION_SEPARATOR;
          //  webPublicationTime = parts[1];
        } else {
            webPublicationDate = getContext().getString(R.string.no_date);
           // webPublicationTime = originalDate;
        }

        Log.i(LOG_TAG, "In NewsAdapter at: " + webPublicationDate);

        //Display the webPublicationDate only in the TextView
        TextView primaryDateView = (TextView) listItemView.findViewById(R.id.date);
        primaryDateView.setText(webPublicationDate);

        //Find the textview in the news_list_item layout with the ID newsSection
        TextView newsSectionTextView = (TextView) listItemView.findViewById(R.id.newsSection);
        newsSectionTextView.setText(currentNews.getSection());

        //Find the textview in the news_list_item layout with the ID author
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author);
        authorTextView.setText(currentNews.getAuthor());

        return listItemView;
    }
}
