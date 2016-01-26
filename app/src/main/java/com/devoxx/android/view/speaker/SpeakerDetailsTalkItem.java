package com.devoxx.android.view.speaker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devoxx.R;

@EViewGroup(R.layout.speaker_talk_section_item)
public class SpeakerDetailsTalkItem extends LinearLayout {

    @ViewById(R.id.speakerDetailsTalkSectionTitle)
    TextView title;

    @ViewById(R.id.speakerDetailsTalkSectionSubtitle)
    TextView subtitle;

    @AfterViews
    void afterViews() {
        setOrientation(VERTICAL);
        setupBackground();
    }

    public void setupView(String titleVal, String subtitleVal) {
        title.setText(titleVal);
        subtitle.setText(subtitleVal);
    }

    private void setupBackground() {
        int[] attrs = new int[]{android.R.attr.selectableItemBackground};
        TypedArray ta = getContext().getTheme().obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = ta.getDrawable(0);
        ta.recycle();
        setBackground(drawableFromTheme);
    }

    public SpeakerDetailsTalkItem(Context context) {
        super(context);
    }

    public SpeakerDetailsTalkItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpeakerDetailsTalkItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SpeakerDetailsTalkItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
