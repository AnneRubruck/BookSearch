package rubruck.booksearch;

import android.os.Bundle;
import android.widget.TextView;

import rubruck.booksearch.utilities.BaseMenuActivity;

/**
 * This Activity is started to show information such as
 * the general business information or legal information
 *
 * Created by rubruck on 01/09/15.
 */
public class InformationActivity extends BaseMenuActivity
{
    //private final String GEN_BUSINESS_INFO = getString(R.string.businessInformationText);
    //private final String LEGAL_INFO = getString(R.string.legal_information_text);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.information_screen);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getResources().getString(R.string.bs_information));

        if ((getIntent() != null)&&(getIntent().getExtras().get("TYPE") != null))
        {
            TextView textView = (TextView) findViewById(R.id.informationTextView);
            if (getIntent().getExtras().get("TYPE") != null)
            {
                String type = getIntent().getExtras().get("TYPE").toString();
                switch (type) {
                    case "BUSINESS_INFO": {
                        System.out.println(getString(R.string.businessInformationTitle));
                        textView.setText(getString(R.string.businessInformationTitle) + "\n\n" + getString(R.string.businessInformationText));
                        break;
                    }
                    case "LEGAL_INFO": {
                        System.out.println(getString(R.string.legal_information_title));
                        textView.setText(getString(R.string.legal_information_title) + "\n\n" + getString(R.string.legal_information_text));
                        break;
                    }
                }
            }
        }
    }
}
