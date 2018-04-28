package com.vgs.android.vgs_cardform_demo;

import android.view.Menu;

public class AddNewCardFormActivity extends VGS_CardFormActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (mCardForm.isCardScanningAvailable()) {
            getMenuInflater().inflate(R.menu.card_io_dark, menu);
        }

        return true;
    }
}
