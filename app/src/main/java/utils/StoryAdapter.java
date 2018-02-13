package utils;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdViewAttributes;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import app.story.craftystudio.shortstory.R;

/**
 * Created by bunny on 25/12/17.
 */

public class StoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Context context;
    private List<Story> storyArrayList;

    private static final int STORY_VIEW_TYPE = 1;
    private static final int AD_VIEW_TYPE = 2;

    ClickListener clickListener;
    private int TYPE_NATIVE_AD = 1;

    public class StoryViewHolder extends RecyclerView.ViewHolder {
        public TextView title, dateTextView;
        View containerView;
        LinearLayout adViewContainer;
        ImageView imageView;

        public StoryViewHolder(final View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.storyAdapter_title_textView);
            imageView = (ImageView) view.findViewById(R.id.storyAdapter_image_imageView);
            dateTextView = (TextView) view.findViewById(R.id.storyAdapter_date_textView);

            containerView = view.findViewById(R.id.storyAdapter_container);

            containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (clickListener != null) {
                        clickListener.onItemClick(v, getAdapterPosition());
                    }

                }
            });

            adViewContainer= (LinearLayout) view.findViewById(R.id.storyAdapter_adView_container);


        }
    }

    public StoryAdapter(List<Story> storyArrayList, Context context) {
        this.storyArrayList = storyArrayList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {

            case AD_VIEW_TYPE:
               /* View nativeExpressLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.native_express_ad_container, parent, false);
                return new NativeExpressAdViewHolder(nativeExpressLayoutView);
*/
            case STORY_VIEW_TYPE:
            default:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.storyadapter_row_layout, parent, false);


                return new StoryViewHolder(itemView);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        StoryViewHolder storyViewHolder = (StoryViewHolder) holder;
        Story story = (Story) storyArrayList.get(position);


        if (story.getObjectType() == TYPE_NATIVE_AD) {

            /*storyViewHolder.adViewContainer.setVisibility(View.VISIBLE);
            storyViewHolder.containerView.setVisibility(View.GONE);

            NativeAdViewAttributes viewAttributes = new NativeAdViewAttributes()
                    .setBackgroundColor(Color.LTGRAY)
                    .setButtonTextColor(Color.WHITE);


            View adView = NativeAdView.render(context, story.getNativeAd(), NativeAdView.Type.HEIGHT_400, viewAttributes);

            storyViewHolder.adViewContainer.removeAllViews();
            storyViewHolder.adViewContainer.addView(adView);*/


        } else {
            storyViewHolder.adViewContainer.removeAllViews();

            storyViewHolder.containerView.setVisibility(View.VISIBLE);
            storyViewHolder.adViewContainer.setVisibility(View.GONE);

            storyViewHolder.title.setText(story.getStoryTitle());

            if (story.getStoryImageAddress() == null) {
                storyViewHolder.imageView.setVisibility(View.GONE);
            } else if (story.getStoryImageAddress().isEmpty()) {
                storyViewHolder.imageView.setVisibility(View.GONE);

            } else {
                try {

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("shortStoryImage/" + story.getStoryID() + "/" + "main");

                    Glide.with(context)
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .crossFade(100)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(storyViewHolder.imageView);

                    storyViewHolder.imageView.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                    storyViewHolder.imageView.setVisibility(View.GONE);

                }


            }

            storyViewHolder.dateTextView.setText(story.getStoryDate());
        }


    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return storyArrayList.size();
    }


    @Override
    public int getItemViewType(int position) {
        //logic to implement ad in every 8th card

        //return (position % 8 == 0) ? AD_VIEW_TYPE : EDITORIAL_VIEW_TYPE;
        return STORY_VIEW_TYPE;

    }


    public interface ClickListener {

        public void onItemClick(View view, int position);
    }

}
