package rubruck.booksearch.searchResults;

import rubruck.booksearch.utilities.Book;

/**
 * Singleton class to manage the search results books list
 * Created by rubruck on 07/09/15.
 */
public class SearchResultsManager
{
    // singleton instance
    private static SearchResultsManager booksInstance;

    // Array of 10 search result pages
    // with each page containing 10 books
    private Book[][] books = new Book[10][10];

    // number of search results
    private int numberOfSearchResults = 0;

    // number of search result pages
    // a maximum of 10 pages is retrieved
    // value is smaller that 10, if their are less than 91 search results
    private int numberOfSearchResultPages = 0;

    // the search query needs to be stored,
    // in order to be able to perform more ItemSearch request
    // to retrieve the other search result pages for the same query
    private String searchQuery;

    // it is likely, that books contains less than numberOfSearchResults entries
    // because amazon delivers search results page-wise
    // with 10 result per page, and 10 pages at the most

    // private constructor, to avoid creating more than one instance of books
    private SearchResultsManager() {}

    // static method to allow exactly one creation of an instance of books
    public static SearchResultsManager getBooksInstance ()
    {
        if (SearchResultsManager.booksInstance == null)
        {
            SearchResultsManager.booksInstance = new SearchResultsManager();
        }
        return SearchResultsManager.booksInstance;
    }

    /**
     * this method is used to reset all information
     * will be called, when a new search is ItemLookupDone
     */
    public void reset()
    {
        books = new Book[10][10];
        numberOfSearchResults = 0;
        numberOfSearchResultPages = 0;
    }

    public void addBook(Book b, int page)
    {
        for (int i = 0; i < 10; i++)
            if (books[page][i] == null)
            {
                books[page][i] = b;
                return;
            }

    }

    /**
     * if the page asked for, contains at least one book, return the whole page array
     * otherwise return null
     * @param page
     * @return
     */
    public Book[] getBooksOfPage(int page)
    {
        boolean empty = true;
        for (int i = 0; i < 10; i++)
            if (books[page][i] != null)
                empty = false;

        if (empty)
            return null;
        else
        {
            Book[] result = new Book[10];
            System.arraycopy(books[page], 0, result, 0, 10);
            return result;
        }
    }

    public int getNumberOfSearchResults()
    {
        return numberOfSearchResults;
    }

    public void setNumberOfSearchResults(int numberOfSearchResults)
    {
        this.numberOfSearchResults = numberOfSearchResults;
    }

    public int getNumberOfSearchResultPages() {
        return numberOfSearchResultPages;
    }

    public void setNumberOfSearchResultPages(int numberOfSearchResultPages) {
        this.numberOfSearchResultPages = numberOfSearchResultPages;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getSearchQuery() {
        return searchQuery;
    }
}
