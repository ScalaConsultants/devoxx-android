package com.devoxx.android.activity;

import com.devoxx.utils.Logger;

import android.content.Intent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import com.devoxx.R;

import io.scalac.scanner.BarcodeCaptureActivity;

@EActivity(R.layout.activity_register_user)
public class RegisterUserActivity extends BaseActivity {

    private static final int RC_BARCODE_CAPTURE = 1578;

    @AfterViews
    void afterViews() {
        super.afterViews();
    }

    @Click(R.id.registerUserViaNfc)
    void onNfcClick() {
        NfcScanningActivity_.intent(this).start();
    }

    @Click(R.id.registerUserViaQr)
    void onScannerClick() {
        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
        startActivityForResult(intent, RC_BARCODE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.l("onActivityResult: " + data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
