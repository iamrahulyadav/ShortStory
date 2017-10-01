package app.story.craftystudio.shortstory;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;

import android.speech.tts.TextToSpeech;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.RatingEvent;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

import utils.FireBaseHandler;
import utils.Like;
import utils.Story;
import utils.StoryWordMeaning;

/**
 * Created by Aisha on 8/11/2017.
 */

public class ShortStoryFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Story story;

    TextView descriptionText;

    static MainActivity mainActivity;
    TextToSpeech tts;

    static boolean mNightModeOn = false;

    static SharedPreferences prefs;
    TextView titleText;
    TextView bookLinkText;
    TextView storyAuthorNameText;
    TextView storyGenreText;
    TextView storyTagText;
    TextView storyDateText;
    TextView wordMeaningTextview;
    CardView itemHolderCardview;

    TextView bookNameText;
    TextView storyLikesText;

    boolean isDarkNightModeOn = false;
    boolean isSepiaNightModeOn = false;

    BottomSheetBehavior behavior;

    boolean isTestToSpeechOn = false;

    ProgressDialog progressDialog;

    public static ShortStoryFragment newInstance(Story story, MainActivity context, boolean nightMode) {
        mainActivity = context;
        mNightModeOn = nightMode;
        ShortStoryFragment fragment = new ShortStoryFragment();
        Bundle args = new Bundle();
        args.putSerializable("Story", story);
        fragment.setArguments(args);
        prefs = mainActivity.getSharedPreferences(
                "app.story.craftystudio.shortstory", Context.MODE_PRIVATE);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.story = (Story) getArguments().getSerializable("Story");

            try {
                Answers.getInstance().logContentView(new ContentViewEvent()
                        .putContentName(story.getStoryTitle())
                        .putContentId(story.getStoryID())
                );
            }catch(Exception e){
                e.printStackTrace();
            }

        }

        boolean darkNightModepref = prefs.getBoolean("DarkNightModeOn", false);
        boolean sepiaNightModepref = prefs.getBoolean("SepiaNightModeOn", false);

        if (darkNightModepref) {
            //Toast.makeText(mainActivity, "dark night mode ON", Toast.LENGTH_SHORT).show();
            isDarkNightModeOn = true;
            // onDarkNightMode();
        }

        if (sepiaNightModepref) {
            isSepiaNightModeOn = true;
            // onSepiaNightMode();
            //  Toast.makeText(mainActivity, "Sepia night mode ON", Toast.LENGTH_SHORT).show();
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (story.getObjectType() == 1) {
            View view = inflater.inflate(R.layout.nativead_card_layout, container, false);
            LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.nativead_container_linearLayout);

            NativeExpressAdView nativeExpressAdView= story.getNativeExpressAdView();

            if (nativeExpressAdView.getParent() != null) {
                ((ViewGroup) nativeExpressAdView.getParent()).removeView(nativeExpressAdView);
            }


            linearLayout.removeAllViews();
            linearLayout.addView(nativeExpressAdView);

            return view;
        }

        View view = inflater.inflate(R.layout.fragment_short_story, container, false);
        //initializeView


        titleText = (TextView) view.findViewById(R.id.fragmentShortStory_title_textView);

        //formatting text
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            String fulltitle = story.getStoryTitle();
            String newTitle = fulltitle.replaceAll("\n", "<br/>");

            titleText.setText(Html.fromHtml(newTitle, Html.FROM_HTML_MODE_COMPACT));
        } else {

            String fulltitle = story.getStoryTitle();
            String newTitle = fulltitle.replaceAll("\n", "<br/>");
            titleText.setText(Html.fromHtml(newTitle));
            //descriptionText.setText(story.getStoryFull());

        }
        descriptionText = (TextView) view.findViewById(R.id.fragmentShortStory_description_textView);
        //init(story.getStoryFull());

        //formatting text
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            String fullStory = story.getStoryFull();
            String newStory = fullStory.replaceAll("\n", "<br/>");
            descriptionText.setText(Html.fromHtml(newStory, Html.FROM_HTML_MODE_COMPACT));
        } else {

            String fullStory = story.getStoryFull();
            String newStory = fullStory.replaceAll("\n", "<br/>");
            descriptionText.setText(Html.fromHtml(newStory));
            //descriptionText.setText(story.getStoryFull());

        }

        //get wordmeaning of word tap
        wordMeaningTextview = (TextView) view.findViewById(R.id.fragmentShortStory_storyWordMeaning_textView);

        //Name of a book
        bookNameText = (TextView) view.findViewById(R.id.fragmentShortStory_bookName_textView);
        if (story.getStoryBookName().isEmpty()) {
            bookNameText.setVisibility(View.GONE);
        } else {
            bookNameText.setText("Book Name -" + story.getStoryBookName());

        }

        //link of book to buy
        bookLinkText = (TextView) view.findViewById(R.id.fragmentShortStory_bookLink_textView);
        if (story.getStoryBookLink().isEmpty()) {
            bookLinkText.setVisibility(View.GONE);
        } else {
            bookLinkText.setText("Get Book From -" + story.getStoryBookLink());
        }

        //name of Author
        storyAuthorNameText = (TextView) view.findViewById(R.id.fragmentShortStory_authorName_textView);
        if (story.getStoryAuthorNAme().isEmpty()) {
            storyAuthorNameText.setVisibility(View.GONE);
        } else {
            storyAuthorNameText.setText("by " + story.getStoryAuthorNAme());
        }

        //story genre
        storyGenreText = (TextView) view.findViewById(R.id.fragmentShortStory_genre_textView);
        storyGenreText.setText(story.getStoryGenre());

        //small tag for story
        storyTagText = (TextView) view.findViewById(R.id.fragmentShortStory_tag_textView);
        storyTagText.setText(story.getStoryTag());

        //date on which story uploaded
        storyDateText = (TextView) view.findViewById(R.id.fragmentShortStory_storyDate_textView);
        storyDateText.setText(story.getStoryDate());

        storyLikesText = (TextView) view.findViewById(R.id.fragmentShortStory_storyLikes_Textview);
        storyLikesText.setText(story.getStoryLikes() + " likes");


        //speak full story
        ImageView readFullStoryButton = (ImageView) view.findViewById(R.id.fragmentShortStory_storyReadFull_Button);
        readFullStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*if (isTestToSpeechOn) {
                    stopReadingFullStory();
                } else {
                    speakOutFullStory();
                }*/

                speakOutFullStory();

            }
        });

        //open Quotes Activty
        ImageView openQuotesActivityButton = (ImageView) view.findViewById(R.id.fragmentShortStory_quotes_activity_Button);
        openQuotesActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainActivity, Main2ActivityQuotes.class);
                startActivity(intent);
            }
        });


        //display image
        ImageView displayImageImageview = (ImageView) view.findViewById(R.id.fragmentShortStory_image_imageview);
        if (story.getStoryImageAddress() == null) {
            displayImageImageview.setVisibility(View.GONE);
        } else if (story.getStoryImageAddress().isEmpty()) {
            displayImageImageview.setVisibility(View.GONE);

        } else {
            try {

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("shortStoryImage/" + story.getStoryID() + "/" + "main");

                Glide.with(getContext())
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

        itemHolderCardview = (CardView) view.findViewById(R.id.fragmentShortStory_MainItemHolder_CardView);


        //check which mode is ON and act accordingly
        if (isSepiaNightModeOn) {
            onSepiaNightMode();
        } else if (isDarkNightModeOn) {
            onDarkNightMode();

        }

        //like button
        final ShineButton shineLikeButtonJava = (ShineButton) view.findViewById(R.id.like_shine_button);

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

        final ShineButton shineShareButtonJava = (ShineButton) view.findViewById(R.id.share_shine_button);

        shineShareButtonJava.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean checked) {
                if (checked) {
                    Toast.makeText(mainActivity, "Opening Share Dialog", Toast.LENGTH_SHORT).show();

                    onShareClick();


                    Answers.getInstance().logCustom(new CustomEvent("Share link").putCustomAttribute("Story name", story.getStoryTitle()));

                }
            }
        });


        ImageView shareStoryButton = (ImageView) view.findViewById(R.id.fragmentShortStory_sharestory_Button);
        shareStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShareClick();

                Answers.getInstance().logCustom(new CustomEvent("Share link").putCustomAttribute("Story name", story.getStoryTitle()));
            }
        });

        //change for material sheet and changing NIGHT MODE
        final MaterialSheetFab materialSheetFab;
        FloatingActionButton fabs = (FloatingActionButton) view.findViewById(R.id.fabs);
        View sheetView = view.findViewById(R.id.fab_sheet);
        View overlay = view.findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.colorWhiteBg);
        final int fabColor = getResources().getColor(R.color.colorAccent);

        // Initialize material sheet FAB
        materialSheetFab = new MaterialSheetFab(fabs, sheetView, overlay,
                sheetColor, fabColor);

        final TextView sepiaModeOnTextview = (TextView) view.findViewById(R.id.materialSheet_sepiaMode_textview);
        sepiaModeOnTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(mainActivity, "Sepia selected", Toast.LENGTH_SHORT).show();
                materialSheetFab.hideSheet();

                prefs.edit().putBoolean("SepiaNightModeOn", true).apply();
                prefs.edit().putBoolean("DarkNightModeOn", false).apply();
                onSepiaNightMode();
            }
        });

        final TextView darkModeOnTextview = (TextView) view.findViewById(R.id.materialSheet_darkMode_textview);
        darkModeOnTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Toast.makeText(mainActivity, "Dark selected", Toast.LENGTH_SHORT).show();
                materialSheetFab.hideSheet();

                prefs.edit().putBoolean("DarkNightModeOn", true).apply();
                prefs.edit().putBoolean("SepiaNightModeOn", false).apply();

                onDarkNightMode();
            }
        });

        final TextView normalModeOnTextview = (TextView) view.findViewById(R.id.materialSheet_normalMode_textview);
        normalModeOnTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(mainActivity, "Normal selected", Toast.LENGTH_SHORT).show();
                materialSheetFab.hideSheet();

                prefs.edit().putBoolean("DarkNightModeOn", false).apply();
                prefs.edit().putBoolean("SepiaNightModeOn", false).apply();

                onNormalMode();
            }
        });
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                //Toast.makeText(mainActivity, "Sheet open", Toast.LENGTH_SHORT).show();

                Answers.getInstance().logCustom(new CustomEvent("Night Mode").putCustomAttribute("Story name", story.getStoryTitle()));

                // Called when the material sheet's "show" animation starts.
            }

            @Override
            public void onSheetShown() {
                // Called when the material sheet's "show" animation ends.
            }

            @Override
            public void onHideSheet() {
                // Called when the material sheet's "hide" animation starts.
            }

            public void onSheetHidden() {
                Toast.makeText(mainActivity, "Sheet close", Toast.LENGTH_SHORT).show();

                // Called when the material sheet's "hide" animation ends.
            }
        });


        View bottomSheet = view.findViewById(R.id.fragmentShortStory_storyWordMeaning_Cardview);
        behavior = BottomSheetBehavior.from(bottomSheet);


        NativeExpressAdView nativeExpressAdView =story.getNativeExpressAdView();
        if (nativeExpressAdView != null) {
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragmentShortStory_adView_linearLayout);
            linearLayout.removeAllViews();

            if (nativeExpressAdView.getParent() != null) {
                ((ViewGroup) nativeExpressAdView.getParent()).removeView(nativeExpressAdView);
            }

            linearLayout.addView(nativeExpressAdView);
        }
        return view;
    }

    private void stopReadingFullStory() {

        isTestToSpeechOn = false;
        tts.stop();
    }


    private void onDarkNightMode() {

        itemHolderCardview.setCardBackgroundColor(getResources().getColor(R.color.colorDarkBg));
        titleText.setTextColor(getResources().getColor(R.color.colorWhiteText));
        descriptionText.setTextColor(getResources().getColor(R.color.colorWhiteText));

        bookLinkText.setTextColor(getResources().getColor(R.color.colorAccent));
        bookNameText.setTextColor(getResources().getColor(R.color.colorLightWhiteText));
        storyAuthorNameText.setTextColor(getResources().getColor(R.color.colorLightWhiteText));
        storyTagText.setTextColor(getResources().getColor(R.color.colorLightWhiteText));
        storyGenreText.setTextColor(getResources().getColor(R.color.colorLightWhiteText));
        storyDateText.setTextColor(getResources().getColor(R.color.colorLightWhiteText));
        wordMeaningTextview.setTextColor(getResources().getColor(R.color.colorBlackText));


    }

    private void onNormalMode() {

        itemHolderCardview.setCardBackgroundColor(getResources().getColor(R.color.colorWhiteBg));
        titleText.setTextColor(getResources().getColor(R.color.colorPrimary));
        descriptionText.setTextColor(getResources().getColor(R.color.colorBlackText));

        bookLinkText.setTextColor(getResources().getColor(R.color.colorLightBlackText));
        bookNameText.setTextColor(getResources().getColor(R.color.colorLightBlackText));
        storyAuthorNameText.setTextColor(getResources().getColor(R.color.colorLightBlackText));
        storyTagText.setTextColor(getResources().getColor(R.color.colorLightBlackText));
        storyGenreText.setTextColor(getResources().getColor(R.color.colorLightBlackText));
        storyDateText.setTextColor(getResources().getColor(R.color.colorLightBlackText));
        wordMeaningTextview.setTextColor(getResources().getColor(R.color.colorBlackText));


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

    private void onSepiaNightMode() {
        itemHolderCardview.setCardBackgroundColor(getResources().getColor(R.color.colorSepiaBg));
        titleText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorSepiaText));
        // descriptionText.setTextColor(getResources().getColor(R.color.colorSepiaText));
        descriptionText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorSepiaText));


        bookLinkText.setTextColor(getResources().getColor(R.color.colorLightSepiaText));
        bookNameText.setTextColor(getResources().getColor(R.color.colorLightSepiaText));
        storyAuthorNameText.setTextColor(getResources().getColor(R.color.colorLightSepiaText));
        storyTagText.setTextColor(getResources().getColor(R.color.colorLightSepiaText));
        storyGenreText.setTextColor(getResources().getColor(R.color.colorLightSepiaText));
        storyDateText.setTextColor(getResources().getColor(R.color.colorLightSepiaText));
        wordMeaningTextview.setTextColor(getResources().getColor(R.color.colorBlackText));


    }

    private void init(String textToShow) {

        try {
            // FirebaseCrash.log("Showing spannable text");
            String definition = textToShow;
            descriptionText.setMovementMethod(LinkMovementMethod.getInstance());
            //descriptionText.setText(definition, TextView.BufferType.SPANNABLE);

            //formatting text
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                descriptionText.setText(Html.fromHtml(definition, Html.FROM_HTML_MODE_COMPACT), TextView.BufferType.SPANNABLE);
            } else {
                descriptionText.setText(Html.fromHtml(definition), TextView.BufferType.SPANNABLE);
            }
            //, TextView.BufferType.SPANNABLE

            Spannable spans = (Spannable) descriptionText.getText();
            BreakIterator iterator = BreakIterator.getWordInstance(Locale.UK);
            iterator.setText(definition);
            int start = iterator.first();
            for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator
                    .next()) {
                String possibleWord = definition.substring(start, end);
                if (Character.isLetterOrDigit(possibleWord.charAt(0))) {
                    ClickableSpan clickSpan = getClickableSpan(possibleWord);
                    spans.setSpan(clickSpan, start, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

        } catch (Exception e) {
            descriptionText.setText(textToShow);

        }

    }

    private ClickableSpan getClickableSpan(final String word) {
        return new ClickableSpan() {
            final String mWord;

            {
                mWord = word;
            }

            @Override
            public void onClick(View widget) {
                Log.d("tapped on:", mWord);


                onWordTap(mWord);

            }

            public void updateDrawState(TextPaint ds) {
                //ds.setUnderlineText(false);

               /* if (mWord.contentEquals(selectedWord)){
                    ds.setColor(ds.linkColor);
                }*/
                //ds.setColor(ds.linkColor);
                //super.updateDrawState(ds);
            }
        };
    }

    private void onWordTap(final String mWord) {


        speakOutWord(mWord);
        wordMeaningTextview.setText("Fetching Meaning for - " + mWord);
        StoryWordMeaning storyWordMeaning = new StoryWordMeaning(mWord, new StoryWordMeaning.OnWordMeaninglistener() {
            @Override
            public void onWordMeaningDownLoad(ArrayList<String> wordMeaning, boolean isSuccessful) {
                if (isSuccessful) {

                    wordMeaningTextview.setText(wordMeaning.get(0));
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    Log.d("meaning is ", wordMeaning.get(0));

                } else {
                    wordMeaningTextview.setText("No Meaning Found");

                    Answers.getInstance().logCustom(new CustomEvent("Meaning Failed").putCustomAttribute("word", mWord));

                }
            }
        });
        storyWordMeaning.execute();
        //  Translation translation = new Translation(mWord);
        //translation.fetchTranslation(this);

        //  translateText.setText(mWord);
        // selectedWord = mWord;
    }

    private void speakOutWord(final String speakWord) {

        tts = new TextToSpeech(mainActivity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                tts.setLanguage(Locale.UK);
                tts.speak(speakWord, TextToSpeech.QUEUE_FLUSH, null);
                tts.setSpeechRate(0.5f);

            }
        });


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

    private void speakOutFullStory() {

        isTestToSpeechOn = true;
        tts = new TextToSpeech(mainActivity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.UK);
                    tts.setSpeechRate(0.75f);
                    try {
                        tts.speak(story.getStoryFull(), TextToSpeech.QUEUE_FLUSH, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void openShareDialog(Uri shortUrl) {

        try {
            Answers.getInstance().logCustom(new CustomEvent("Share link created").putCustomAttribute("Content Id", story.getStoryID())
                    .putCustomAttribute("Shares", story.getStoryTitle()));
        }catch (Exception e){
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void showDialog() {
        progressDialog = new ProgressDialog(mainActivity);
        progressDialog.setMessage("Please wait..Creating link");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public void hideDialog() {
        progressDialog.cancel();
    }
}
