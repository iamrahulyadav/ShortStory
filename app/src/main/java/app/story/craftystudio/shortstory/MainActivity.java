package app.story.craftystudio.shortstory;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import java.util.ArrayList;

import utils.AppRater;
import utils.FireBaseHandler;
import utils.Story;
import utils.ZoomOutPageTransformer;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    ArrayList<Story> mStoryList = new ArrayList<>();

    FireBaseHandler fireBaseHandler;

    static boolean nightMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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

        //upload story


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fireBaseHandler = new FireBaseHandler();
        mPager = (ViewPager) findViewById(R.id.mainActivity_viewpager);


        initializeViewPager();
        openDynamicLink();

        //calling rate now dialog
        AppRater appRater = new AppRater();
        appRater.app_launched(MainActivity.this);



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
                            Toast.makeText(MainActivity.this, "Story id " + shortStoryID, Toast.LENGTH_SHORT).show();

                            //download story list
                            downloadStory(shortStoryID);

                            // downloadNewsArticle(newsArticleID);

                        } else {
                            Log.d("DeepLink", "onSuccess: ");

                            //download story list
                            downloadStoryList();

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
                        Log.w("DeepLink", "getDynamicLink:onFailure", e);
                    }
                });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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

        if (id == R.id.nav_camera) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");

            //sharingIntent.putExtra(Intent.EXTRA_STREAM, newsMetaInfo.getNewsImageLocalPath());

            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                    "\n\n App Link Read Full Story");
            startActivity(Intent.createChooser(sharingIntent, "Share Story App via"));

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void downloadStory(String shortStoryUID) {

        fireBaseHandler.downloadStory(shortStoryUID, new FireBaseHandler.OnStorylistener() {
            @Override
            public void onStoryDownLoad(Story story, boolean isSuccessful) {

                if (isSuccessful) {
                    mStoryList.add(story);
                    mPagerAdapter.notifyDataSetChanged();
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

                if (isSuccessful) {

                    for (Story story : storyList) {
                        MainActivity.this.mStoryList.add(story);
                    }

                    mPagerAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onStoryUpload(boolean isSuccessful) {

            }
        });
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

                    mPagerAdapter.notifyDataSetChanged();

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


    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

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
}
