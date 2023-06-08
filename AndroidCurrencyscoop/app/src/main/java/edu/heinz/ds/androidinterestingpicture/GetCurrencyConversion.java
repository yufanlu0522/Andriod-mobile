package edu.heinz.ds.androidinterestingpicture;

import android.app.Activity;
import android.graphics.Bitmap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class GetCurrencyConversion {
    Currencyscoop cs = null;   // for callback
    String currencyCodeFrom = null;
    String currencyCodeTo = null;
    String currencyAmount = null;
    String date = null;          // returned from API
    String value = null;          // returned from API

    public void search(String currencyCodeFrom, String currencyCodeTo, String currencyAmount, Activity activity, Currencyscoop cs) {
        this.cs = cs;
        this.currencyCodeFrom = currencyCodeFrom;
        this.currencyCodeTo = currencyCodeTo;
        this.currencyAmount = currencyAmount;
        new GetCurrencyConversion.BackgroundTask(activity).execute();
    }

    private class BackgroundTask {

        private Activity activity; // The UI thread

        public BackgroundTask(Activity activity) {
            this.activity = activity;
        }

        private void startBackground() {
            new Thread(new Runnable() {
                public void run() {

                    doInBackground();
                    // This is magic: activity should be set to MainActivity.this
                    //    then this method uses the UI thread
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            onPostExecute();
                        }
                    });
                }
            }).start();
        }

        private void execute() {
            // There could be more setup here, which is why
            //    startBackground is not called directly
            startBackground();
        }

        // doInBackground( ) implements whatever you need to do on
        //    the background thread.
        // Implement this method to suit your needs
        private void doInBackground() {
            value = search(currencyCodeFrom, currencyCodeTo, currencyAmount);
        }

        // onPostExecute( ) will run on the UI thread after the background
        //    thread completes.
        // Implement this method to suit your needs
        public void onPostExecute() {
            cs.valueReady(value);
        }

        /*
         * Search Flickr.com for the searchTerm argument, and return a Bitmap that can be put in an ImageView
         */
        private String search(String currencyCodeFrom, String currencyCodeTo, String currencyAmount) {
            String value = null;

//             conn;
            int status = 0;
            Result result = new Result();
            HttpURLConnection conn = null;
            try {
//                String API_KEY = "b83a9e71e00630d1d29bfdb3b9099ce4";
                // GET wants us to pass the name on the URL line  75980
                URL url = new URL("https://morning-gorge-75980.herokuapp.com/getApiCurrencyscoop?currencyCodeFrom=" +
                        currencyCodeFrom + "&currencyCodeTo=" + currencyCodeTo +
                        "&currencyAmount=" + currencyAmount);
                System.out.println("url = " + url);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                // we are sending plain text
                conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                // tell the server what format we want back
                conn.setRequestProperty("Accept", "text/plain");

                // wait for response
                status = conn.getResponseCode();

                // set http response code
                result.setResponseCode(status);
                // set http response message - this is just a status message
                // and not the body returned by GET
                result.setResponseText(conn.getResponseMessage());

                System.out.println("Status:" + status);

                if (status == 200) {
                    String responseBody = getResponseBody(conn);
                    result.setResponseText(responseBody);
                    System.out.println("result.getResponseText()= " + result.getResponseText());
                    return result.getResponseText();///////////////
                } else if (status == 404) {
                    result.setResponseText("{from:\"\",to:\"\",amount:\"0\",result:\"API down!\"}");
                    return result.getResponseText();
                } else {
                    result.setResponseText("{from:\"\",to:\"\",amount:\"0\",result:\"Unexpected error\"}");
                    return result.getResponseText();
                }


            }
            // handle exceptions
            catch (MalformedURLException e) {
                System.out.println("URL Exception thrown" + e);
            } catch (IOException e) {
                System.out.println("IO Exception thrown" + e);
            } catch (Exception e) {
                System.out.println("IO Exception thrown" + e);
            } finally {
                conn.disconnect();
            }
            return null;
        }

        // Gather up a response body from the connection
        // and close the connection.
        private String getResponseBody(HttpURLConnection conn) {
            String responseText = "";
            try {
                String output = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));
                while ((output = br.readLine()) != null) {
                    responseText += output;
                }
                System.out.println("responseText " + responseText);
                conn.disconnect();
            } catch (IOException e) {
                System.out.println("Exception caught " + e);
            }
            return responseText;
        }
    }
}

// A simple class to wrap an RPC result.
class Result {
    private int responseCode;
    private String responseText;

    public int getResponseCode() { return responseCode; }
    public void setResponseCode(int code) { responseCode = code; }
    public String getResponseText() { return responseText; }
    public void setResponseText(String msg) { responseText = msg; }

    public String toString() { return responseCode + ":" + responseText; }
}
