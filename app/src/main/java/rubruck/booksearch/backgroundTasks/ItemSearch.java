package rubruck.booksearch.backgroundTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import rubruck.booksearch.R;

/**
 * ItemSearch is an asynchronous task to perform an ItemSearch on the amazon API
 * and put the result on the screen
 * Created by rubruck on 25/08/15.
 */
public class ItemSearch extends AsyncTask<String, Void, Integer>
{
    // context used to make Toasts, etc.
    private Context con;
    // calling Activity to callback, after search is ItemLookupDone
    private TaskCallback callback;

    // XML-Parser to parseForBooks the result retrieved from amazon
    private XML2Book parser;

    // xml String retrieved from amazon API ItemSearch
    private String xmlResult = "";
    private int page = 1;

    /**
     * constructor
     * @param con The calling activities context The activity that called this Task, is used to communicate back
     * @param callback The calling Activity to callback, after search is ItemLookupDone
     */
    public ItemSearch(Context con, TaskCallback callback)
    {
        this.con = con;
        this.callback = callback;
        parser = new XML2Book(con);
    }

    /**
     * amazon API ItemSearch-Request for args[0]
     * retrieves result page number args[1]
     * @param args
     * @return error code
     */
    @Override
    protected Integer doInBackground(String... args)
    {
        // search terms
        String input = args[0];

        // result page to deliver
        // retrieve the first page, if args[1] is not given or not a valid int
        if (args.length == 2)
            page = Integer.parseInt(args[1]) + 1;

            /* example GET request:
            http://webservices.amazon.com/onca/xml?
            Service=AWSECommerceService&
            AWSAccessKeyId=[AccessKey]&
            Operation=ItemSearch&
            Keywords=Harry Potter&
            SearchIndex=Books&
            Timestamp=[YYYY-MM-DDThh:mm:ssZ]&
            Signature=[Request Signature] */

        // TODO: Please enter the amazon api rootkeys here! Otherwise app will not work correctly!
        String SECRET_KEY = "...";
        String AWS_KEY = "...";
        String ASSOCIATE_TAG = "th0426-20";

        // create a signed request to Amazon API
        SignedRequestsHelper helper;
        try
        {
            helper = SignedRequestsHelper.getInstance("ecs.amazonaws.com", AWS_KEY, SECRET_KEY);

        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return -1;
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return -1;
        } catch (InvalidKeyException e)
        {
            e.printStackTrace();
            return -1;
        }

        // request parameters as key-value-pairs
        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("Version", "2009-03-31");
        params.put("Operation", "ItemSearch");
        params.put("SearchIndex", "Books");
        params.put("ItemPage", "" + page);
        params.put("Keywords", input);
        params.put("MerchantId", "Amazon");
        params.put("ResponseGroup", "Images,ItemAttributes");
        params.put("AssociateTag", ASSOCIATE_TAG);

        // create url
        String url = helper.sign(params);

        // send request and get response
        try
        {
            Document response = getResponse(url);
            xmlResult = printResponse(response);
            Log.d("ItemsSearch",xmlResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    /**
     * This method is called after the result of the request is retrieved from amazon.
     * It prints the result on the screen
     * @param errorMsg
     */
    @Override
    protected void onPostExecute(Integer errorMsg)
    {
        if (errorMsg != 0)
        {
            Toast.makeText(con, R.string.error_while_connecting_to_amazon, Toast.LENGTH_LONG).show();
        }
        else
        {
            // interpret xmlResult and store books data
            int result = parser.parseForBooks(xmlResult, page);
            // if an error occurred, tell the user and stay in home screen
            if (result == -1)
                Toast.makeText(con, R.string.error_while_reading_answer_from_amazon, Toast.LENGTH_SHORT).show();
            // if no search results have been retrieved, tell the user and stay in home screen
            else if (result == 0)
                Toast.makeText(con, R.string.no_search_results_found, Toast.LENGTH_SHORT).show();
            else
                // tell the Home Screen Activity, that the search is completed successfully
                // it will switch to the Search Results Screen
                callback.done();
        }
    }

    private Document getResponse(String url) throws ParserConfigurationException, IOException, SAXException
    {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(url);
        return doc;
    }

    private String printResponse(Document doc) throws TransformerException, FileNotFoundException
    {
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        Properties props = new Properties();
        props.put(OutputKeys.INDENT, "yes");
        trans.setOutputProperties(props);
        StreamResult res = new StreamResult(new StringWriter());
        DOMSource src = new DOMSource(doc);
        trans.transform(src, res);
        String toString = res.getWriter().toString();
        return toString;
    }
}
