package rubruck.booksearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.util.Locale;

/**
 * This class handles preferences, such as
 * Language, Sound, ...
 *
 * Created by rubruck on 01/09/15.
 */
public class SettingsActivity extends PreferenceActivity
{
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setLocale(this.getApplicationContext());
        addPreferencesFromResource(R.xml.preferences);
    }

    public static void setLocale (Context context)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        // language (default: en)
        String language = sp.getString("listPrefLanguages","en");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getApplicationContext().getResources().updateConfiguration(config, null);
    }
}
