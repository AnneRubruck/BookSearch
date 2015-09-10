package rubruck.booksearch.details;

import android.content.res.Configuration;
import android.os.Bundle;

import rubruck.booksearch.R;
import rubruck.booksearch.utilities.BaseMenuActivity;

/**
 * This class is used to display a books details in portrait mode.
 * In portrait mode only the details are shown.
 *
 * When in landscape mode, details are shown on the same screen as the titles list.
 * In that case, this activity is not used.
 */
public class DetailsActivity extends BaseMenuActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Check if the device is in landscape mode
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            // If the screen is now in landscape mode this activity is not needed
            finish();
            return;
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getResources().getString(R.string.bs_details));

        // Check if we have a details fragment already
        if (savedInstanceState == null)
        {
            // If not then create the DetailsFragment
            DetailsFragment details = new DetailsFragment();

            // Get the Bundle of key value pairs and assign them to the DetailsFragment
            details.setArguments(getIntent().getExtras());

            // Add the details Fragment
            getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
        }
    }
}
