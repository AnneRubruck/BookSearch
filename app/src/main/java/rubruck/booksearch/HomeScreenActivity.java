package rubruck.booksearch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import rubruck.booksearch.backgroundTasks.ItemSearch;
import rubruck.booksearch.backgroundTasks.TaskCallback;
import rubruck.booksearch.searchResults.SearchResultsActivity;
import rubruck.booksearch.searchResults.SearchResultsManager;
import rubruck.booksearch.utilities.BaseMenuActivity;

/**
 * Home Screen lets the suer initiate a search
 * TaskCallback is implemented, to give the AsyncTask doing the search, a way to call back to the Home Screen Activity
 */
public class HomeScreenActivity extends BaseMenuActivity implements TaskCallback
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getResources().getString(R.string.bs_home));
    }

    public void onSearchClick(View view)
    {
        EditText inputEditText = (EditText) findViewById(R.id.inputEditText);

        String input = inputEditText.getText().toString().trim();

        // input not empty?
        if (input.length() > 0)
        {
            Toast.makeText(this, R.string.serching, Toast.LENGTH_SHORT).show();
            // delete the last search if existent
            // search results always stored in singleton object books
            SearchResultsManager books = SearchResultsManager.getBooksInstance();
            books.reset();

            // start an asynchronous task, to keep the rest of the app responding
            // this will attempt to access the amazon api to do an ItemSearch
            // and retrieve first page of search results
            books.setSearchQuery(input);
            ItemSearch is = new ItemSearch(this.getApplicationContext(), this);
            is.execute(input, "0");
        }
        else
            Toast.makeText(this, R.string.please_type_in, Toast.LENGTH_LONG).show();
    }

    /**
     * this method is called when the ItemSearch is ItemLookupDone
     * it leads to the Search Result Activity
     */
    @Override
    public void done()
    {
        // start the SearchResultsActivity
        // tell it which search result page to show
        // after searching always show the first page (0 in array, 1 in amazon request)
        Intent intent = new Intent();
        intent.setClass(this, SearchResultsActivity.class);
        intent.putExtra("page", 0);
        startActivity(intent);
    }
}
