package com.juilyoon.booklook;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private final String DEBUG_TAG = "SearchActivity";
    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes";
    // Full list of parsed Books
    private ArrayList<Book> list = new ArrayList<Book>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ListView bookList = (ListView) findViewById(R.id.searchResults_view);
        bookList.setEmptyView(findViewById(R.id.emptyList_view));

        if (savedInstanceState != null && savedInstanceState.containsKey("key")) {
            list = savedInstanceState.getParcelableArrayList("key");
            updateResults(list);
        }

        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBooks();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("key", list);
        super.onSaveInstanceState(outState);
    }

    private void searchBooks(){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Search for query
            EditText searchTextView = (EditText) findViewById(R.id.search_bar);
            String query = searchTextView.getText().toString();
            new BookSearchAsyncTask().execute(query.split(" "));
        }
        else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateResults(ArrayList<Book> bookList) {
        // Handle 0 case
        if (bookList.isEmpty()) {
            Toast.makeText(this, "No results found.", Toast.LENGTH_SHORT).show();
        }
        else {
            ListView searchResultsView = (ListView) findViewById(R.id.searchResults_view);
            searchResultsView.setAdapter(new BookListAdapter(this, bookList));
        }
    }

    private ArrayList<Book> extractBooks(String jsonResponse) {
        ArrayList<Book> books = new ArrayList<Book>();
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray bookList = root.optJSONArray("items");
            // Variables that need to be extracted
            String title;
            ArrayList<String> authors = new ArrayList<>();
            String description;
            String thumbnailUrl;
            String infoUrl;
            // Iterate through bookList and add Book objects to books ArrayList
            for (int i=0; i < bookList.length(); i++) {
                JSONObject book = bookList.optJSONObject(i);
                JSONObject volumeInfo = book.optJSONObject("volumeInfo");
                title = volumeInfo.optString("title");
                JSONArray authorList = volumeInfo.optJSONArray("authors");
                for (int j=0; j < authorList.length(); j++) {
                    authors.add(authorList.optString(j));
                }
                description = book.optJSONObject("searchInfo").optString("textSnippet");
                thumbnailUrl = volumeInfo.optJSONObject("imageLinks").optString("smallThumbnail");
                infoUrl = volumeInfo.optString("infoLink");
                // Add Book object to books
                books.add(new Book(title, authors.toArray(new String[0]), description, thumbnailUrl, infoUrl));
            }
        } catch (JSONException e) {
            Log.e(DEBUG_TAG, "Problem parsing JSON results.", e);
        } finally {
            return books;
        }
    }

    private class BookSearchAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... query) {
            // Create URL object for search query
            URL url = createUrl(query);
            // Perform an HttpRequest to Google Books api and return JSON response
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            }
            catch (IOException e) {
                Log.e(DEBUG_TAG, "Problem making the HTTP request.", e);
            }
            return jsonResponse;
        }

        /**
         * Update the screen with search results
         */
        @Override
        protected void onPostExecute(String books){
            if (books == null) {
                return;
            }
            list = extractBooks(books);
            updateResults(list);
        }

        /**
         * Returns a new URL object from given String URL
         */
        private URL createUrl(String[] query) {
            URL url = null;
            try {
                // Generate single string for all query words
                if (query.length > 0) {
                    StringBuilder queryString = new StringBuilder();
                    queryString.append(query[0]);
                    for (int i = 1; i < query.length; i++) {
                        // Add a space and next word of query
                        queryString.append("+" + query[i]);
                    }
                    url = new URL(API_URL + "?q=" + queryString.toString());
                }
            } catch (MalformedURLException e) {
                Log.e(DEBUG_TAG, "Error generating URL.", e);
                return null;
            }
            return url;
        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
        // the web page content as a InputStream, which it returns as
        // a string.
        private String makeHttpRequest(URL url) throws IOException {
            // Handle empty url
            if (url == null) { return null; }
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Test response code
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(DEBUG_TAG, "Connection error " + responseCode);
                }
            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Trouble retrieving JSON results.", e);
            } finally {
                // Make sure urlConnection and inputStream are closed
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Reads an InputStream and converts to a string
         *
         * @param stream
         * @return String: converted input
         * @throws IOException
         * @throws UnsupportedEncodingException
         */
        public String readFromStream(InputStream stream) throws IOException, UnsupportedEncodingException {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = stream.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            return output.toString("UTF-8");
        }
    }
}
