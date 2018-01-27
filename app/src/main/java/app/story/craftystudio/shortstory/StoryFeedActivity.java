package app.story.craftystudio.shortstory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.RatingEvent;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.Locale;

import utils.FireBaseHandler;
import utils.SettingManager;
import utils.Story;

public class StoryFeedActivity extends AppCompatActivity {

    Story story;

    TextView titleTextView, descriptionTextView, authorTextView, dateTextView;

    ImageView displayImageImageview;

    ProgressDialog progressDialog;

    TextToSpeech tts;
    boolean isTestToSpeechOn = false;
    private int voiceReaderChunk = 0;
    private TextView storyLikesText;
    private ShineButton shineLikeButtonJava;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SettingManager.getThemeMode(this) == 1) {
            setTheme(R.style.ActivityTheme_Primary_Base_Dark);
        }

        setContentView(R.layout.activity_story_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        story = (Story) getIntent().getSerializableExtra("story");

        titleTextView = (TextView) findViewById(R.id.storyFeedActivity_title_textView);
        descriptionTextView = (TextView) findViewById(R.id.storyFeedActivity_description_textView);
        authorTextView = (TextView) findViewById(R.id.storyFeedActivity_authorName_textView);
        dateTextView = (TextView) findViewById(R.id.storyFeedActivity_storyDate_textView);
        displayImageImageview = (ImageView) findViewById(R.id.storyFeedActivity_image_imageview);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initializeActivity();

        try {
            tts = new TextToSpeech(StoryFeedActivity.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if (i == TextToSpeech.SUCCESS) {
                        tts.setLanguage(Locale.UK);
                        tts.setSpeechRate(1f);


                    } else {
                        // Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


        storyLikesText = (TextView) findViewById(R.id.fragmentShortStory_storyLikes_Textview);
        storyLikesText.setText(story.getStoryLikes() + " likes");
        //like button
        shineLikeButtonJava = (ShineButton)findViewById(R.id.like_shine_button);

        shineLikeButtonJava.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean checked) {
                if (checked) {

                    story.setStoryLikes(story.getStoryLikes() + 1);

                    storyLikesText.setText(story.getStoryLikes() + " likes");
                    //Toast.makeText(mainActivity, "ThankYou! for liking the story", Toast.LENGTH_SHORT).show();
                    uploadLike(story.getStoryLikes());

                    shineLikeButtonJava.setActivated(false);

                    Answers.getInstance().logRating(new RatingEvent().putContentName(story.getStoryTitle()).putContentId(story.getStoryID()).putRating(1));
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (tts != null) {
                tts.speak(".", TextToSpeech.QUEUE_FLUSH, null);
                tts.stop();
                tts.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeActivity() {

        titleTextView.setText(story.getStoryTitle());


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            String fullStory = story.getStoryFull();
            String newStory = fullStory.replaceAll("\n", "<br/>");
            descriptionTextView.setText(Html.fromHtml(newStory, Html.FROM_HTML_MODE_COMPACT));
        } else {

            String fullStory = story.getStoryFull();
            String newStory = fullStory.replaceAll("\n", "<br/>");
            descriptionTextView.setText(Html.fromHtml(newStory));

        }


        if (story.getStoryAuthorNAme().isEmpty()) {
            authorTextView.setVisibility(View.GONE);
        } else {
            authorTextView.setText("by " + story.getStoryAuthorNAme());
        }


        dateTextView.setText(story.getStoryDate());


        if (story.getStoryImageAddress() == null) {
            displayImageImageview.setVisibility(View.GONE);
        } else if (story.getStoryImageAddress().isEmpty()) {
            displayImageImageview.setVisibility(View.GONE);

        } else {
            try {

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("shortStoryImage/" + story.getStoryID() + "/" + "main");

                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(storageReference)
                        .crossFade(100)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(displayImageImageview);

            } catch (Exception e) {
                e.printStackTrace();
                displayImageImageview.setVisibility(View.GONE);

            }


        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_story_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            onShareClick();
            return true;
        } else if (id == R.id.action_tts_reader) {
            speakOutFullStory();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void speakOutFullStory() {

        try {
            isTestToSpeechOn = true;
            if (tts.isSpeaking()) {
                tts.speak("", TextToSpeech.QUEUE_FLUSH, null);
                tts.stop();

            } else {

                if (story.getStoryFull().length() < 3999) {
                    tts.speak(story.getStoryFull(), TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    voiceReaderChunk = 0;
                    voiceReaderChunkManager(story.getStoryFull());
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void voiceReaderChunkManager(final String ttsString) {

        if (ttsString.length() > (voiceReaderChunk)) {

            String chunk = ttsString.substring(voiceReaderChunk, Math.min(voiceReaderChunk + 3999, ttsString.length()));

            voiceReaderChunk = voiceReaderChunk + 3999;

            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    Log.d("TTS", "onDone: " + utteranceId);
                }

                @Override
                public void onDone(String utteranceId) {

                    Log.d("TTS", "onDone: " + utteranceId);
                    voiceReaderChunkManager(ttsString);

                }

                @Override
                public void onError(String utteranceId) {
                    Log.d("TTS", "onDone: " + utteranceId);
                }
            });

            try {
                if (Build.VERSION.SDK_INT > 21) {
                    tts.speak(chunk, TextToSpeech.QUEUE_FLUSH, null, "1");
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    }


    private void onShareClick() {

        showDialog();
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://goo.gl/UG9hPL?storyID=" + story.getStoryID()))
                .setDynamicLinkDomain("x87w4.app.goo.gl")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("app.story.craftystudio.shortstory")
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(story.getStoryTitle())
                                .setDescription(story.getStoryBookName() + " by " + story.getStoryAuthorNAme())
                                .setImageUrl(Uri.parse(story.getStoryImageAddress()))
                                .build())
                .setGoogleAnalyticsParameters(
                        new DynamicLink.GoogleAnalyticsParameters.Builder()
                                .setSource("share")
                                .setMedium("social")
                                .setCampaign("example-promo")
                                .build())
                .buildShortDynamicLink()
                .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();

                            openShareDialog(shortLink);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void openShareDialog(Uri shortUrl) {

        try {
            Answers.getInstance().logCustom(new CustomEvent("Share link created").putCustomAttribute("Content Id", story.getStoryID())
                    .putCustomAttribute("Shares", story.getStoryTitle()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        //sharingIntent.putExtra(Intent.EXTRA_STREAM, newsMetaInfo.getNewsImageLocalPath());

        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shortUrl
                + "\n\nRead Personality development Tips");
        startActivity(Intent.createChooser(sharingIntent, "Share Personality Development Tips via"));
        hideDialog();

    }

    public void showDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public void hideDialog() {
        progressDialog.cancel();
    }

    private void uploadLike(int currentLikes) {

        FireBaseHandler fireBaseHandler = new FireBaseHandler();

        fireBaseHandler.uploadStoryLikes(story.getStoryID(), currentLikes, new FireBaseHandler.OnLikeListener() {
            @Override
            public void onLikeUpload(boolean isSuccessful) {
                if (isSuccessful) {

                } else {
                    // storyLikesText.setText(story.getStoryLikes()+"");

                }
            }
        });

    }

}
