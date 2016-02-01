package com.devoxx.android.view.talk;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devoxx.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.talk_details_section_clickable_item)
public class TalkDetailsSectionClickableItem extends LinearLayout {

    @ViewById(R.id.talkDetailsSectionIcon)
    ImageView icon;

    @ViewById(R.id.talkDetailsSectionTitle)
    TextView title;

    @ViewById(R.id.talkDetailsSectionSpeakersContainer)
    LinearLayout speakers;

    @AfterViews
    void afterViews() {
        setOrientation(HORIZONTAL);
    }

    public void setupView(@DrawableRes int iconRes, @StringRes int titleResId) {
        icon.setImageResource(iconRes);
        title.setText(titleResId);
    }

    public void addSpeakerView(View view) {
        speakers.addView(view);
    }

    public LinearLayout getSpeakersContainer() {
        return speakers;
    }

    public TalkDetailsSectionClickableItem(Context context) {
        super(context);
    }

    public TalkDetailsSectionClickableItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TalkDetailsSectionClickableItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TalkDetailsSectionClickableItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
