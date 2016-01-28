package com.devoxx.android.view.selector;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devoxx.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.selector_item_view)
public class SelectorItemView extends FrameLayout {

    @ViewById(R.id.selectorItemLabel)
    TextView label;

    @ViewById(R.id.selectorItemIcon)
    ImageView icon;

    @AfterViews
    void afterViews() {
        setClipToPadding(false);
        setClipChildren(false);
    }

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

    public void setupIcon(int conferenceIcon) {
        Glide.with(getContext())
                .load(conferenceIcon)
                .fitCenter()
                .into(icon);
    }
}
