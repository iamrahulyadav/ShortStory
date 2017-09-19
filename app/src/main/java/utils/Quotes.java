package utils;

import java.io.Serializable;

/**
 * Created by Aisha on 9/15/2017.
 */

public class Quotes implements Serializable {

    private String quotesFull;
    private String quotesAuthorName;
    private String quotesTag;
    private String quotesDate;
    private String quotesID;

    private int quotesLikes;
    boolean pushNotification;

    public String getQuotesImageAddress() {
        return quotesImageAddress;
    }

    public void setQuotesImageAddress(String quotesImageAddress) {
        this.quotesImageAddress = quotesImageAddress;
    }

    private String quotesImageAddress;



    public String getQuotesAuthorName() {
        return quotesAuthorName;
    }

    public void setQuotesAuthorName(String quotesAuthorName) {
        this.quotesAuthorName = quotesAuthorName;
    }

    public String getQuotesTag() {
        return quotesTag;
    }

    public void setQuotesTag(String quotesTag) {
        this.quotesTag = quotesTag;
    }

    public String getQuotesDate() {
        return quotesDate;
    }

    public void setQuotesDate(String quotesDate) {
        this.quotesDate = quotesDate;
    }

    public String getQuotesID() {
        return quotesID;
    }

    public void setQuotesID(String quotesID) {
        this.quotesID = quotesID;
    }

    public int getQuotesLikes() {
        return quotesLikes;
    }

    public void setQuotesLikes(int quotesLikes) {
        this.quotesLikes = quotesLikes;
    }

    public boolean isPushNotification() {
        return pushNotification;
    }

    public void setPushNotification(boolean pushNotification) {
        this.pushNotification = pushNotification;
    }

    public String getQuotesFull() {
        return quotesFull;
    }

    public void setQuotesFull(String quotesFull) {
        this.quotesFull = quotesFull;
    }
}
