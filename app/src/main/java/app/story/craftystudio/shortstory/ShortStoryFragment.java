package app.story.craftystudio.shortstory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.speech.tts.TextToSpeech;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

import utils.Story;
import utils.StoryWordMeaning;

/**
 * Created by Aisha on 8/11/2017.
 */

public class ShortStoryFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Story story;

    TextView descriptionText;
    TextView wordMeaningTextview;

    static MainActivity mainActivity;
    TextToSpeech tts;


    public static ShortStoryFragment newInstance(Story story, MainActivity context) {
        mainActivity = context;
        ShortStoryFragment fragment = new ShortStoryFragment();
        Bundle args = new Bundle();
        args.putSerializable("Story", story);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.story = (Story) getArguments().getSerializable("Story");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_short_story, container, false);
        //initializeView


        TextView titleText = (TextView) view.findViewById(R.id.fragmentShortStory_title_textView);
        titleText.setText(story.getStoryTitle());

        descriptionText = (TextView) view.findViewById(R.id.fragmentShortStory_description_textView);
        init(story.getStoryFull());
        //descriptionText.setText(story.getStoryFull());

        wordMeaningTextview = (TextView) view.findViewById(R.id.fragmentShortStory_storyWordMeaning_textView);

        TextView bookNameText = (TextView) view.findViewById(R.id.fragmentShortStory_bookName_textView);
        bookNameText.setText(story.getStoryBookName());

        TextView bookLinkText = (TextView) view.findViewById(R.id.fragmentShortStory_bookLink_textView);
        bookLinkText.setText(story.getStoryBookLink());

        TextView storyAuthorNameText = (TextView) view.findViewById(R.id.fragmentShortStory_authorName_textView);
        storyAuthorNameText.setText(story.getStoryAuthorNAme());

        TextView storyGenreText = (TextView) view.findViewById(R.id.fragmentShortStory_genre_textView);
        storyGenreText.setText(story.getStoryGenre());

        TextView storyTagText = (TextView) view.findViewById(R.id.fragmentShortStory_tag_textView);
        storyTagText.setText(story.getStoryTag());

        TextView storyDateText = (TextView) view.findViewById(R.id.fragmentShortStory_storyDate_textView);
        storyDateText.setText(story.getStoryDate());

        Button shareStoryButton = (Button) view.findViewById(R.id.fragmentShortStory_storyShareLink_Button);
        shareStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShareClick();
            }
        });

        Button readFullStoryButton = (Button) view.findViewById(R.id.fragmentShortStory_storyReadFull_Button);
        readFullStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakOutFullStory();
            }
        });


        return view;
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
