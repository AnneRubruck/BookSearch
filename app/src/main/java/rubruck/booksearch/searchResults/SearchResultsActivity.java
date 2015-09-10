package rubruck.booksearch.searchResults;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import rubruck.booksearch.R;
import rubruck.booksearch.backgroundTasks.ItemSearch;
import rubruck.booksearch.backgroundTasks.TaskCallback;
import rubruck.booksearch.utilities.BaseMenuActivity;

/**
 * This class is used to display the search results.
 *
 * When in portrait mode, only the search results, without details are shown.
 * When in landscape mode, details are shown on the same screen as the titles list.
 */
public class SearchResultsActivity extends BaseMenuActivity implements TaskCallback
{
    // the search results page currently shown
    private int currentPage = 0;

    // the search results manager
    SearchResultsManager res = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // data saved in savedInstanceBundle will be kept if android force shuts the application
        if ((savedInstanceState != null)&&(savedInstanceState.getInt("page",-1) != -1))
        {
            currentPage = savedInstanceState.getInt("page");
        }

        // page of search results to show, given by the home screen activity
        res = SearchResultsManager.getBooksInstance();
        currentPage = getIntent().getExtras().getInt("page");


        setContentView(R.layout.search_results_layout);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getResources().getString(R.string.bs_search_results));

        // TextView informing about the pages
        TextView pagesTextView = (TextView) findViewById(R.id.pagesTextView);
        pagesTextView.setText((currentPage + 1) + " / " + (res.getNumberOfSearchResultPages()));

        // set the buttons correctly (filled or not? clickable or not?)
        ImageView lastPageButton = (ImageView) findViewById(R.id.lastPageImageView);
        if (currentPage > 0)
        {
            lastPageButton.setImageResource(R.drawable.ic_left_black_18dp);
            lastPageButton.setClickable(true);
        }
        else
        {
            lastPageButton.setImageResource(R.drawable.ic_left_outline_black_18dp);
            lastPageButton.setClickable(false);
        }

        ImageView nextPageButton = (ImageView) findViewById(R.id.nextPageImageView);
        if (currentPage < res.getNumberOfSearchResultPages() - 1)
        {
            nextPageButton.setImageResource(R.drawable.ic_right_black_18dp);
            nextPageButton.setClickable(true);
        }
        else
        {
            nextPageButton.setImageResource(R.drawable.ic_right_outline_black_18dp);
            nextPageButton.setClickable(false);
        }
    }

    /**
     * this is called, when the app is shut down by android or orientation is changed
     * this does not save the data, when the user shuts down the app (use method onStop)
     * @param outState the data relevant to keep when application is paused, or orientation changes
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putInt("page",currentPage);
        super.onSaveInstanceState(outState);
    }

    /**
     * This method is called to show the last / previous search results page
     * e.g. show page 3 if current page is 4
     * @param view the imageView clicked on
     */
    public void onLastPageClick(View view)
    {
        if (res == null)
            res = SearchResultsManager.getBooksInstance();

        // next page
        if (currentPage > 0)
            currentPage --;

        // check if that page has been downloaded already previously
        // if not prepare the next page (perform an ItemSearch)
        if (res.getBooksOfPage(currentPage) == null)
        {
            // perform an ItemSearch for the next page
            // start an asynchronous task, to keep the rest of the app responding
            // this will attempt to access the amazon api to do an ItemSearch
            // and retrieve first page of search results (0 in array, 1 in amazon request)
            ItemSearch is = new ItemSearch(getApplicationContext(), this);
            // after it is done, it will return to done()
            is.execute(res.getSearchQuery(), currentPage + "");
        }
        // if the page is already available
        // directly go to done()
        else done();
    }

    /**
     * This method is called to show the nex search result page
     * e.g. show page 5 if current page is 4
     * @param view the imageView clicked on
     */
    public void onNextPageClick(View view)
    {
        if (res == null)
            res = SearchResultsManager.getBooksInstance();

        // next page
        if (currentPage < res.getNumberOfSearchResultPages())
            currentPage ++;

        // check if that page has been downloaded already previously
        // if not prepare the next page (perform an ItemSearch)
        if (res.getBooksOfPage(currentPage) == null)
        {
            // perform an ItemSearch for the next page
            // start an asynchronous task, to keep the rest of the app responding
            // this will attempt to access the amazon api to do an ItemSearch
            // and retrieve first page of search results (0 in array, 1 in amazon request)
            ItemSearch is = new ItemSearch(getApplicationContext(), this);
            // after it is done, it will return to done()
            is.execute(res.getSearchQuery(), currentPage + "");
        }
        // if the page is already available
        // directly go to done()
        else done();
    }

    @Override
    public void done()
    {
        // start the new SearchResultsActivity (with the new page)
        // tell it which search result page to show
        // after searching always show the first page (0 in array, 1 in amazon request)
        Intent intent = new Intent();
        intent.setClass(this, SearchResultsActivity.class);
        intent.putExtra("page", currentPage);
        startActivity(intent);
        // to replace the current activity:
        this.finish();
    }

    public int getCurrentPage()
    {
        return currentPage;
    }
}
