package io.scalac.degree.android.view.list.schedule;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree33.R;

@EViewGroup(R.layout.list_item_break)
public class BreakItemView extends LinearLayout {

    @ViewById(R.id.list_item_break_label)
    TextView label;

    @ViewById(R.id.list_item_timespan)
    TextView timeLabel;

    @AfterViews
    void afterViews() {
        setOrientation(VERTICAL);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public BreakItemView(Context context) {
        super(context);
    }

    public BreakItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BreakItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BreakItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setupBreak(SlotApiModel breakApiModel) {
        label.setText(breakApiModel.slotBreak.nameEN);
        final String startString = TimespanItemView.formatTime(breakApiModel.fromTimeMillis);
        final String endString = TimespanItemView.formatTime(breakApiModel.toTimeMillis);
        timeLabel.setText(String.format(TimespanItemView.TIMESPAN_PLACEHOLDER, startString, endString));
    }

}
