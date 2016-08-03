package com.juilyoon.booklook;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.test.suitebuilder.TestMethod;
import android.util.Log;
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
        authorView.setText(generateAuthorString(currentBook.getAuthors()));
        TextView descriptionView = (TextView) listItemView.findViewById(R.id.description_view);
        descriptionView.setText(currentBook.getDescription());
        // TODO: Populate thumbnail from url
        ImageView thumbnailView = (ImageView) listItemView.findViewById(R.id.thumbnail_view);
        ThumbnailAsyncTask task = new ThumbnailAsyncTask();
        task.setThumbnailView(thumbnailView);
        try {
            task.execute(new URL(currentBook.getThumbnailUrl()));
        } catch (MalformedURLException e) {
            Log.e("BookListAdapter", "Bad thumbnail URL.", e);
        }

        return listItemView;
    }

    private String generateAuthorString(String[] authors) {
        String output = "";
        if (authors.length == 0) {
        }
        else {
            output += authors[0];
            for (int i=1; i < authors.length; i++) {
                output += " & " + authors[i];
            }
        }
        return output;
    }

    private class ThumbnailAsyncTask extends AsyncTask<URL, Void, Drawable> {
        ImageView thumbnailView;
        // Get imageView to update
        public void setThumbnailView(ImageView thumbnailView) {
            this.thumbnailView = thumbnailView;
        }
        @Override
        protected Drawable doInBackground(URL... urls) {
            try {
                Log.v("BookListAdapter", "Get thumbnail image from " + urls[0]);
                InputStream content = (InputStream) urls[0].getContent();
                return Drawable.createFromStream(content, "src");
            } catch (IOException e) {
                Log.e("BookListAdapter", "Could not retrieve thumbnail image.", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable thumbnailImage) {
            thumbnailView.setImageDrawable(thumbnailImage);
        }
    }
}
