package com.devoxx.android.view.selector;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.devoxx.R;
import com.devoxx.utils.FontUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;

import java.util.List;

@EViewGroup(R.layout.selector_values)
public class SelectorValues extends LinearLayout {

    @Bean
    FontUtils fontUtils;

    @ViewById(R.id.selectorValueLabel)
    TextView label;

    @ViewsById({R.id.selectorValueFirstNumber,
            R.id.selectorValueSecondNumber,
            R.id.selectorValueThirdNumber,
            R.id.selectorValueFourthNumber})
    List<TextView> numbers;

    @AfterViews
    void afterViews() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        fontUtils.applyTypeface(this, FontUtils.Font.REGULAR);
    }

    public void setupView(String labelVal, int numberVal) {
        final String numberAsString = String.format("%04d", numberVal);
        label.setText(labelVal);
        final String[] letters = numberAsString.split("");
        final List<String> lettersFiltered = Stream.of(letters)
                .filter(value -> !value.isEmpty()).collect(Collectors.toList());
        for (int i = lettersFiltered.size() - 1; i >= 0; i--) {
            final TextView textView = numbers.get(i);
            textView.setText(lettersFiltered.get(i));
            textView.setVisibility(View.VISIBLE);
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
