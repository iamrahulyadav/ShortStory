package app.story.craftystudio.shortstory;

import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.stephentuso.welcome.WelcomeHelper;

import io.fabric.sdk.android.Fabric;

import java.util.ArrayList;
import java.util.List;

import utils.AppRater;
import utils.FireBaseHandler;
import utils.RandomSplashQuotes;
import utils.Story;
import utils.ZoomOutPageTransformer;

import static android.R.id.message;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    ArrayList<Story> mStoryList = new ArrayList<>();

    FireBaseHandler fireBaseHandler;

    static boolean nightMode = false;

    int adsCount = 0;
    private InterstitialAd mInterstitialAd;

    private boolean pendingInterstitialAd;
    private Handler handler;
    private Runnable runnable;

    boolean isSplashScreen = true;

    WelcomeHelper welcomeScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        //tutorial display
        welcomeScreen = new WelcomeHelper(this, MyWelcomeActivity.class);
        welcomeScreen.show(savedInstanceState);

        setContentView(R.layout.splash_main);

        try{
            RandomSplashQuotes randomSplashQuotes =new RandomSplashQuotes();
            TextView textView =(TextView)findViewById(R.id.splash_quote_textView);
            textView.setText(randomSplashQuotes.randomQuote());
        }catch(Exception e){
            e.printStackTrace();
        }

        MobileAds.initialize(this, "ca-app-pub-8455191357100024~5605269189");

        fireBaseHandler = new FireBaseHandler();
        openDynamicLink();
    }

    public void initializeActivity() {
        setContentView(R.layout.activity_main);
        isSplashScreen = false;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mPager = (ViewPager) findViewById(R.id.mainActivity_viewpager);


        initializeViewPager();


        //calling rate now dialog
        AppRater appRater = new AppRater();
        appRater.app_launched(MainActivity.this);

        FirebaseMessaging.getInstance().subscribeToTopic("subscribed");
        // FirebaseMessaging.getInstance().subscribeToTopic("tester");
        //Log.d("push notifiaction", "onCreate: "+ FirebaseInstanceId.getInstance().getToken());


        //initializeInterstitialAds();

    }

    private void openDynamicLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.d("DeepLink", "onSuccess: " + deepLink);

                            String shortStoryID = deepLink.getQueryParameter("storyID");
                            //Toast.makeText(MainActivity.this, "Story id " + shortStoryID, Toast.LENGTH_SHORT).show();

                            //download story
                            if (shortStoryID != null) {
                                downloadStory(shortStoryID);
                                try {
                                    Answers.getInstance().logCustom(new CustomEvent("Via dyanamic link").putCustomAttribute("Story id", shortStoryID));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                openQuotesActivity();
                                try {
                                    Answers.getInstance().logCustom(new CustomEvent("Via dyanamic link quotes").putCustomAttribute("Quotes", 1));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                            // downloadNewsArticle(newsArticleID);

                        } else {
                            Log.d("DeepLink", "onSuccess: ");

                            //download story list


                            try {
                                Intent intent = getIntent();
                                String storyID = intent.getStringExtra("storyID");
                                if (storyID == null) {
                                    downloadStoryList();
                                } else {
                                    //download story
                                    downloadStory(storyID);
                                    try {
                                        Answers.getInstance().logCustom(new CustomEvent("Via push notification").putCustomAttribute("Story id", storyID));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //   Toast.makeText(this, "Story id is = "+storyID, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                downloadStoryList();
                                e.printStackTrace();
                            }


                        }


                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        downloadStoryList();
                        Log.w("DeepLink", "getDynamicLink:onFailure", e);
                    }
                });
    }

    private void openQuotesActivity() {
        downloadStoryList();
        Intent intent = new Intent(MainActivity.this, Main2ActivityQuotes.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        if (!isSplashScreen) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_suggestion) {
            giveSuggestion();

        } else if (id == R.id.nav_rate) {

            rateUs();

        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");

            //sharingIntent.putExtra(Intent.EXTRA_STREAM, newsMetaInfo.getNewsImageLocalPath());

            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                    "\n\n Read Short Inspirational Stories Daily \n Download it now \n ");
            startActivity(Intent.createChooser(sharingIntent, "Share Story App via"));

        } else if (id == R.id.nav_quotes) {
            Intent intent = new Intent(MainActivity.this, Main2ActivityQuotes.class);
            startActivity(intent);
        } else if (id == R.id.nav_tutlorial) {
            welcomeScreen.forceShow();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void giveSuggestion() {

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"acraftystudio@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Suggestion From Short Story User");
        emailIntent.setType("text/plain");

        startActivity(Intent.createChooser(emailIntent, "Send mail From..."));

    }

    private void rateUs() {

        MainActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=app.craftystudio.vocabulary.dailyeditorial&hl=en")));

    }

    private void downloadStory(String shortStoryUID) {

        fireBaseHandler.downloadStory(shortStoryUID, new FireBaseHandler.OnStorylistener() {
            @Override
            public void onStoryDownLoad(Story story, boolean isSuccessful) {

                if (isSplashScreen) {
                    initializeActivity();
                }

                if (isSuccessful) {
                    mStoryList.add(story);
                    mPagerAdapter.notifyDataSetChanged();
                } else {
                    openConnectionFailureDialog();
                }
                downloadStoryList();
            }

            @Override
            public void onStoryListDownLoad(ArrayList<Story> storyList, boolean isSuccessful) {

            }

            @Override
            public void onStoryUpload(boolean isSuccessful) {

            }
        });
    }

    public void downloadStoryList() {
        fireBaseHandler.downloadStoryList(5, new FireBaseHandler.OnStorylistener() {
            @Override
            public void onStoryDownLoad(Story story, boolean isSuccessful) {

            }

            @Override
            public void onStoryListDownLoad(ArrayList<Story> storyList, boolean isSuccessful) {
                //    Toast.makeText(MainActivity.this, "Story size is " + storyList.size() + "\n " + storyList.get(2).getStoryTitle(), Toast.LENGTH_SHORT).show();

                if (isSplashScreen) {
                    initializeActivity();
                }
                if (isSuccessful) {

                    for (Story story : storyList) {
                        MainActivity.this.mStoryList.add(story);
                    }

                    initializeNativeAds();
                    mPagerAdapter.notifyDataSetChanged();

                } else {
                    openConnectionFailureDialog();
                }
            }

            @Override
            public void onStoryUpload(boolean isSuccessful) {

            }
        });
    }

    public void openConnectionFailureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Thank You!");
        builder.setMessage("If you like the App, Give Review and support us")
                .setPositiveButton("Rate Now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                        recreate();

                        // FIRE ZE MISSILES!
                    }
                });

        // Create the AlertDialog object and return it
        builder.create();
        builder.show();


    }


    public void downloadMoreStoryList() {
        fireBaseHandler.downloadStoryList(5, mStoryList.get(mStoryList.size() - 1).getStoryID(), new FireBaseHandler.OnStorylistener() {
            @Override
            public void onStoryDownLoad(Story story, boolean isSuccessful) {

            }

            @Override
            public void onStoryListDownLoad(ArrayList<Story> storyList, boolean isSuccessful) {

                if (isSuccessful) {

                    for (Story story : storyList) {
                        MainActivity.this.mStoryList.add(story);
                    }

                    initializeNativeAds();
                    mPagerAdapter.notifyDataSetChanged();

                } else {
                    openConnectionFailureDialog();
                }

            }


            @Override
            public void onStoryUpload(boolean isSuccessful) {

            }


        });

    }

    private void initializeViewPager() {

// Instantiate a ViewPager and a PagerAdapter.

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        //change to zoom
        mPager.setPageTransformer(true, new RotateUpTransformer());

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //checkInterstitialAds();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void uploadStory() {

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

       /* fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Story story = new Story();
                story.setStoryAuthorNAme("Mona");
                story.setStoryBookLink("http://mona");
                story.setStoryBookName("Boss");
                story.setStoryDate("11/8/17");
                story.setStoryFull("hey!how are you Work hard and stay humble ");
                story.setStoryGenre("Inspirational");
                story.setStoryTag("dailyLife");
                story.setStoryTitle("Step Forward");

                FireBaseHandler fireBaseHandler = new FireBaseHandler();
                fireBaseHandler.uploadStory(story, new FireBaseHandler.OnStorylistener() {
                    @Override
                    public void onStoryDownLoad(Story story, boolean isSuccessful) {
                        //             Snackbar.make(view, "Story Title is " + story.getStoryTitle(), Snackbar.LENGTH_LONG)
                        //                   .setAction("Action", null).show();


                    }

                    @Override
                    public void onStoryListDownLoad(ArrayList<Story> storyList, boolean isSuccessful) {

                    }

                    @Override
                    public void onStoryUpload(boolean isSuccessful) {
                        if (isSuccessful) {
                            Snackbar.make(view, "Story uploaded ", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();


                        } else {
                            Snackbar.make(view, "Failed to upload ", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();


                        }

                    }
                });


            }
        });*/


    }

    public void initializeNativeAds() {

        DisplayMetrics displayMetrics = MainActivity.this.getResources().getDisplayMetrics();
        int dpHeight = (int) (displayMetrics.heightPixels / displayMetrics.density);
        int dpWidth = (int) (displayMetrics.widthPixels / displayMetrics.density);

        for (Story story : mStoryList) {

            if (story.getNativeExpressAdView() == null) {

                NativeExpressAdView adView = new NativeExpressAdView(this);

                adView.setAdUnitId("ca-app-pub-8455191357100024/7311187776");
                adView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

                adView.setAdSize(new AdSize(dpWidth - 64, 150));
                adView.loadAd(new AdRequest.Builder().build());
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int i) {
                        super.onAdFailedToLoad(i);
                        try {
                            Answers.getInstance().logCustom(new CustomEvent("Ad failed to load").putCustomAttribute("placement", "bottom").putCustomAttribute("error code", i));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Log.d("native ads", "onAdFailedToLoad: ");
                    }
                });
                story.setNativeExpressAdView(adView);

            }

        }


        for (int i = 0; i < mStoryList.size(); i++) {

            if (i % 3 == 2) {

                if (mStoryList.get(i).getObjectType() != 1) {


                    Story nativeAdsStory = new Story();
                    nativeAdsStory.setObjectType(1);
                    NativeExpressAdView adView = new NativeExpressAdView(this);

                    adView.setAdUnitId("ca-app-pub-8455191357100024/7223557860");
                    adView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

                    adView.setAdSize(new AdSize(dpWidth - 24, 3 * (dpHeight / 4)));
                    adView.loadAd(new AdRequest.Builder().build());
                    adView.setAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(int i) {
                            super.onAdFailedToLoad(i);
                            try {
                                Answers.getInstance().logCustom(new CustomEvent("Ad failed to load").putCustomAttribute("placement", "top").putCustomAttribute("error code", i));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    nativeAdsStory.setNativeExpressAdView(adView);

                    mStoryList.add(i, nativeAdsStory);
                }

            }

        }


    }

    public void initializeInterstitialAds() {

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8455191357100024/8750307275");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        interstitialAdTimer(45000);


        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.i("Ads", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.i("Ads", "onAdFailedToLoad");

                Answers.getInstance().logCustom(new CustomEvent("Ad failed to load").putCustomAttribute("Failed index", errorCode));

            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
                Log.i("Ads", "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.i("Ads", "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                Log.i("Ads", "onAdClosed");
                adsCount = 0;
                interstitialAdTimer(45000);

                mInterstitialAd.loadAd(new AdRequest.Builder().build());

            }


        });

    }

    public void interstitialAdTimer(long waitTill) {
        pendingInterstitialAd = false;

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                pendingInterstitialAd = true;
            }
        };


        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, waitTill);


    }


    private void checkInterstitialAds() {

        if (adsCount > 2 && pendingInterstitialAd) {
            if (mInterstitialAd.isLoaded()) {
                //  mInterstitialAd.show();
            } else {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());

            }
        }
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            adsCount++;
            //getting more stories
            if (position == mStoryList.size() - 2) {
                downloadMoreStoryList();
            }

            return ShortStoryFragment.newInstance(mStoryList.get(position), MainActivity.this, nightMode);
        }

        @Override
        public int getCount() {
            return mStoryList.size();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        welcomeScreen.onSaveInstanceState(outState);
    }
}
