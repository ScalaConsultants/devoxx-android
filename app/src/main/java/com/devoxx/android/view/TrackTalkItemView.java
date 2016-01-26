package com.devoxx.android.view;

import com.bumptech.glide.Glide;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devoxx.R;

@EViewGroup(R.layout.list_item_track)
public class TrackTalkItemView extends ForegroundLinearLayout {

    @ViewById(R.id.textTopic)
    TextView topicLabel;

    @ViewById(R.id.textSpeakers)
    TextView speakersLabel;

    @ViewById(R.id.textTime)
    TextView timeLabel;

    @ViewById(R.id.talkTrackIcon)
    ImageView icon;

    @DimensionPixelOffsetRes(R.dimen.activity_horizontal_margin)
    int paddingLr;

    @AfterViews
    void afterViews() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(paddingLr, 0, paddingLr, 0);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public TrackTalkItemView(Context context) {
        super(context);
    }

    public TrackTalkItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TrackTalkItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTitle(String titleValue) {
        topicLabel.setText(titleValue);
    }

    public void setIcon(String iconUrl) {
        Glide.with(getContext())
                .load(iconUrl)
                .placeholder(R.drawable.th_background)
                .error(R.drawable.no_photo)
                .fallback(R.drawable.no_photo)
                .into(icon);
    }

    public void setSpeakers(String speakers) {
        speakersLabel.setText(speakers);
    }

    public void setTime(String time) {
        timeLabel.setText(time);
    }
}
