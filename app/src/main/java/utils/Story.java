package utils;

import java.io.Serializable;

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

    private int storyLikes;
    boolean pushNotification;


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


}
