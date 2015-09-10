package rubruck.booksearch.backgroundTasks;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import rubruck.booksearch.searchResults.SearchResultsManager;
import rubruck.booksearch.utilities.Book;

/**
 * class used to parse XML received from amazon to Books
 * Created by rubruck on 24/08/15.
 */
public class XML2Book
{
    // calling Activities context, used to make toasts
    private Context con;

    public XML2Book(Context con)
    {
        // get callers activity context
        this.con = con;
    }

    /**
     * this method parses the given string in xml format
     * and extracts a list of Books from it
     * @param xmlResult the result retrieved from amazon as a string in xml format
     * @return returns 1 for success and at least 1 search result,
     *         0 for successful amazon request but no search results or
     *         -1, if something went wrong
     */
    public int parseForBooks(String xmlResult, int page)
    {
        // singleton object the books (=search results) are stored in
        SearchResultsManager books = SearchResultsManager.getBooksInstance();

        ArrayList<String> currentAuthors = new ArrayList<String>();

        // initialise the first book
        Book currentBook = new Book(con, "");

        try
        {
            // generate an XML-Parser
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(xmlResult));
            parser.nextTag();

            // while the current event is not end of file, keep reading
            // possible event types: start-of-file, start-tag, text, end-tag, end-of-file
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
//              first, check if request is valid
//              <Request>
//                  <IsValid>True</IsValid>
//                  [...]
//              </Request>
                if ((eventType == XmlPullParser.START_TAG) && (parser.getName().equals("Request")))
                {
                    eventType = parser.next();
                    if ((eventType == XmlPullParser.START_TAG) && (parser.getName().equals("IsValid")))
                    {
                        eventType = parser.next();
                        if ((eventType == XmlPullParser.TEXT) && (parser.getText().equals("False")))
                        {
                            // negative value indicates, that an error occurred, and no results could be extracted
                            return -1;
                        }
                    }
                }

//              check how many results have been retrieved
//              <TotalResults>78</TotalResults>
                if ((eventType == XmlPullParser.START_TAG) && (parser.getName().equals("TotalResults")))
                {
                    eventType = parser.next();
                    if (eventType == XmlPullParser.TEXT)
                    {
                        int numberOfResults = Integer.parseInt(parser.getText());
                        if (numberOfResults == 0)
                        {
                            return numberOfResults;
                        }
                        else
                            books.setNumberOfSearchResults(numberOfResults);
                    }
                }
                // get the number of search result pages
                if ((eventType == XmlPullParser.START_TAG) && (parser.getName().equals("TotalPages")))
                {
                    eventType = parser.next();
                    if (eventType == XmlPullParser.TEXT)
                    {
                        int numberOfResultPages = Integer.parseInt(parser.getText());
                        books.setNumberOfSearchResultPages(numberOfResultPages);
                    }
                }
                // get the current page
                if ((eventType == XmlPullParser.START_TAG) && (parser.getName().equals("ItemPage")))
                {
                    eventType = parser.next();
                    if (eventType == XmlPullParser.TEXT)
                        // subtract 1 to get from search result page to array index
                        page = Integer.parseInt(parser.getText()) - 1;
                }

                // retrieve the ASIN
                // <ASIN>4789014401</ASIN>
                if ((eventType == XmlPullParser.START_TAG) && (parser.getName().equals("ASIN")))
                {
                    eventType = parser.next();
                    if (eventType == XmlPullParser.TEXT)
                        currentBook.setAsin(parser.getText());
                }

                // retrieve the authors
                // <Author>Eri Banno</Author>
                // These authors will be added to the current book
                // as soon as the end tag of the current book has been read.
                // Then the List will be re-initialised to start the next books list
                if ((eventType == XmlPullParser.START_TAG) && (parser.getName().equals("Author")))
                {
                    eventType = parser.next();
                    if (eventType == XmlPullParser.TEXT)
                        currentAuthors.add(parser.getText());
                }

                // get the publisher
                if ((eventType == XmlPullParser.START_TAG) && (parser.getName().equals("Publisher")))
                {
                    eventType = parser.next();
                    if (eventType == XmlPullParser.TEXT)
                        currentBook.setPublisher(parser.getText());
                }

                // get the title
                if ((eventType == XmlPullParser.START_TAG) && (parser.getName().equals("Title")))
                {
                    eventType = parser.next();
                    if (eventType == XmlPullParser.TEXT)
                        currentBook.setTitle(parser.getText());
                }

                //get the isbn
                if ((eventType == XmlPullParser.START_TAG) && (parser.getName().equals("ISBN")))
                {
                    eventType = parser.next();
                    if (eventType == XmlPullParser.TEXT)
                        currentBook.setIsbn(parser.getText());
                }

                // get the edition number
                if ((eventType == XmlPullParser.START_TAG) && (parser.getName().equals("Edition")))
                {
                    eventType = parser.next();
                    if (eventType == XmlPullParser.TEXT)
                        currentBook.setEdition(parser.getText());
                }

                // get the publication date
                if ((eventType == XmlPullParser.START_TAG) && (parser.getName().equals("PublicationDate")))
                {
                    eventType = parser.next();
                    if (eventType == XmlPullParser.TEXT)
                       currentBook.setPublicationDate(parser.getText());
                }

                // get url to small image
                if ((eventType == XmlPullParser.START_TAG) && (parser.getName().equals("SmallImage")))
                {
                    eventType = parser.next();
                    eventType = parser.next();
                    eventType = parser.next();

                    if (eventType == XmlPullParser.TEXT)
                    {
                        URL url = null;
                        // check if string is valid url
                        try
                        {
                            url = new URL(parser.getText());
                        }
                        catch (MalformedURLException e)
                        {
                            break;
                        }
                        if (url != null)
                            currentBook.setSmallImage(url);
                    }
                }

                // get url to medium image
                if ((eventType == XmlPullParser.START_TAG) && (parser.getName().equals("MediumImage")))
                {
                    eventType = parser.next();
                    eventType = parser.next();
                    eventType = parser.next();

                    if (eventType == XmlPullParser.TEXT)
                    {
                        URL url = null;
                        try
                        {
                            url = new URL(parser.getText());
                        }
                        catch (MalformedURLException e)
                        {
                            break;
                        }
                        if (url != null)
                            currentBook.setMediumImage(url);
                    }
                }

                // get url to large image
                if ((eventType == XmlPullParser.START_TAG) && (parser.getName().equals("LargeImage")))
                {
                    eventType = parser.next();
                    eventType = parser.next();
                    eventType = parser.next();

                    if (eventType == XmlPullParser.TEXT)
                    {
                        URL url = null;
                        try
                        {
                            url = new URL(parser.getText());
                        }
                        catch (MalformedURLException e)
                        {
                            break;
                        }
                        if (url != null)
                            currentBook.setLargeImage(url);
                    }
                }

                // end of item is read
                // store current book and
                // get ready for the next book
                if ((eventType == XmlPullParser.END_TAG) && (parser.getName().equals("Item")))
                {
                    // add the authors to the book
                    // and reset the authors list for the next book to extract
                    currentBook.setAuthors(currentAuthors.toArray(new String[currentAuthors.size()]));
                    currentAuthors = new ArrayList<String>();

                    // add the current book to the correct page
                    books.addBook(currentBook,page);
                    // reset the current book
                    currentBook = new Book(con,"");
                }

                // after the current event has been handled, go on to the next
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            System.err.println("Error while creating XML Parser.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error while reading xml file.");
            e.printStackTrace();
        }

        // return 1 because data could be extracted successful and
        // at least one search result has been retrieved
        return 1;
    }
}

