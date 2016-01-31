package com.devoxx.android.view.list.schedule;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.devoxx.R;

@EViewGroup(R.layout.list_item_timespan)
public class TimespanItemView extends LinearLayout {

    private static final String TIME_FORMAT_RAW = "HH:mm";
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(
            TIME_FORMAT_RAW, Locale.getDefault());
    public static final String TIMESPAN_PLACEHOLDER = "%s-%s";
    public static final String RUNNING_TIMESPAN_PLACEHOLDER = "NOW: %s to %s";

    @ViewById(R.id.list_item_timespan)
    TextView label;

    @ColorRes(R.color.primary)
    int notRunningTimespanColor;

    @ColorRes(R.color.running_timespan)
    int runningTimespanColor;

    public TimespanItemView(Context context) {
        super(context);
    }

    public TimespanItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimespanItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimespanItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setupTimespan(long timeStart, long timeEnd, boolean running) {
        final String startString = formatTime(timeStart);
        final String endString = formatTime(timeEnd);
        if (running) {
            label.setText(String.format(RUNNING_TIMESPAN_PLACEHOLDER, startString, endString));
            label.setTextColor(runningTimespanColor);
        } else {
            label.setText(String.format(TIMESPAN_PLACEHOLDER, startString, endString));
            label.setTextColor(notRunningTimespanColor);
        }
    }

    public static String formatTime(long time) {
        return TIME_FORMAT.format(time);
    }
}
