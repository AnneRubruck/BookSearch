package rubruck.booksearch.utilities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;

import rubruck.booksearch.InformationActivity;
import rubruck.booksearch.R;
import rubruck.booksearch.SettingsActivity;
import rubruck.booksearch.favorites.FavoritesActivity;

import static rubruck.booksearch.R.drawable.icon;

/**
 * This class makes up a base activity,
 * extended by all activities of this app
 * to provide all of them with the same menu
 *
 * Created by rubruck on 01/09/15.
 */
public class BaseMenuActivity extends AppCompatActivity
{

    private static final int SETTINGS = 1;
    private static Locale locale = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // set local (language) if necessary
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration config = getBaseContext().getResources().getConfiguration();
        String lang = settings.getString("listPrefLanguages", "");
        if (! "".equals(lang) && ! config.locale.getLanguage().equals(lang))
        {
            locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    /**
     * this method is used to update local (language)
     * @param newConfig new configuration, containing the new locale (language)
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if (locale != null)
        {
            newConfig.locale = locale;
            Locale.setDefault(locale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);


        // if we are in the Favorites Activity don't show the favorites menu item
        MenuItem favoritesItem = menu.findItem(R.id.action_favorites);
        if (this instanceof FavoritesActivity)
            favoritesItem.setVisible(false);
        else
            favoritesItem.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_favorites:
            {
                // switch to Favorites Activity
                Intent intent = new Intent();
                intent.setClass(this, FavoritesActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.action_settings:
            {
                Intent intentPreferences = new Intent(this, SettingsActivity.class);
                // the passed back result (changed settings) can be identified using the id SETTINGS
                startActivityForResult(intentPreferences, SETTINGS);
                break;
            }
            case R.id.action_business_terms:
            {
                // switch to Information Activity and show business terms (AGB)
                Intent intent = new Intent();
                intent.setClass(this, InformationActivity.class);
                intent.putExtra("TYPE", "BUSINESS_INFO");
                startActivity(intent);
                break;
            }
            case R.id.legal_information:
            {
                // switch to Information Activity and show legal information (Impressum)
                Intent intent = new Intent();
                intent.setClass(this,InformationActivity.class);
                intent.putExtra("TYPE","LEGAL_INFO");
                startActivity(intent);
                break;
            }
            case R.id.action_exit:
            {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // collect the changes that have been done in the SettingsActivity intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SETTINGS)
        {
            // apply changed settings
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

            // language (default: en)
            String languageString = sp.getString("listPrefLanguages","en");
            // will be set in all Activities before setContentView is called
            // needs to call onCreate again, to have effect
            this.recreate();
        }
    }
}
