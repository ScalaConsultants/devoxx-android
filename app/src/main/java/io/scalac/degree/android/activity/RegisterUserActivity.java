package io.scalac.degree.android.activity;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import io.scalac.degree33.R;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 09/12/2015
 */
@EActivity(R.layout.activity_register_user)
public class RegisterUserActivity extends BaseActivity {

    @Click(R.id.registerUserViaNfc)
    void onNfcClick() {
        NfcScanningActivity_.intent(this).start();
    }
}
