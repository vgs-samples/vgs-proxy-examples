package com.vgs.android.cardformdemo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.braintreepayments.cardform.view.SupportedCardTypesView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import static com.vgs.android.cardformdemo.CardStorageContract.CardEntry.CARDS_CARDIDENTIFIER;
import static com.vgs.android.cardformdemo.CardStorageContract.CardEntry.CARDS_CARDTYPE;
import static com.vgs.android.cardformdemo.CardStorageContract.CardEntry.CARDS_CCN;
import static com.vgs.android.cardformdemo.CardStorageContract.CardEntry.CARDS_COUNTRYCODE;
import static com.vgs.android.cardformdemo.CardStorageContract.CardEntry.CARDS_CVV;
import static com.vgs.android.cardformdemo.CardStorageContract.CardEntry.CARDS_MOBILE;
import static com.vgs.android.cardformdemo.CardStorageContract.CardEntry.CARDS_MONTH;
import static com.vgs.android.cardformdemo.CardStorageContract.CardEntry.CARDS_POST_CODE;
import static com.vgs.android.cardformdemo.CardStorageContract.CardEntry.CARDS_YEAR;


public class VGSCardFormActivity extends AppCompatActivity implements OnCardFormSubmitListener,
        CardEditText.OnCardTypeChangedListener {

    private static final CardType[] SUPPORTED_CARD_TYPES = {CardType.VISA, CardType.MASTERCARD, CardType.DISCOVER,
            CardType.AMEX, CardType.DINERS_CLUB, CardType.JCB, CardType.MAESTRO, CardType.UNIONPAY};

    private SupportedCardTypesView mSupportedCardTypesView;

    protected CardForm mCardForm;

    //open a SQLite DB for local storage:
    CardStorageDBHelper mDbHelper = new CardStorageDBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_form);

        mSupportedCardTypesView = findViewById(R.id.supported_card_types);
        mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);

        mCardForm = findViewById(R.id.card_form);
        mCardForm.cardRequired(true)
                .maskCardNumber(true)
                .maskCvv(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(true)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("Make sure SMS is enabled for this mobile number")
                .actionLabel(getString(R.string.purchase))
                .setup(this);

        mCardForm.setOnCardFormSubmitListener(this);
        mCardForm.setOnCardTypeChangedListener(this);

        // Warning: this is for development purposes only and should never be done outside of this example app.
        // Failure to set FLAG_SECURE exposes your app to screenshots allowing other apps to steal card information.
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    public void onCardTypeChanged(CardType cardType) {
        if (cardType == CardType.EMPTY) {
            mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
        } else {
            mSupportedCardTypesView.setSelected(cardType);
        }
    }

    @Override
    public void onCardFormSubmit() {


        URL url = null;
        String mbe_endpoint = this.getString(R.string.mbe_endpoint);
        try {
            //todo: get this property from another means
            url = new URL(this.getString(R.string.proxy_url));
        } catch (MalformedURLException e) {
            Log.e(this.getClass().getName(),e.getLocalizedMessage());;
        }

        if (mCardForm.isValid()) {

            JSONObject card = new JSONObject();
            try {
                card.put("CCN", mCardForm.getCardNumber());
                card.put("CVV", mCardForm.getCvv());
                card.put("MONTH", mCardForm.getExpirationMonth());
                card.put("YEAR", mCardForm.getExpirationYear());
                card.put("POST_CODE", mCardForm.getPostalCode());
                card.put("COUNTRYCODE", mCardForm.getCountryCode());
                card.put("MOBILE", mCardForm.getMobileNumber());
                card.put("CARDTYPE", mCardForm.getCardEditText().getCardType().toString());

            } catch (JSONException e) {
                Log.e(this.getClass().getName(),e.getLocalizedMessage());;
            }

            // Mobile Backend Service with VGS Proxy re-write rule: MobileBackEndClient(url)
            MobileBackEndClient api = new MobileBackEndClient(url, mbe_endpoint); //URL reflects VGS proxy
            api.persistSensitive(card.toString(), new MobileBeUICallback() {
                @Override
                public void onSuccess(String token) {

                    // Parse the result from VGS proxy
                    JSONObject jObject = new JSONObject();
                    try {
                        jObject = new JSONObject(token);
                    } catch (JSONException e) {
                        Log.e(this.getClass().getName(),e.getLocalizedMessage());;
                    }

                    // Gets the data repository in write mode
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    Log.i(this.getClass().getName(), "db path" + db.getPath());

                    // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(CARDS_CARDIDENTIFIER, 1);
                    values.put(CARDS_CARDTYPE, mCardForm.getCardEditText().getCardType().toString());

                    try {
                        values.put(CARDS_CCN, jObject.getString("CCN"));
                    } catch (JSONException e) {
                        Log.e(this.getClass().getName(),e.getLocalizedMessage());;
                    }

                    try {
                        values.put(CARDS_CVV, jObject.getString("CVV"));
                    } catch (JSONException e) {
                        Log.e(this.getClass().getName(),e.getLocalizedMessage());;
                    }

                    try {
                        values.put(CARDS_MONTH, jObject.getString("MONTH"));
                    } catch (JSONException e) {
                        Log.e(this.getClass().getName(),e.getLocalizedMessage());;
                    }

                    try {
                        values.put(CARDS_YEAR, jObject.getString("YEAR"));
                    } catch (JSONException e) {
                        Log.e(this.getClass().getName(),e.getLocalizedMessage());;
                    }

                    try {
                        values.put(CARDS_POST_CODE, jObject.getString("POST_CODE"));
                    } catch (JSONException e) {
                        Log.e(this.getClass().getName(),e.getLocalizedMessage());;
                    }

                    try {
                        values.put(CARDS_COUNTRYCODE, jObject.getString("COUNTRYCODE"));
                    } catch (JSONException e) {
                        Log.e(this.getClass().getName(),e.getLocalizedMessage());;
                    }

                    try {
                        values.put(CARDS_MOBILE, jObject.getString("MOBILE"));
                    } catch (JSONException e) {
                        Log.e(this.getClass().getName(),e.getLocalizedMessage());;
                    }

                    // Insert the new row, returning the primary key value of the new row
                    long newRowId = db.insert("card", null, values);

                    // Print the result locally
                    Toast.makeText(mCardForm.getContext(), token, Toast.LENGTH_LONG).show();


                }

                @Override
                public void onFailure(MobileBeError error) {
                    Toast.makeText(mCardForm.getContext(), error.toString(), Toast.LENGTH_SHORT).show();

                }
            });

        } else {
            // mCardForm.validate(); do someting as a fallback?
            Toast.makeText(this, R.string.invalid, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.card_io_item) {
            mCardForm.scanCard(this);
            return true;
        }

        return false;
    }
}
