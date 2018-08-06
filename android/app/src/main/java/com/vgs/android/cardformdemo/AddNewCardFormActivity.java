package com.vgs.android.cardformdemo;

import android.view.Menu;

public class AddNewCardFormActivity extends VGSCardFormActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (mCardForm.isCardScanningAvailable()) {
            getMenuInflater().inflate(R.menu.card_io_dark, menu);
        }

        return true;
    }
}
