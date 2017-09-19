package app.story.craftystudio.shortstory;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

import utils.FireBaseHandler;
import utils.Quotes;
import utils.Story;

/**
 * Created by Aisha on 9/15/2017.
 */

public class QuotesFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    static Main2ActivityQuotes main2ActivityQuotes;

    private Quotes quotes;

    TextView quoteText;
    TextView quoteAuthorNameText;
    TextView quoteTagText;
    TextView quoteDateText;
    ImageView displayQuotesImageImageview;
    Button likeButton;
    Button shareButton;

    ProgressDialog progressDialog;

    public static QuotesFragment newInstance(Quotes quotes, Main2ActivityQuotes context) {
        main2ActivityQuotes = context;
        QuotesFragment fragment = new QuotesFragment();

        Bundle args = new Bundle();

        args.putSerializable("Quotes", quotes);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quotes, container, false);
        //initializeView


        quoteText = (TextView) view.findViewById(R.id.fragmentQuotes_description_textView);
        quoteText.setText(quotes.getQuotesFull());
        //formatting text
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            quoteText.setText(Html.fromHtml(quotes.getQuotesFull(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            quoteText.setText(Html.fromHtml(quotes.getQuotesFull()));
        }

        quoteAuthorNameText = (TextView) view.findViewById(R.id.fragmentQuotes_authorname_textView);
        quoteAuthorNameText.setText("-" + quotes.getQuotesAuthorName());


        //display image
        displayQuotesImageImageview = (ImageView) view.findViewById(R.id.fragmentQuotes_image_imageview);
        if (quotes.getQuotesImageAddress() == null) {
            displayQuotesImageImageview.setVisibility(View.GONE);
        } else if (quotes.getQuotesImageAddress().isEmpty()) {
            displayQuotesImageImageview.setVisibility(View.GONE);

        } else {

            displayQuotesImage();
        }


        likeButton = (Button) view.findViewById(R.id.fragmentQuotes_likes_button);
        likeButton.setText(quotes.getQuotesLikes() + " Likes");
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeButton.setText(quotes.getQuotesLikes() + 1 + " Likes");
                uploadLike(quotes.getQuotesLikes() + 1);
                quotes.setQuotesLikes(quotes.getQuotesLikes() + 1);
            }
        });


        shareButton = (Button) view.findViewById(R.id.fragmentQuotes_share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShareClick();
            }
        });
        return view;

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.quotes = (Quotes) getArguments().getSerializable("Quotes");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ShortStoryFragment.OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void uploadLike(int currentLikes) {

        FireBaseHandler fireBaseHandler = new FireBaseHandler();

        fireBaseHandler.uploadQuotesLikes(quotes.getQuotesID(), currentLikes, new FireBaseHandler.OnLikeListener() {
            @Override
            public void onLikeUpload(boolean isSuccessful) {
                if (isSuccessful) {

                } else {
                    // storyLikesText.setText(story.getStoryLikes()+"");

                }
            }
        });

    }

    private void displayQuotesImage() {
        try {

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("quotesImage/" + quotes.getQuotesID() + "/" + "main");

            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .crossFade(100)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(displayQuotesImageImageview);

        } catch (Exception e) {
            e.printStackTrace();
            displayQuotesImageImageview.setVisibility(View.GONE);

        }

    }

    private void onShareClick() {

        showDialog();
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://goo.gl/UG9hPL"))
                .setDynamicLinkDomain("x87w4.app.goo.gl")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("app.story.craftystudio.shortstory")
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(quotes.getQuotesFull())
                                .setDescription("By " + quotes.getQuotesAuthorName())
                                .setImageUrl(Uri.parse(quotes.getQuotesImageAddress()))
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
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        //sharingIntent.putExtra(Intent.EXTRA_STREAM, newsMetaInfo.getNewsImageLocalPath());

        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "\" " + quotes.getQuotesFull()
                +  " \"\n\nRead More inspirational Quotes - " + shortUrl);
        startActivity(Intent.createChooser(sharingIntent, "Share Quotes via"));
        hideDialog();

    }

    public void showDialog() {
        progressDialog = new ProgressDialog(main2ActivityQuotes);
        progressDialog.setMessage("Please wait..Creating link");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public void hideDialog() {
        progressDialog.cancel();
    }

}
