package edu.heinz.ds.androidinterestingpicture;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class Currencyscoop extends AppCompatActivity {
    Currencyscoop me = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
         * The click listener will need a reference to this object, so that upon successfully finding a picture from Flickr, it
         * can callback to this object with the resulting picture Bitmap.  The "this" of the OnClick will be the OnClickListener, not
         * this InterestingPicture.
         */
        final Currencyscoop ma = this;

        /*
         * Find the "submit" button, and add a listener to it
         */
        Button submitButton = (Button)findViewById(R.id.submit);

        Spinner currencyCodeFromDropdown = findViewById(R.id.currencyCodeFrom);
        String[] items = new String[]{"CNY", "USD", "EUR", "GBP", "AUD", "CAD", "JPY", "HKD", "INR", "ZAR", "TWD", "MOP", "KRW", "THB", "NZD", "SGD"};
        ArrayAdapter<String> currencyCodeFromAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        currencyCodeFromDropdown.setAdapter(currencyCodeFromAdapter);

        Spinner currencyCodeToDropdown = findViewById(R.id.currencyCodeTo);
        ArrayAdapter<String> currencyCodeToAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        currencyCodeToDropdown.setAdapter(currencyCodeToAdapter);

        // Add a listener to the send button
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                String currencyCodeFrom = currencyCodeFromDropdown.getSelectedItem().toString();
                String currencyCodeTo = currencyCodeToDropdown.getSelectedItem().toString();
                String currencyAmount = ((EditText)findViewById(R.id.currencyAmount)).getText().toString();

                System.out.println("currencyCodeFrom = " + currencyCodeFrom);
                System.out.println("currencyCodeTo = " + currencyCodeTo);
                System.out.println("currencyAmount = " + currencyAmount);

                if(currencyAmount.trim().equals("")){
                    TextView valueView = (TextView)findViewById(R.id.valueText);
                    valueView.setText("Please enter amount");
//                System.out.println("picture");
                    valueView.setVisibility(View.VISIBLE);
                    return;
                }
                GetCurrencyConversion gp = new GetCurrencyConversion();
                gp.search(currencyCodeFrom, currencyCodeTo, currencyAmount, me, ma); // Done asynchronously in another thread.  It calls ip.pictureReady() in this thread when complete.
            }
        });
    }

    /*
     * This is called by the GetPicture object when the picture is ready.  This allows for passing back the Bitmap picture for updating the ImageView
     */
    public void valueReady(String someJSON) {
        System.out.println("JSON" + someJSON);
        TextView valueView = (TextView)findViewById(R.id.valueText);
        Gson gson = new Gson();
        ResponseMessage incommingMsg = gson.fromJson(someJSON,ResponseMessage.class);
        String value = String.valueOf(incommingMsg.result);

        if (value != null) {
            valueView.setText(value);
//                System.out.println("picture");
            valueView.setVisibility(View.VISIBLE);
        } else {
//                valueView.setImageResource(R.mipmap.ic_launcher);
            System.out.println("No picture");
//                valueView.setVisibility(View.INVISIBLE);
        }
//        searchView.setText("");
//        pictureView.invalidate();
}
}
