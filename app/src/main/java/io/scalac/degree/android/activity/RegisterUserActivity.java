package io.scalac.degree.android.activity;

import android.content.Intent;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import io.scalac.degree.utils.Logger;
import io.scalac.degree33.R;
import io.scalac.scanner.BarcodeCaptureActivity;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 09/12/2015
 */
@EActivity(R.layout.activity_register_user)
public class RegisterUserActivity extends BaseActivity {

    private static final int RC_BARCODE_CAPTURE = 1578;

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
