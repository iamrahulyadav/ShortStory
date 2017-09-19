package app.story.craftystudio.shortstory;

import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

/**
 * Created by Aisha on 9/16/2017.
 */

public class MyWelcomeActivity extends WelcomeActivity{

    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.colorAccent)
                .page(new TitlePage(R.drawable.ic_action_quotes,
                        "Title")
                )
                .page(new BasicPage(R.drawable.ic_action_speaker,
                        "Header",
                        "More text.")
                        .background(R.color.colorPrimary)
                )
                .page(new BasicPage(R.drawable.ic_menu_camera,
                        "Lorem ipsum",
                        "dolor sit amet.")
                )
                .swipeToDismiss(true)
                .build();
    }
}
