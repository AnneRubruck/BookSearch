package rubruck.booksearch.utilities;

import android.content.Context;

import java.net.URL;

import rubruck.booksearch.R;

/**
 * This class is used to define Books and their parameters
 * Created by rubruck on 07/09/15.
 */
public class Book
{
    // Amazon Standard Identification Number, used as id
    private String asin = "n/a";

    // Overview
    private String title = "n/a";
    private String[] authors;
    private String publisher = "n/a";
    private String isbn = "n/a";
    private String edition = "n/a";
    private String publicationDate = "n/a";

    // Images
    private URL smallImage;
    private URL mediumImage;
    private URL largeImage;

    // application context
    private Context con = null;

    /*
     * alternate implementation of the toString()-method
     * imports the Context to access the string resources
     */
    public String toString()
    {
        // title
        String result = title + "\n";

        // authors
        if ((authors != null)&&(authors.length == 1))
            result += con.getString(R.string.author) + " " + authors[0] + "\n";
        if ((authors != null)&&(authors.length > 1))
        {
            result += con.getString(R.string.authors) + " ";
            for (String author : authors) result += author + ", ";
            // cut off the last coma
            result = result.substring(0, result.length() - 2);
            result += "\n";
        }

        // publisher
        if (!publisher.equals(""))
            result += con.getString(R.string.publisher) + " " + this.publisher + "\n";

        // edition
        if ((!edition.equals(""))&&(!edition.equals("0")))
            result += con.getString(R.string.edition) + " "  + this.edition + "\n";

        // publication date
        if (!publicationDate.equals(""))
            result += con.getString(R.string.publication_date) + " " + this.publicationDate + "\n";

        // isbn
        if (!isbn.equals(""))
            result += "ISBN: " + this.isbn;

        return result;
    }

        /*
         * constructor
         */

    public Book(Context context, String asin)
    {
        this.con = context.getApplicationContext();
        this.asin = asin;
    }

        /*
         * getter and setter
         */

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getAuthors() {
        return authors;
    }

    public void setAuthors(String[] authors) {
        this.authors = authors;
    }

    public String getPublisher() { return publisher; }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) { this.publicationDate = publicationDate; }

    public URL getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(URL smallImage) {
        this.smallImage = smallImage;
    }

    public URL getMediumImage()
    {
        return mediumImage;
    }

    public void setMediumImage(URL mediumImage) {
        this.mediumImage = mediumImage;
    }

    public URL getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(URL largeImage) {
        this.largeImage = largeImage;
    }

    public String detailsToString()
    {
        String result = "";
        String[] resultArray = this.toString().split("\n");
        if (resultArray.length > 1)
            for (int i = 1; i < resultArray.length; i++)
                result += resultArray[i] + "\n";
        return result;
    }
}
