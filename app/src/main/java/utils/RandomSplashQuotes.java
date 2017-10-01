package utils;

import java.util.Random;

/**
 * Created by bunny on 20/09/17.
 */

public class RandomSplashQuotes {


    String[] quotes = new String[]{"\"Success is walking from failure to failure with no loss of enthusiasm.\"",
            "\"Success usually comes to those who are too busy to be looking for it.\"",
            "\"Great minds discuss ideas; average minds discuss events; small minds discuss people.\"",
            "\"Success is the sum of small efforts, repeated day in and day out.\"",
            "\"Keep your fears to yourself, but share your courage with others.\"",
            "\"Leadership is not about titles, positions, or flowcharts. It is about one life influencing another.\"",
            "\"If you really want the key to success, start by doing the opposite of what everyone else is doing.\"",
            "\"Whenever you see a successful business, someone once made a courageous decision.\"",
            "\"A good plan violently executed now is better than a perfect plan executed next week.\"",
            "\"You may have to fight a battle more than once to win it.\"",
            "\"The only way to do great work is to love what you do.\"",
            "\"Style is a reflection of your attitude and your personality.\"",
            "\"Personality is an unbroken series of successful gestures.\"",
            "\"Personality is to a man what perfume is to a flower.\""
    };

    public String randomQuote() {

        int random = (int) (Math.random() * (quotes.length-1));


        try {
            return quotes[random];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

}
