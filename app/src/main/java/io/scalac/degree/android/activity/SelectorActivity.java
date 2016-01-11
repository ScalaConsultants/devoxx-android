package io.scalac.degree.android.activity;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

import io.scalac.degree.data.Settings_;
import io.scalac.degree33.R;

@EActivity(R.layout.activity_selector)
public class SelectorActivity extends BaseActivity {

    private static final String TEST_CONF_CODE = "DV15";

    @Pref
    Settings_ settings;

    @Click(R.id.selectorGo)
    void onGoClick() {
        settings.activeConferenceCode().put(TEST_CONF_CODE);
        MainActivity_.intent(this).start();
        finish();
    }
}
