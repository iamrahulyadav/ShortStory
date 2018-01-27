package utils;

import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.NativeExpressAdView;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Aisha on 8/11/2017.
 */

public class Story implements Serializable {
    private String storyTitle;
    private String storyFull;
    private String storyAuthorNAme;
    private String storyBookName;
    private String storyBookLink;
    private String storyGenre;
    private String storyTag;
    private String storyDate;
    private String storyID;


    private String storyImageAddress;
    private int storyLikes , objectType;
    boolean pushNotification;

    transient NativeExpressAdView nativeAd;

    public String getStoryImageAddress() {
        return storyImageAddress;
    }

    public void setStoryImageAddress(String storyImageAddress) {
        this.storyImageAddress = storyImageAddress;
    }


    public boolean isPushNotification() {
        return pushNotification;
    }

    public void setPushNotification(boolean pushNotification) {
        this.pushNotification = pushNotification;
    }


    public int getStoryLikes() {
        return storyLikes;
    }

    public void setStoryLikes(int storyLikes) {
        this.storyLikes = storyLikes;
    }


    public String getStoryID() {
        return storyID;
    }

    public void setStoryID(String storyID) {
        this.storyID = storyID;
    }


    public String getStoryTitle() {
        return storyTitle;
    }

    public void setStoryTitle(String storyTitle) {
        this.storyTitle = storyTitle;
    }

    public String getStoryAuthorNAme() {
        return storyAuthorNAme;
    }

    public void setStoryAuthorNAme(String storyAuthorNAme) {
        this.storyAuthorNAme = storyAuthorNAme;
    }

    public String getStoryFull() {
        return storyFull;
    }

    public void setStoryFull(String storyFull) {
        this.storyFull = storyFull;
    }

    public String getStoryBookName() {
        return storyBookName;
    }

    public void setStoryBookName(String storyBookName) {
        this.storyBookName = storyBookName;
    }

    public String getStoryBookLink() {
        return storyBookLink;
    }

    public void setStoryBookLink(String storyBookLink) {
        this.storyBookLink = storyBookLink;
    }

    public String getStoryGenre() {
        return storyGenre;
    }

    public void setStoryGenre(String storyGenre) {
        this.storyGenre = storyGenre;
    }

    public String getStoryTag() {
        return storyTag;
    }

    public void setStoryTag(String storyTag) {
        this.storyTag = storyTag;
    }

    public String getStoryDate() {
        return storyDate;
    }

    public void setStoryDate(String storyDate) {
        this.storyDate = storyDate;
    }

    public NativeExpressAdView getNativeAd() {
        return nativeAd;
    }

    public void setNativeAd(NativeExpressAdView nativeAd) {
        this.nativeAd = nativeAd;
    }

    public int getObjectType() {
        return objectType;
    }

    public void setObjectType(int objectType) {
        this.objectType = objectType;
    }

    public static String resolveDate(long editorialTime) {

        Calendar calendar = Calendar.getInstance();


        long currenttime = calendar.getTimeInMillis();


        //calculate difference in time
        //long timeDifference = (currenttime - newsTime);

        if ((currenttime - editorialTime) <= 0 || editorialTime <= 1493013649175l) {
            return "";
        }

        long numberOfHour = (currenttime - editorialTime) / 3600000;
        if (numberOfHour == 0) {
            return "just now";
        } else if (numberOfHour < 24) {
            return String.valueOf(numberOfHour) + " hour ago";
        } else {

            long numberOfDays = numberOfHour / 24;

            if (numberOfDays < 7) {
                return String.valueOf(numberOfDays) + " day ago";
            } else {

                long numberOfWeek = numberOfDays / 7;
                if (numberOfWeek <= 4) {
                    return String.valueOf(numberOfWeek) + " week ago";
                } else {

                    long numberOfMonth = numberOfWeek / 4;
                    if (numberOfMonth <= 12) {
                        return String.valueOf(numberOfMonth) + " month ago";
                    } else {

                        long numberOfYear = numberOfMonth / 12;

                        return String.valueOf(numberOfYear) + " year ago";

                    }

                }

            }

        }


    }


}
