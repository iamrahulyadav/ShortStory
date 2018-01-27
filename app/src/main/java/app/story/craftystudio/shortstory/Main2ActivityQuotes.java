package app.story.craftystudio.shortstory;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import java.util.ArrayList;

import utils.FireBaseHandler;
import utils.Quotes;
import utils.SettingManager;
import utils.Story;

public class Main2ActivityQuotes extends AppCompatActivity {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    AdView mAdView;

    ArrayList<Quotes> mQuotesList = new ArrayList<>();
    FireBaseHandler fireBaseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SettingManager.getThemeMode(this) == 1) {
            setTheme(R.style.ActivityTheme_Primary_Base_Dark);
        }

        setContentView(R.layout.activity_main2_quotes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                uploadQuotes();
            }
        });


        mPager = (ViewPager) findViewById(R.id.mainActivity2_quotes_viewpager);

        fireBaseHandler = new FireBaseHandler();
        downloadQuotesList();

        initializeBannerAd();
    }

    private void initializeBannerAd() {
        mAdView = (AdView) findViewById(R.id.mainActivity2_banner_adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);

            }
        });


    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
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

                // checkInterstitialAds();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void downloadQuotesList() {

        fireBaseHandler.downloadQuotesist(5, new FireBaseHandler.OnQuoteslistener() {
            @Override
            public void onQuotesDownLoad(Quotes quotes, boolean isSuccessful) {

            }

            @Override
            public void onQuotesListDownLoad(ArrayList<Quotes> quotesArrayListList, boolean isSuccessful) {

                initializeViewPager();

                if (isSuccessful) {

                    for (Quotes quotes : quotesArrayListList) {
                        Main2ActivityQuotes.this.mQuotesList.add(quotes);
                    }

                    mPagerAdapter.notifyDataSetChanged();

                } else {
                    //  openConnectionFailureDialog();
                }
            }

            @Override
            public void onQuotesUpload(boolean isSuccessful) {

            }
        });
    }


    public void downloadMoreQuotesList() {
        fireBaseHandler.downloadQuotesist(5, mQuotesList.get(mQuotesList.size() - 1).getQuotesID(), new FireBaseHandler.OnQuoteslistener() {
            @Override
            public void onQuotesDownLoad(Quotes quotes, boolean isSuccessful) {

            }

            @Override
            public void onQuotesListDownLoad(ArrayList<Quotes> quotesArrayListList, boolean isSuccessful) {
                if (isSuccessful) {

                    for (Quotes quotes : quotesArrayListList) {
                        Main2ActivityQuotes.this.mQuotesList.add(quotes);
                    }

                    mPagerAdapter.notifyDataSetChanged();

                } else {
                    //  openConnectionFailureDialog();
                }
            }

            @Override
            public void onQuotesUpload(boolean isSuccessful) {

            }
        });

    }

    public void uploadQuotes() {

        fireBaseHandler = new FireBaseHandler();
        Quotes quotes = new Quotes();
        quotes.setQuotesFull("hy hwlkjfh fbejkg ndfgd nbfvehv nxbdv cchvfh xnbvfh nvdshfv ");
        quotes.setQuotesAuthorName("mona boss");
        fireBaseHandler.uploadQuotes(quotes, new FireBaseHandler.OnQuoteslistener() {
            @Override
            public void onQuotesDownLoad(Quotes quotes, boolean isSuccessful) {

                initializeViewPager();

                if (isSuccessful) {
                    Toast.makeText(Main2ActivityQuotes.this, "Quotes Uploaded", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onQuotesListDownLoad(ArrayList<Quotes> quotesArrayListList, boolean isSuccessful) {

            }

            @Override
            public void onQuotesUpload(boolean isSuccessful) {

            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            //   adsCount++;
            //getting more stories

            if (position == mQuotesList.size() - 2) {
                downloadMoreQuotesList();
            }


            return QuotesFragment.newInstance(mQuotesList.get(position), Main2ActivityQuotes.this);
        }

        @Override
        public int getCount() {
            return mQuotesList.size();
        }
    }

}
