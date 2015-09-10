package rubruck.booksearch.favorites;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import rubruck.booksearch.R;
import rubruck.booksearch.utilities.Book;

/**
 * Singleton class to manage the favoritesList
 * Created by rubruck on 07/09/15.
 */
public class FavoritesManager
{
    // singleton object
    private static FavoritesManager favoritesManager = null;

    // favoritesList list database
    private SQLiteDatabase favoritesDB = null;

    // application Context
    private Context appContext = null;

    /**
     * private constructor
     * avoids creating more than one instance of the favorites manager
     * @param context
     */
    private FavoritesManager(Context context)
    {
        appContext = context.getApplicationContext();
        openOrCreateDB();
    }

    // static method to allow exactly one creation of an instance of the favorites manager
    public static FavoritesManager getFavoritesInstance (Context context)
    {
        if (favoritesManager == null)
            favoritesManager = new FavoritesManager(context);
        return favoritesManager;
    }

    public void addBook(Book book)
    {
        // add book to favorites database
        addBookToDB(book);
    }

    public void removeBook(Book book)
    {
        // delete book from favorites database
        deleteFromDB(book);
    }

    public boolean isFavorite(String asin)
    {
        // return whether the db contains a row with the given asin as primary key
        return dbContains(asin);
    }

    /**
     * reads all favorite books stored in the data base
     * and returns them as an ArrayList of Book
     * @return
     */
    public ArrayList<Book> getFavorites()
    {
        ArrayList<Book> favorites = new ArrayList<Book>();

        if (favoritesDB != null)
        {
            // retrieve all rows
            Cursor cursor = favoritesDB.rawQuery("SELECT * FROM favorites", null);

            int asinColumn = cursor.getColumnIndex("asin");
            int titleColumn = cursor.getColumnIndex("title");
            int authorsColumn = cursor.getColumnIndex("authors");
            int publisherColumn = cursor.getColumnIndex("publisher");
            int isbnColumn = cursor.getColumnIndex("isbn");
            int editionColumn = cursor.getColumnIndex("edition");
            int publicationDateColumn = cursor.getColumnIndex("publicationDate");
            int smallImageColumn = cursor.getColumnIndex("smallImage");
            int mediumImageColumn = cursor.getColumnIndex("mediumImage");
            int largeImageColumn = cursor.getColumnIndex("largeImage");

            // start with the first row retrieved
            // extract every rows data into a book and
            // return the list of all these books
            cursor.moveToFirst();
            if (cursor.getCount() > 0)
                do
                {
                    Book currentBook = new Book(appContext, cursor.getString(asinColumn));
                    currentBook.setTitle(cursor.getString(titleColumn));
                    currentBook.setAuthors(cursor.getString(authorsColumn).split(","));
                    currentBook.setPublisher(cursor.getString(publisherColumn));
                    currentBook.setIsbn(cursor.getString(isbnColumn));
                    currentBook.setEdition(cursor.getString(editionColumn));
                    currentBook.setPublicationDate(cursor.getString(publicationDateColumn));
                    String smallImage = cursor.getString(smallImageColumn);
                    if (!smallImage.equals("n/a"))
                        try {
                            currentBook.setSmallImage(new URL(smallImage));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    String mediumImage = cursor.getString(mediumImageColumn);
                    if (!mediumImage.equals("n/a"))
                        try {
                            currentBook.setMediumImage(new URL(mediumImage));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    String largeImage = cursor.getString(largeImageColumn);
                    if (!largeImage.equals("n/a"))
                        try {
                            currentBook.setLargeImage(new URL(largeImage));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    favorites.add(currentBook);
                }
                while (cursor.moveToNext());
            cursor.close();
        }
        return favorites;
    }

    private void addBookToDB(Book book)
    {
        // initialize favoritesList DB
        if (favoritesDB == null)
            openOrCreateDB();

        // build add command
        String insertCommand = "INSERT INTO favorites (asin, title, authors, publisher, "
                + "isbn, edition, publicationDate, smallImage, mediumImage, largeImage)"
                + " VALUES ('"
                + book.getAsin().replace("'", "") + "','"
                + book.getTitle().replace("'","") + "','";
        // authors as coma-separated string
        if (book.getAuthors().length > 0)
        {
            for (int i = 0; i < book.getAuthors().length; i++)
                insertCommand += book.getAuthors()[i].replace("'","") + ",";
            insertCommand = insertCommand.substring(0,insertCommand.length()-1);
            insertCommand += "','";
        }
        insertCommand += book.getPublisher().replace("'", "") + "','"
                + book.getIsbn().replace("'", "") + "','"
                + book.getEdition().replace("'", "") + "','"
                + book.getPublicationDate().replace("'", "") + "','";
        if(book.getSmallImage() != null)
            insertCommand += book.getSmallImage().toString().replace("'", "") + "','";
        else
            insertCommand += "n/a','";
        if(book.getMediumImage() != null)
            insertCommand += book.getMediumImage().toString().replace("'", "") + "','";
        else
            insertCommand += "n/a','";
        if(book.getLargeImage() != null)
            insertCommand += book.getLargeImage().toString().replace("'","") + "');";
        else
            insertCommand += "n/a');";

        // finally add the current book
        favoritesDB.execSQL(insertCommand);
    }

    private void deleteFromDB(Book book)
    {
        favoritesDB.execSQL("DELETE FROM favorites WHERE asin = '" + book.getAsin() + "';");
    }

    private void openOrCreateDB()
    {
        try
        {
            favoritesDB = appContext.openOrCreateDatabase("FavoritesDB",Context.MODE_PRIVATE,null);
            favoritesDB.execSQL("CREATE TABLE IF NOT EXISTS favorites"
                    + "(asin VARCHAR primary key, title VARCHAR, authors VARCHAR, publisher VARCHAR,"
                    + " isbn VARCHAR, edition VARCHAR, publicationDate VARCHAR, smallImage VARCHAR,"
                    + " mediumImage VARCHAR, largeImage VARCHAR);");
            // verify, that the DB has been written to file system
            File db = appContext.getDatabasePath("FavoritesDB");
            if (!db.exists())
                Toast.makeText(appContext, R.string.database_missing, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.e("SResultsArrayAdapter", "Error while opening or creating DB");
        }
    }

    /**
     * This method returns, whether the book with the given asin
     * is in the favorites list or not.
     * @param asin the books id to check for
     * @return true, if the book is in the favorites, false otherwise
     */
    private boolean dbContains(String asin)
    {
        // read all favorite books from the db
        FavoritesManager fav = getFavoritesInstance(appContext);
        ArrayList<Book> books = fav.getFavorites();
        // go through the list
        for (int i = 0; i < books.size(); i++)
            if (books.get(i).getAsin().equals(asin))
                // return true, as soon as the asin in question is found
                return true;
        // or false, if it is not in the list
        return false;

    }
}
