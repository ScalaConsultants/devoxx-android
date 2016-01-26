package com.devoxx.android.view.list.schedule;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

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

    @ViewById(R.id.list_item_timespan)
    TextView label;

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

    public void setupTimespan(long timeStart, long timeEnd) {
        final String startString = formatTime(timeStart);
        final String endString = formatTime(timeEnd);
        label.setText(String.format(TIMESPAN_PLACEHOLDER, startString, endString));
    }

    public static String formatTime(long time) {
        return TIME_FORMAT.format(time);
    }
}
