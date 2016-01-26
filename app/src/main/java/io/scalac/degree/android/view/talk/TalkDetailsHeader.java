package io.scalac.degree.android.view.talk;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.scalac.degree33.R;

@EViewGroup(R.layout.fragment_talks_details_toolbar_include)
public class TalkDetailsHeader extends LinearLayout {

    @ViewById(R.id.header_view_title)
    TextView title;

    @ViewById(R.id.header_view_sub_title)
    TextView subtitle;

    @AfterViews
    void afterViews() {
        setOrientation(VERTICAL);
    }

    public void setupHeader(String titleVal, String subtitleVal) {
        title.setText(titleVal);
        subtitle.setText(subtitleVal);
    }

    public TalkDetailsHeader(Context context) {
        super(context);
    }

    public TalkDetailsHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TalkDetailsHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TalkDetailsHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
