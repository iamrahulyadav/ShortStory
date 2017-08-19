package app.story.craftystudio.shortstory;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
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
        View view = inflater.inflate(R.layout.fragment_short_story, container, false);
        //initializeView


        titleText = (TextView) view.findViewById(R.id.fragmentShortStory_title_textView);
        titleText.setText(story.getStoryTitle());

        descriptionText = (TextView) view.findViewById(R.id.fragmentShortStory_description_textView);
        init(story.getStoryFull());

        //get wordmeaning of word tap
        wordMeaningTextview = (TextView) view.findViewById(R.id.fragmentShortStory_storyWordMeaning_textView);

        //Name of a book
        bookNameText = (TextView) view.findViewById(R.id.fragmentShortStory_bookName_textView);
        bookNameText.setText("Book Name -" + story.getStoryBookName());

        //link of book to buy
        bookLinkText = (TextView) view.findViewById(R.id.fragmentShortStory_bookLink_textView);
        bookLinkText.setText("Get Book From -" + story.getStoryBookLink());

        //name of Author
        storyAuthorNameText = (TextView) view.findViewById(R.id.fragmentShortStory_authorName_textView);
        storyAuthorNameText.setText("by " + story.getStoryAuthorNAme());

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
        storyLikesText.setText(story.getStoryLikes() + "");


        //speak full story
        ImageView readFullStoryButton = (ImageView) view.findViewById(R.id.fragmentShortStory_storyReadFull_Button);
        readFullStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakOutFullStory();
            }
        });

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

                    storyLikesText.setText(story.getStoryLikes() + "");
                    Toast.makeText(mainActivity, "ThankYou! for liking the story", Toast.LENGTH_SHORT).show();
                    uploadLike(story.getStoryLikes());
                }
            }
        });

        final ShineButton shineShareButtonJava = (ShineButton) view.findViewById(R.id.share_shine_button);

        shineShareButtonJava.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean checked) {
                if (checked) {
                    Toast.makeText(mainActivity, "ThankYou! for Sharing the story", Toast.LENGTH_SHORT).show();
                    onShareClick();
                }
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
                Toast.makeText(mainActivity, "Sheet open", Toast.LENGTH_SHORT).show();
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


        return view;
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
        titleText.setTextColor(getResources().getColor(R.color.colorBlueTitle));
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
            descriptionText.setText(definition, TextView.BufferType.SPANNABLE);

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

    private void onWordTap(String mWord) {

        speakOutWord(mWord);

        StoryWordMeaning storyWordMeaning = new StoryWordMeaning(mWord, new StoryWordMeaning.OnWordMeaninglistener() {
            @Override
            public void onWordMeaningDownLoad(ArrayList<String> wordMeaning, boolean isSuccessful) {
                if (isSuccessful) {
                    wordMeaningTextview.setText(wordMeaning.get(0));
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    Log.d("meaning is ", wordMeaning.get(0));

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

            }
        });


    }


    private void onShareClick() {

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://goo.gl/Ae4Mhw?storyID=" + story.getStoryID()))
                .setDynamicLinkDomain("x87w4.app.goo.gl")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("app.story.craftystudio.shortstory")
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(story.getStoryTitle())
                                .setDescription(story.getStoryBookName() + " by " + story.getStoryAuthorNAme())
                                .setImageUrl(Uri.parse("https://play.google.com/store/apps/details?id=app.craftystudio.vocabulary.dailyeditorial"))
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
        tts = new TextToSpeech(mainActivity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                tts.setLanguage(Locale.UK);
                tts.speak(story.getStoryFull(), TextToSpeech.QUEUE_FLUSH, null);

            }
        });


    }

    private void openShareDialog(Uri shortUrl) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        //sharingIntent.putExtra(Intent.EXTRA_STREAM, newsMetaInfo.getNewsImageLocalPath());

        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shortUrl
                + "\n\nRead Full Story");
        startActivity(Intent.createChooser(sharingIntent, "Share Story via"));

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

}
