package com.juilyoon.booklook;

import android.content.Context;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.test.suitebuilder.TestMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by juil on 16-08-01.
 */
public class BookListAdapter extends ArrayAdapter<Book> {
    public BookListAdapter(Context context, ArrayList<Book> bookList) {
        super(context, 0, bookList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if existing list item is available, otherwise inflate
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_item, parent, false);
        }

        final Book currentBook = getItem(position);

        // Populate title, author, and description
        TextView titleView = (TextView) listItemView.findViewById(R.id.title_view);
        titleView.setText(currentBook.getTitle());
        TextView authorView = (TextView) listItemView.findViewById(R.id.author_view);
        authorView.setText(currentBook.getAuthors().toString());
        TextView descriptionView = (TextView) listItemView.findViewById(R.id.description_view);
        descriptionView.setText(currentBook.getDescription());
        // TODO: Populate thumbnail from url
        ImageView thumbnailView = (ImageView) listItemView.findViewById(R.id.thumbnail_view);
        thumbnailView.setImageResource(R.drawable.thumbnail);

        return listItemView;
    }

    private class ThumbnailAsyncTask extends AsyncTask<URL, Void, Book> {
        @Override
        protected Book doInBackground(URL... url) {
            return null;
        }
    }
}
