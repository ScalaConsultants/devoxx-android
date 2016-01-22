package io.scalac.degree.android.view.list.schedule;

import com.bumptech.glide.Glide;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.connection.model.TalkBaseApiModel;
import io.scalac.degree.connection.model.TalkFullApiModel;
import io.scalac.degree.data.downloader.TracksDownloader;
import io.scalac.degree.data.manager.NotificationsManager;
import io.scalac.degree33.R;

@EViewGroup(R.layout.list_item_talk)
public class TalkItemView extends LinearLayout {

    @ViewById(R.id.list_item_talk_title)
    TextView title;

    @ViewById(R.id.list_item_talk_track)
    TextView track;

    @ViewById(R.id.list_item_talk_place)
    TextView place;

    @ViewById(R.id.list_item_talk_track_icon)
    ImageView trackIcon;

    @ViewById(R.id.list_item_talk_track_schedule)
    ImageView scheduleIcon;

    @DimensionPixelOffsetRes(R.dimen.activity_horizontal_margin)
    int paddingLr;

    @ColorRes(R.color.primary)
    int scheduledIndicatorColor;

    @Bean
    TracksDownloader tracksDownloader;

    @Bean
    NotificationsManager notificationsManager;

    @AfterViews
    void afterViews() {
        setOrientation(HORIZONTAL);
        setPadding(paddingLr, 0, paddingLr, paddingLr);
        setGravity(Gravity.CENTER_VERTICAL);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setupTalk(SlotApiModel slotModel) {
        if (slotModel.notAllocated) {
            // TODO Handle somehow free slots.
            title.setText("Free slot...");
            place.setText(slotModel.roomName);
            track.setText("");
            trackIcon.setImageDrawable(null);
        } else {
            final TalkFullApiModel talkModel = slotModel.talk;
            title.setText(talkModel.title);
            track.setText(talkModel.track);
            place.setText(slotModel.roomName);

            Glide.with(getContext())
                    .load(obtainTrackIconUrl(talkModel))
                    .placeholder(R.drawable.th_background)
                    .error(R.drawable.no_photo)
                    .fallback(R.drawable.no_photo)
                    .into(trackIcon);
        }

        if (notificationsManager.isNotificationScheduled(slotModel.slotId)) {
            scheduleIcon.setVisibility(View.VISIBLE);
            scheduleIcon.setColorFilter(scheduledIndicatorColor, PorterDuff.Mode.MULTIPLY);
        } else {
            scheduleIcon.setVisibility(View.GONE);
        }
    }

    private String obtainTrackIconUrl(TalkBaseApiModel slotModel) {
        return tracksDownloader.getTrackIconUrl(slotModel.track);
    }

    public TalkItemView(Context context) {
        super(context);
    }

    public TalkItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TalkItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TalkItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
