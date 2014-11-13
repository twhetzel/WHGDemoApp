package com.example.whetzel.whgdemoapp.whgdemoapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.content.Context;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;



public class MyActivity extends Activity {
    private static final String TAG = MyActivity.class.getSimpleName();

    // Api demo card - instructions
    static final int CARDS = 0;

    private CardScrollAdapter mAdapter;
    private CardScrollView mCardScroller;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display speech recognition screen
        displaySpeechRecognizer();
        mAdapter = new CardAdapter(createCards(this));
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(mAdapter);
        setContentView(mCardScroller);
        //setContentView(R.layout.activity_my);
    }


    //See: https://developers.google.com/glass/develop/gdk/input/voice#starting_glassware
    //See: http://developer.android.com/reference/android/speech/SpeechRecognizer.html
    final int SPEECH_REQUEST = 0;
    private void displaySpeechRecognizer() {
        //String prompt = "What word or phrase \nwould you like to translate?"; //Add newline tp help format text and not display over speech prompt
        String prompt = "Translate to (say language \nthen phrase to translate)";
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // http://developer.android.com/reference/android/speech/RecognizerIntent.html#EXTRA_PROMPT
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, prompt);
        startActivityForResult(intent, SPEECH_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        AsyncTask translate = null;
        if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            String spokenText = results.get(0);
            Log.d(TAG, "Speech Input: " + spokenText);
            //Call AsyncTask
            if (null == translate || translate.isCancelled()) {
                translate = new TranslateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, spokenText);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public class TranslateTask extends AsyncTask<String, Void, String> {
        private final String TAG = TranslateTask.class.getSimpleName();

        private String getTranslationFromJson(String results, String spokenText)
                throws JSONException {

            final String DATA = "data";
            final String TRANSLATIONS = "translations";
            final String TRANSLATED_TEXT = "translatedText";

            //final String translation = spokenText;
            JSONObject textTranslationJson = new JSONObject(results);
            Log.v(TAG, "JSON "+textTranslationJson);
            JSONObject data = textTranslationJson.getJSONObject(DATA);
            JSONObject translations = data.getJSONArray(TRANSLATIONS).getJSONObject(0);
            String translatedText = translations.getString(TRANSLATED_TEXT);

            return translatedText;
        }


        @Override
        protected String doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String spokenText = params[0];
            Log.v(TAG, "Spoken Text: " + spokenText);
            /*
            Get first word to use as language tag in web service call
             */
            String [] allWords = spokenText.split("[\\W]");
            String language = allWords[0];
            int offset = 1;
            String [] allButFirstWord = Arrays.copyOfRange(allWords,offset,allWords.length);
            //String textToTranslate = allButFirstWord.toString();
            StringBuilder builder = new StringBuilder();
            for(String s : allButFirstWord) {
                builder.append(s+' ');
            }
            String textToTranslate = builder.toString();

            Log.v(TAG,"First word: "+language);
            // Get language code based on language mentioned by user
            // Add check to make sure a match exists
            ArrayMap<String,String> languageMap = new ArrayMap();
            languageMap.put("Afrikaans","af");
            languageMap.put("Albanian","sq");
            languageMap.put("Arabic","ar");
            languageMap.put("Azerbaijani","az");
            languageMap.put("Basque","eu");
            languageMap.put("Bengali","bn");
            languageMap.put("Belarusian","be");
            languageMap.put("Bulgarian","bg");
            languageMap.put("Catalan","ca");
            languageMap.put("Chinese","zh-TW");
            languageMap.put("Croatian","hr");
            languageMap.put("Czech","cs");
            languageMap.put("Danish","da");
            languageMap.put("Dutch","nl");
            //languageMap.put("English","en"); //Can't use since source is coded as 'en' 11-12-2014
            languageMap.put("Esperanto","eo");
            languageMap.put("Estonian","et");
            languageMap.put("Filipino","tl");
            languageMap.put("Finnish","fi");
            languageMap.put("French","fr");
            languageMap.put("Galician","gl");
            languageMap.put("Georgian","ka");
            languageMap.put("German","de");
            languageMap.put("Greek","el");
            languageMap.put("Gujarati","gu");
            languageMap.put("Haitian","ht");
            languageMap.put("Hebrew","iw");
            //languageMap.put("Hindi","hi"); //characters are not displayed correctly
            languageMap.put("Hungarian","hu");
            languageMap.put("Icelandic","is");
            languageMap.put("Indonesian","id");
            languageMap.put("Irish","ga");
            languageMap.put("Italian","it");
            languageMap.put("Japanese","ja");
            languageMap.put("Kannada","kn");
            languageMap.put("Korean","ko");
            languageMap.put("Latin","la");
            languageMap.put("Norwegian","no");
            languageMap.put("Persian","fa");
            languageMap.put("Polish","pl");
            languageMap.put("Portuguese","pt");
            languageMap.put("Romanian","ro");
            languageMap.put("Russian","ru");
            languageMap.put("Swahili","sw");
            languageMap.put("Swedish","sv");
            languageMap.put("Thai","th");
            languageMap.put("Turkish","tr");
            languageMap.put("Ukrainian","uk");
            languageMap.put("Vietnamese","vi");
            languageMap.put("Yiddish","yi");
            languageMap.put("Spanish","es");


            // TODO Create method to get language tag outside of this section
            //String language_tag = getLanguageTag(language);

            // Test - get language tag
            String language_tag = languageMap.get(language);
            Log.v(TAG, "LanguageMap: "+language_tag);

            Log.v(TAG, "Text to translate: "+textToTranslate);

            final String KEY_PARAM ="key";
            final String API_KEY = "AIzaSyBOb_kV7YV9atkfYZrlkwNx54MRbCxEUGg";
            final String QUERY_PARAM = "q";
            final String SOURCE_PARAM = "source";
            final String SOURCE = "en";
            final String TARGET_PARAM = "target";
            //final String TARGET = "es"; //es for Spanish
            final String TARGET = language_tag; //dynamic language tag from user voice input
            String results = null;

            try {
                final String BASE_URL = "https://www.googleapis.com/language/translate/v2";
                //https://www.googleapis.com/language/translate/v2?key=INSERT-YOUR-KEY&q=hello%20world&source=en&target=de
                //https://www.googleapis.com/language/translate/v2?key=AIzaSyBOb_kV7YV9atkfYZrlkwNx54MRbCxEUGg&q=hello%20world&source=en&target=es
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(KEY_PARAM, API_KEY)
                        //.appendQueryParameter(QUERY_PARAM, spokenText)
                        .appendQueryParameter(QUERY_PARAM, textToTranslate)
                        .appendQueryParameter(SOURCE_PARAM, SOURCE)
                        .appendQueryParameter(TARGET_PARAM, TARGET)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(TAG, "Query URL: " + url.toString());

                // Create the request to Google Translate, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    results = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    results = null;
                }
                results = buffer.toString();
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                results = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getTranslationFromJson(results, spokenText);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "LOG onPostExecute " + result);
            processValue(result);
        }
    }


    private void getValue()
    {
        new TranslateTask().execute();
    }

    private void processValue(String myValue)
    {
        Log.d(TAG, "LOG processValue: "+myValue);
        //handle value
        Card card = new Card(this);
        card.setText(myValue);
        View cardView = card.getView();
        setContentView(cardView);
        //TODO: Read text on Card so it is also audible
    }


    // TODO: Remove unneeded code below
    /**
     * Create list of API demo cards. --> filler from template app
     */
    private List<Card> createCards(Context context) {
        ArrayList<Card> cards = new ArrayList<Card>();
        //cards.add(CARDS, new Card(context).setText(R.string.text_cards));

        return cards;
    }

}
