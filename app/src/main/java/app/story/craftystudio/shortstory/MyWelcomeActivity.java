package app.story.craftystudio.shortstory;

import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

/**
 * Created by Aisha on 9/16/2017.
 */

public class MyWelcomeActivity extends WelcomeActivity {

    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.colorTurorial1)
                .page(new BasicPage(R.drawable.tutorial_personality,
                        "Daily Personality Development tips"
                        , "Become the best version of yourself")
                        .background(R.color.colorTurorial1)
                )
                .page(new BasicPage(R.drawable.tutorial_quotes,
                        "INSPIRING QUOTES",
                        "Keep Reading It's one of the most Marvelous Adventure any one can have")
                        .background(R.color.colorTurorial2)
                )
                .page(new BasicPage(R.drawable.tutorial_share,
                        "LIKE AND SHARE",
                        "It's time to inspire others by sharing tips and quotes")
                        .background(R.color.colorTurorial3)
                )
                .swipeToDismiss(true)
                .build();
    }
}
