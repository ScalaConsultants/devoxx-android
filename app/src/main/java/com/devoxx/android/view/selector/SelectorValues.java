package com.devoxx.android.view.selector;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devoxx.R;
import com.devoxx.utils.FontUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.selector_values)
public class SelectorValues extends LinearLayout {

    @Bean
    FontUtils fontUtils;

    @ViewById(R.id.selectorValueLabel)
    TextView label;

    @ViewById(R.id.selectorValueFirstNumber)
    TextView first;

    @ViewById(R.id.selectorValueSecondNumber)
    TextView second;

    @ViewById(R.id.selectorValueThirdNumber)
    TextView third;

    @AfterViews
    void afterViews() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        fontUtils.applyTypeface(this, FontUtils.Font.REGULAR);
    }

    public void setupView(String labelVal, String numberVal) {
        label.setText(labelVal);
        final String[] letters = numberVal.split("");
        for (int i = letters.length - 1; i >= 1; i--) {
            if (i == 3) {
                third.setText(letters[i]);
            } else if (i == 2) {
                second.setText(letters[i]);
            } else if (i == 1) {
                first.setText(letters[i]);
            }
        }
    }

    public SelectorValues(Context context) {
        super(context);
    }

    public SelectorValues(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectorValues(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SelectorValues(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
