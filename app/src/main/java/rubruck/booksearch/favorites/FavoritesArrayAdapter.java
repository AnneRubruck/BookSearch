package rubruck.booksearch.favorites;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import rubruck.booksearch.R;
import rubruck.booksearch.backgroundTasks.DownloadImageTask;
import rubruck.booksearch.utilities.Book;

/**
 * This Array adapter receives an Array of Books
 * and puts in a ListView
 *
 * Created by rubruck on 25/08/15.
 */
public class FavoritesArrayAdapter extends ArrayAdapter<Book>
{

    private Context con;
    private FavoritesManager fav;

    // constructor used to initialise favorites and get the context
    public FavoritesArrayAdapter(Context context, ArrayList<Book> books)
    {
        super(context, R.layout.favorites_row_layout, books);

        fav = FavoritesManager.getFavoritesInstance(context);
        con = context.getApplicationContext();
    }

    /**
     * getView is responsible for creating the rows of the favorites list.
     * will be called for each position to show in the list
     * @param position current index of the favorites array
     * @param convertView reference to the previous view
     * @param parent ViewGroup (invisible container defining the layout of its content)
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        // put a layout into the View
        LayoutInflater inflater = LayoutInflater.from(con.getApplicationContext());

        View view = inflater.inflate(R.layout.favorites_row_layout, parent, false);

        // get the asin at the current position
        final Book book = getItem(position);

        if (book != null)
        {
            // book image to the left
            ImageView imageView = (ImageView) view.findViewById(R.id.smallPreviewImageView);
            String url = book.getMediumImage().toString();
            if (url != null)
                new DownloadImageTask(imageView).execute(url);

            // title in the middle
            TextView textView = (TextView) view.findViewById(R.id.bookTitleView);
            textView.setText(book.getTitle());

            // set the images on the right to invisible
            ImageView deleteImage = (ImageView) view.findViewById(R.id.deleteImageView);
            deleteImage.setClickable(false);
            deleteImage.setVisibility(View.INVISIBLE);
            ImageView detailImage = (ImageView) view.findViewById(R.id.showDetailsImageView);
            detailImage.setClickable(false);
            detailImage.setVisibility(View.INVISIBLE);

            // onClickListener for this row
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showHandleOptions(v, position, book);
                }
            });
        }
        else
        {
            // in case there is no book at the current position, return an invisible row.
            view.setVisibility(View.INVISIBLE);
            view.setClickable(false);
        }

        return view;
    }

    /**
     * this method is called when an item in the favorites list is clicked
     * shows an icon as button to delete the item
     *  @param favoritesRow row at position in the list
     * @param position in the list
     * @param b book of the current list item
     */
    private void showHandleOptions(final View favoritesRow, int position, Book b)
    {
        //final int pos = position;
        final Book book = b;

        ImageView deleteButton = (ImageView) favoritesRow.findViewById(R.id.deleteImageView);


        if (deleteButton.getVisibility() == View.VISIBLE)
            deleteButton.setVisibility(View.INVISIBLE);
        else {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
                    adb.setTitle(con.getString(R.string.remove));
                    adb.setMessage(con.getString(R.string.deleteFromFavCheck) + " " + book.getTitle() + "?");
                    adb.setNegativeButton(con.getString(R.string.cancel), null);
                    adb.setPositiveButton(con.getString(R.string.ok), new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            fav.removeBook(book);
                            remove(book);
                            notifyDataSetInvalidated();
                        }
                    });
                    adb.show();
                }
            });
        }
    }

}
