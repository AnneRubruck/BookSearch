package rubruck.booksearch;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * used to show and animate the splash screen
 * Created by rubruck on 06/09/15.
 */
public class SplashScreenActivity extends Activity
{
    // thread processing splash screen events
    private Thread splashThread;

    /**
     * called when Splash Screen is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);

        final SplashScreenActivity splashScreen = this;

        // start the splash screen animation
        final ImageView splashImageView = (ImageView) findViewById(R.id.splashImageView);
        splashImageView.setBackgroundResource(R.drawable.splash_screen_animation);
        final AnimationDrawable frameAnimation = (AnimationDrawable) splashImageView.getBackground();

        splashImageView.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });

        // start a thread waiting for end time of splash screen
        // also allows user to end splash screen immediately by touching the screen
        splashThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    synchronized (this)
                    {
                        // while waiting, the splash screen is shown
                        // the user is allowed to click the splash Screen.
                        // what will close it immediately
                        wait(3000);
                    }
                }
                catch (InterruptedException e) { e.printStackTrace(); }

                finish();

                // Start next Activity
                Intent intent = new Intent();
                intent.setClass(splashScreen, HomeScreenActivity.class);
                startActivity(intent);


                try
                {
                    this.join();
                }
                catch (InterruptedException e) { e.printStackTrace(); }
            }
        };

        splashThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            synchronized (splashThread)
            {
                splashThread.notifyAll();
            }
        }
        return true;
    }
}
