package rubruck.booksearch.backgroundTasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * This class is used to download images in background
 * Created by rubruck on 31/08/15.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
{
    ImageView imageView;

    public DownloadImageTask(ImageView imageView) {
        this.imageView = imageView;
    }

    protected Bitmap doInBackground(String... urls)
    {
        String url = urls[0];
        Bitmap mIcon11 = null;
        try
        {
            InputStream in = new java.net.URL(url).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        }
        catch (Exception e)
        {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result)
    {
        imageView.setImageBitmap(result);
    }
}
