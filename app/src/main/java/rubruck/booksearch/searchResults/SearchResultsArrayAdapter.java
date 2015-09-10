package rubruck.booksearch.searchResults;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import rubruck.booksearch.R;
import rubruck.booksearch.backgroundTasks.DownloadImageTask;
import rubruck.booksearch.favorites.FavoritesManager;
import rubruck.booksearch.utilities.Book;

/**
 * This Array adapter receives an Array of Books
 * and puts in a ListView
 *
 * Created by rubruck on 25/08/15.
 */
public class SearchResultsArrayAdapter extends ArrayAdapter<Book>
{
    private Context con;

    public SearchResultsArrayAdapter(Context context, Book[] books)
    {
        super(context, R.layout.search_results_row_layout, books);
        con = context;
    }

    /**
     * getView is responsible for creating the rows of the search results list.
     * will be called for each position to show in the list
     * @param position current index the books array
     * @param convertView reference to the previous view
     * @param parent ViewGroup (invisible container defining the layout of its content)
     * @return the readily prepared current row
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // put a layout into the View
        LayoutInflater inflater = LayoutInflater.from(con.getApplicationContext());

        View view = inflater.inflate(R.layout.search_results_row_layout, parent, false);

        // get the book at the current position
        final Book book = getItem(position);

        if (book != null)
        {
            // book image to the left
            ImageView imageView = (ImageView) view.findViewById(R.id.smallPreviewImageView);
            String url = book.getSmallImage().toString();
            if (url != null)
                new DownloadImageTask(imageView).execute(url);

            // title in the middle
            TextView textView = (TextView) view.findViewById(R.id.bookTitleView);
            textView.setText(book.getTitle());

            // favorite star on the right
            ImageView favorite = (ImageView) view.findViewById(R.id.favoritesStar);
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleFavorites(book, v);
                }
            });
            // item currently a favorite?
            FavoritesManager fav = FavoritesManager.getFavoritesInstance(getContext());
            if (fav.isFavorite(book.getAsin()))
                favorite.setImageResource(R.drawable.ic_star_black_48dp);
            else
                favorite.setImageResource(R.drawable.ic_star_border_black_48dp);
        }
        else
            // in case there is no book at the current position, return an invisible row.
            view.setVisibility(View.INVISIBLE);

        return view;
    }

    private void toggleFavorites(Book book, View v)
    {
        ImageView image = (ImageView) v;

        FavoritesManager fav = FavoritesManager.getFavoritesInstance(con);

        // if it was a favorite before, it is no longer
        if (fav.isFavorite(book.getAsin()))
        {
            // delete it from the favorites
            fav.removeBook(book);

            // change the image indicating whether item is a favorite or not
            image.setImageResource(R.drawable.ic_star_border_black_48dp);
        }
        // if it was no favorite before, it is now
        else
        {
            // add item to the favorites
            fav.addBook(book);

            // change the image indicating whether item is a favorite or not
            image.setImageResource(R.drawable.ic_star_black_48dp);
        }
    }


}
