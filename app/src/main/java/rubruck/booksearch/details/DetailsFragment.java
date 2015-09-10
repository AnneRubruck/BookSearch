package rubruck.booksearch.details;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import rubruck.booksearch.R;
import rubruck.booksearch.backgroundTasks.DownloadImageTask;
import rubruck.booksearch.searchResults.SearchResultsManager;
import rubruck.booksearch.utilities.Book;

/**
 * The fragment showing the currently chosen books details
 *
 * Created by rubruck on 25/08/15.
 */
public class DetailsFragment extends Fragment
{
    // Create a DetailsFragment that contains the books details for the correct index
    public static DetailsFragment newInstance(int page, int index)
    {
        DetailsFragment f = new DetailsFragment();

        // Bundles are used to pass data using a key "index" and a value
        Bundle args = new Bundle();
        // the search results page currently shown
        args.putInt("page", page);
        args.putInt("index", index);

        // Assign key-value pair(s) to the fragment
        f.setArguments(args);

        return f;
    }

    /**
     * this method returns the currently shown index
     * @return the currently shown index
     */
    public int getShownIndex() { return getArguments().getInt("index", 0); }
    public int getShownPage() { return getArguments().getInt("page", 0); }

    /**
     * The system calls this when it's time for the fragment to draw its user interface for the first time.
     * It returns the view that will be displayed in the details fragment, which
     * contains an image of the book and the details below it.
     * @param inflater LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return a View that is the root of the Details Fragment Layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        SearchResultsManager books = SearchResultsManager.getBooksInstance();
        Book book = books.getBooksOfPage(getShownPage())[getShownIndex()];

        // use a layout that displays an image and the details text inside a scrollView underneath
        View view = inflater.inflate(R.layout.details_fragment_layout, container, false);

        // display an image of the book, if available
        display(view);

        // display details of the book
        TextView detailsText = (TextView) view.findViewById(R.id.detailsTextViewText);
        TextView detailsTitle = (TextView) view.findViewById(R.id.detailsTextViewTitle);
        detailsTitle.setText(book.getTitle());
        detailsText.setText(book.detailsToString());

        //return scroller;
        return view;
    }

    public void display(View parentView)
    {
        // get the image View
        ImageView imageView = (ImageView) parentView.findViewById(R.id.bookImageView);

        // get the books image url
        SearchResultsManager books = SearchResultsManager.getBooksInstance();
        Book currentBook = books.getBooksOfPage(getShownPage())[getShownIndex()];
        // landscape mode -> medium image
        // portrait mode -> large image
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            String url = currentBook.getMediumImage().toString();
            if (url != null)
                new DownloadImageTask(imageView).execute(url);
            else
            {
                // if medium image is not available try the small image
                url = currentBook.getSmallImage().toString();
                if (url != null)
                    new DownloadImageTask(imageView).execute(url);
            }
        }
        else
        {
            String url = currentBook.getLargeImage().toString();
            if (url != null)
                new DownloadImageTask(imageView).execute(url);
            else
            {
                // if large image is not available try the medium image
                url = currentBook.getMediumImage().toString();
                if (url != null)
                    new DownloadImageTask(imageView).execute(url);
            }
        }

    }
}
