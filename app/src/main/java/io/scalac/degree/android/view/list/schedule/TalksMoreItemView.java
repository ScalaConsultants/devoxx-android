package io.scalac.degree.android.view.list.schedule;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;
import org.androidannotations.annotations.res.IntegerRes;
import org.androidannotations.annotations.res.StringRes;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.scalac.degree.android.adapter.schedule.model.TalksScheduleItem;
import io.scalac.degree.data.downloader.TracksDownloader;
import io.scalac.degree33.R;

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

    @DimensionPixelOffsetRes(R.dimen.activity_horizontal_margin)
    int paddingLr;

    @StringRes(R.string.item_talk_more_talks_placeholder)
    String talksPlaceholder;

    @StringRes(R.string.item_talk_more_tracks_placeholder)
    String tracksPlaceholder;

    @IntegerRes(android.R.integer.config_shortAnimTime)
    int toggleAnimTime;

    @Bean
    TracksDownloader tracksDownloader;

    @AfterViews
    void afterViews() {
        setOrientation(HORIZONTAL);
        setPadding(paddingLr, 0, paddingLr, paddingLr);
        setGravity(Gravity.CENTER_VERTICAL);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setupMore(TalksScheduleItem talksScheduleItem, Runnable onOpenMoreAction) {
        openMore.setOnClickListener(v -> onOpenMoreAction.run());
        setOnClickListener(v -> onOpenMoreAction.run());
        title.setText(String.format(talksPlaceholder, talksScheduleItem.talksCount()));
        track.setText(String.format(tracksPlaceholder, talksScheduleItem.tracksCount()));
        openMoreIcon.setScaleY(talksScheduleItem.isOthersVisible() ? -1 : 1);
    }

    public void toggleIndicator() {
        openMoreIcon.clearAnimation();
        openMoreIcon.animate().scaleY(openMoreIcon.getScaleY() * -1)
                .setDuration(toggleAnimTime).start();
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
