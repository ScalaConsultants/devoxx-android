package com.devoxx.android.activity;

import com.devoxx.data.Settings_;
import com.devoxx.data.user.UserManager;
import com.devoxx.utils.InfoUtil;
import com.devoxx.utils.Logger;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import com.devoxx.R;
import com.google.android.gms.vision.barcode.Barcode;

import io.scalac.scanner.BarcodeCaptureActivity;

@EActivity(R.layout.activity_register_user)
public class RegisterUserActivity extends BaseActivity {

    private static final int RC_BARCODE_CAPTURE = 1578;

    @Bean
    InfoUtil infoUtil;

    @Bean
    UserManager userManager;

    @ViewById(R.id.registerUserinput)
    EditText codeInput;

    @Click(R.id.registerUserViaQr)
    void onScannerClick() {
        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
        startActivityForResult(intent, RC_BARCODE_CAPTURE);
    }

    @Click(R.id.registerUserResetCode)
    void onResetCodeClick() {
        userManager.clearCode();
        infoUtil.showToast("Code has been cleared!");
    }

    @Click(R.id.registerUserSaveCode)
    void onSaveClick() {
        final String input = codeInput.getText().toString();
        final String message;
        final boolean finishScreen;

        if (validateInput(input)) {
            userManager.saveUserCode(input);
            message = "Saved code! Now you can vote for a talk.";
            finishScreen = true;
        } else {
            message = "Code can't be empty!";
            finishScreen = false;
        }

        infoUtil.showToast(message);

        if (finishScreen) {
            finish();
        }
    }

    private boolean validateInput(String input) {
        return !TextUtils.isEmpty(input);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && data.hasExtra(BarcodeCaptureActivity.BarcodeObject)) {
            final Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
            final String code = barcode.displayValue;
            codeInput.setText(code);
            codeInput.setSelection(0, code.length());
        }
    }
}
