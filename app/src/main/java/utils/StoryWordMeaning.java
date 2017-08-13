package utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;

/**
 * Created by Aisha on 8/12/2017.
 */

public class StoryWordMeaning extends AsyncTask<Void, Void, String> {


    String mWord;
    OnWordMeaninglistener onWordMeaninglistener;

    public StoryWordMeaning(String word, OnWordMeaninglistener mOnWordMeaninglistener) {

        mWord = word;
        onWordMeaninglistener = mOnWordMeaninglistener;

    }

    public String getmWord() {
        return mWord;
    }

    public void setmWord(String mWord) {
        this.mWord = mWord;
    }

    protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
        InputStream in = entity.getContent();
        Log.d("My TAg", "doInBackground: done calling rest");


        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n > 0) {
            byte[] b = new byte[4096];
            n = in.read(b);


            if (n > 0) out.append(new String(b, 0, n));
        }


        return out.toString();
    }

    protected String doInBackground(Void... voids) {
        Log.d("My TAg", "doInBackground: calling rest");
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        String text = null;

        // HttpGet httpGet = new HttpGet("http://api.wordnik.com:80/v4/words.json/randomWords?hasDictionaryDef=true&minCorpusCount=0&minLength=5&maxLength=15&limit=1&api_key=8d93a189fb620cfa578070b02f8056778a640192bd39b10a4");
        try {

            HttpGet httpGet = new HttpGet("http://api.wordnik.com:80/v4/word.json/" + getmWord() + "/definitions?limit=10&includeRelated=true&useCanonical=false&includeTags=false&api_key=8d93a189fb620cfa578070b02f8056778a640192bd39b10a4");


            Log.d("My TAg", "doInBackground: going to call rest");
            HttpResponse response = httpClient.execute(httpGet, localContext);

            Log.d("My TAg", "doInBackground: done calling rest");
            HttpEntity entity = response.getEntity();
            text = getASCIIContentFromEntity(entity);

        } catch (Exception e) {
            Log.d("My TAg", "doInBackground: in catch");
            return e.getLocalizedMessage();
        }
        return text;

    }


    protected void onPostExecute(String results) {
        if (results != null) {
            try {
                Log.d("Tag", "onPostExecute: " + results);
                JSONArray jsonArray = new JSONArray(results);
                ArrayList<String> wordMeaningArraylist = new ArrayList<>();

                // setWord(jsonArray.getJSONObject(0).getString("word"));

                for (int i = 0; i < jsonArray.length(); i++) {


                    if (jsonArray.getJSONObject(i).isNull("text") == false) {

                        JSONObject jsonObj = jsonArray.getJSONObject(i);

                        if (jsonObj.getString("text") != null) {
                            wordMeaningArraylist.add(jsonObj.getString("text"));
                            Log.d("Meaning is ", jsonObj.getString("text"));
                        }

                    }
                    onWordMeaninglistener.onWordMeaningDownLoad(wordMeaningArraylist, true);

                }


            } catch (JSONException je) {
                onWordMeaninglistener.onWordMeaningDownLoad(null, false);

                je.printStackTrace();
            }
            Log.d("my text", "onPostExecute normal word meaning : Executed");
        } else {
            onWordMeaninglistener.onWordMeaningDownLoad(null, false);


            Log.d("my text", "onPostExecute meaning: failed to fetch meaning");
        }

    }

    //interface
    public interface OnWordMeaninglistener {

        public void onWordMeaningDownLoad(ArrayList<String> wordMeaning, boolean isSuccessful);

    }
}
