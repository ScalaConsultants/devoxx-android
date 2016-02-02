package com.devoxx.android.view.list.schedule;

import com.devoxx.android.adapter.schedule.model.TalksScheduleItem;
import com.devoxx.data.downloader.TracksDownloader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;
import org.androidannotations.annotations.res.IntegerRes;
import org.androidannotations.annotations.res.StringRes;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devoxx.R;

@EViewGroup(R.layout.list_item_talks_more)
public class TalksMoreItemView extends LinearLayout {

    @ViewById(R.id.list_item_talks_all)
    TextView title;

    @ViewById(R.id.list_item_talks_tracks)
    TextView track;

    @ViewById(R.id.list_item_talks_more_icon)
    ImageView trackIcon;

    @ViewById(R.id.list_item_talks_more)
    View openMore;

    @ViewById(R.id.list_item_talks_more_indicator_icon)
    View openMoreIcon;

    @ViewById(R.id.list_item_timespan_running_indicator)
    View runningIndicator;

    @DimensionPixelOffsetRes(R.dimen.activity_horizontal_margin)
    int paddingLr;

    @IntegerRes(android.R.integer.config_shortAnimTime)
    int toggleAnimTime;

    @Bean
    TracksDownloader tracksDownloader;

    @AfterViews
    void afterViews() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        setupBackground();
    }

    public void setupMore(TalksScheduleItem item, Runnable onOpenMoreAction) {
        openMore.setOnClickListener(v -> onOpenMoreAction.run());
        trackIcon.setOnClickListener(v -> onOpenMoreAction.run());
        setOnClickListener(v -> onOpenMoreAction.run());
        final int talksCount = item.talksCount();
        final int trackCount = item.tracksCount();
        title.setText(String.format(getResources().getQuantityString(R.plurals.item_talk_more_talk, talksCount), talksCount));
        track.setText(String.format(getResources().getQuantityString(R.plurals.item_talk_more_track, trackCount), trackCount));
        openMoreIcon.setScaleY(item.isOthersVisible() ? -1 : 1);
    }

    public void setRunIndicatorVisibility(TalksScheduleItem item) {
        final boolean shouldShowRunIndicator = item.isRunning() && !item.isOthersVisible();
        runningIndicator.setVisibility(shouldShowRunIndicator ? VISIBLE : INVISIBLE);
    }

    public void toggleIndicator() {
        openMoreIcon.clearAnimation();
        openMoreIcon.animate().scaleY(openMoreIcon.getScaleY() * -1)
                .setDuration(toggleAnimTime).start();
    }

    private void setupBackground() {
        int[] attrs = new int[]{android.R.attr.selectableItemBackground};
        TypedArray ta = getContext().getTheme().obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = ta.getDrawable(0);
        ta.recycle();
        setBackground(drawableFromTheme);
    }

    public TalksMoreItemView(Context context) {
        super(context);
    }

    public TalksMoreItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TalksMoreItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TalksMoreItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
