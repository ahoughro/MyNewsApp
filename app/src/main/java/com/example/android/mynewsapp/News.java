package com.example.android.mynewsapp;

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
public class News {

/**
 * {@link News} represents a news story item including newsTitle, section, author, and date of story.
 *
 */
        /** News Story Title*/
        private String mNewsTitle;

        /** News Section*/
        private String mSection;

        /** Date of News Story */
        private String mAuthor;

        /** Date for News Story */
        private String mDate;

        /** URL for News Story */
        private String mUrl;

        /**
         * Create a new News object.
         *
         * @param newsTitle is the title of the story
         * @param section is the section of the news story
         * @param author is the name of the person who wrote the story
         * @param date is the date the story was published
         * @param url is the web address for the Guardian News Story information
         */
        public News(String newsTitle, String section, String author, String date, String url) {
            mNewsTitle = newsTitle;
            mSection = section;
            mAuthor = author;
            mDate = date;
            mUrl = url;
        }

        /**
         * Get the news title of the news item.
         */
        public String getNewsTitle() {
            return mNewsTitle;
        }

        /**
         * Get the section for this news item.
         */
        public String getSection() {
            return mSection;
        }

        /**
         * Get the author for this news item.
         */
        public String getAuthor() {
            return mAuthor;
        }

        /**
         * Return the date of the news
         */
        public String getNewsDate() {
            return mDate;
        }

        /**
         * Returns the website URL to find more information about the news story.
         */
        public String getUrl() {
            return mUrl;
        }
    }

