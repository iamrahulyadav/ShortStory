package app.story.craftystudio.shortstory;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;

import utils.DataBaseHelper;
import utils.Story;
import utils.StoryAdapter;

public class BookmarkActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StoryAdapter storyAdapter;
    private boolean isLoading = false;
    ArrayList<Story> bookmarkStories = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //getting bookmarked stories
        bookmarkStories = getAllbookmarkStories();
        initializeRecyclerView();

    }

    //setting up recyclerview
    private void initializeRecyclerView() {

        recyclerView = (RecyclerView) findViewById(R.id.bookmarkActivity_recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        storyAdapter = new StoryAdapter(bookmarkStories, this);

        storyAdapter.setClickListener(new StoryAdapter.ClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                onItemClicked(position);
            }
        });


        recyclerView.setAdapter(storyAdapter);

    }

    //Opening New Activity on clicking on card
    private void onItemClicked(int position) {

        Intent intent = new Intent(BookmarkActivity.this, StoryFeedActivity.class);
        intent.putExtra("story", bookmarkStories.get(position));
        startActivity(intent);


    }


    //downloading all bookmarked stories from database
    private ArrayList<Story> getAllbookmarkStories() {

        ArrayList<Story> stories = new ArrayList<>();
        DataBaseHelper dataBaseHelper = new DataBaseHelper(BookmarkActivity.this);
        stories = dataBaseHelper.getAllStories();

        Collections.reverse(stories);
        return stories;
    }


}
