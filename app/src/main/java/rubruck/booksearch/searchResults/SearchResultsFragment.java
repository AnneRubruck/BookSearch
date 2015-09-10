package rubruck.booksearch.searchResults;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import rubruck.booksearch.R;
import rubruck.booksearch.details.DetailsActivity;
import rubruck.booksearch.details.DetailsFragment;

/**
 * List fragment showing the search results in a ListView
 *
 * When a ListView item (= book title) is selected
 * the DetailsFragment is shown in the Framelayout if we are in horizontal mode,
 * or a new DetailsActivity is created and switch to it if in portrait mode.
 *
 * Created by rubruck on 25/08/15.
 */
public class SearchResultsFragment extends ListFragment
{
    // True <-> landscape mode, False <-> portrait mode
    private boolean dualPane;
    // currently selected item in the ListView
    private int currentlyCheckedItem = 0;
    // currently shown search result page (ranges between 0 and 9)
    private int page = 0;

    /**
     * This method is called, when switching to the Search Results Screen.
     * The bundle consists of key-value-pairs and is used for data exchange
     * between the HomeScreenActivity Activity and this SearchResultScreen Activity.
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        SearchResultsActivity callingActivity = (SearchResultsActivity) getActivity();
        page = callingActivity.getCurrentPage();

        // get the books (=search results)
        SearchResultsManager books = SearchResultsManager.getBooksInstance();

        // get the books in the ListView
        // when activity is created show the current search results page (default value 0)
        SearchResultsArrayAdapter connectArrayToListView = new SearchResultsArrayAdapter(getActivity(), books.getBooksOfPage(page));
        setListAdapter(connectArrayToListView);

        View titlesFrame = getActivity().findViewById(R.id.details);

        // get the mode (check whether the details frame is visible or not)
        // true, if both panels need to be shown
        dualPane = ( getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

        // store the last recently selected book
        if (savedInstanceState != null)
        {
            // 0 is the default value
            currentlyCheckedItem = savedInstanceState.getInt("curChoice", 0);
        }

        if (dualPane)
        {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // show the details on the second fragment in landscape mode
            showDetails(page, currentlyCheckedItem);
        }
    }

    // this method is called anytime, the screen orientation changes
    // or android has to force quit an Activity
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        // save which book title is currently chosen
        outState.putInt("curChoice",currentlyCheckedItem);
        outState.putInt("curPage", page);
    }

    /**
     * is called when user clicks on an item in the search results list
     * if in landscape mode, the details fragment is shown to the right of the search results fragment
     * if in portrait mode, the details are shown in a new activity (Details Activity)
     * @param l listView containing the list item clicked
     * @param v
     * @param position index of the book int the list
     * @param id
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        showDetails(page, position);
    }

    /**
     * prepares the details for the book at index bookIndex and puts it on the screen
     * if in landscape mode, the details fragment is shown to the right of the search results fragment
     * if in portrait mode, the details are shown in a new activity (Details Activity)
     * @param bookIndex index of the book int the list
     */
    private void showDetails(int page, int bookIndex)
    {
        currentlyCheckedItem = bookIndex;

        if (dualPane)
        {
            // landscape mode: show titles list and details at the same time

            // highlight the currently selected book title
            getListView().setItemChecked(bookIndex, true);
            // get the object, that is the Fragment, where the details should be shown in
            DetailsFragment details = (DetailsFragment) getFragmentManager().findFragmentById(R.id.details);

            // if the layoutFragment does not exist yet, or has an old bookIndex, fix this
            if (details == null || details.getShownIndex() != bookIndex)
            {
                details = DetailsFragment.newInstance(page, bookIndex);

                // do a fragment transition to show the new data
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                // replaces R.id.details with the new details
                ft.replace(R.id.details, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        }
        else
        {
            // portrait mode: change to new activity showing the selected books details only
            Intent intent = new Intent();
            intent.setClass(getActivity(),DetailsActivity.class);
            intent.putExtra("index",bookIndex);
            intent.putExtra("page", page);
            startActivity(intent);
        }
    }
}
