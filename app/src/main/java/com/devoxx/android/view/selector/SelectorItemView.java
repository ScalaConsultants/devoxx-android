package com.devoxx.android.view.selector;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.devoxx.R;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.selector_item_view)
public class SelectorItemView extends FrameLayout {

    @ViewById(R.id.selectorItemLabel)
    TextView label;

    public void setupLabel(String labelVal) {
        label.setText(labelVal.substring(0, 1));
    }

    public SelectorItemView(Context context) {
        super(context);
    }

    public SelectorItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectorItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SelectorItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
