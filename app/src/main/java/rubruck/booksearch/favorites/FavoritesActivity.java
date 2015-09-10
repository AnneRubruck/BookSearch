package rubruck.booksearch.favorites;

import android.os.Bundle;
import android.widget.ListView;

import rubruck.booksearch.R;
import rubruck.booksearch.utilities.BaseMenuActivity;

/**
 * This class is used to display favoritesList
 * and let the user manage them.
 *
 * Created by rubruck on 31/08/15.
 */
public class FavoritesActivity extends BaseMenuActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        FavoritesManager fav = FavoritesManager.getFavoritesInstance(getApplicationContext());

        setContentView(R.layout.favorites_layout);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getResources().getString(R.string.bs_favorites));

        // get the favoritesList in the ListView
        FavoritesArrayAdapter connectArrayToListView = new FavoritesArrayAdapter(this, fav.getFavorites());
        ListView list = (ListView) findViewById(R.id.favoritesListView);
        list.setAdapter(connectArrayToListView);
    }
}
