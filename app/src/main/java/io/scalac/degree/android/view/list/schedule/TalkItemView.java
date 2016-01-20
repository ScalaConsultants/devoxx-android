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

import io.scalac.degree.connection.model.BreakApiModel;
import io.scalac.degree.connection.model.TalkBaseApiModel;
import io.scalac.degree33.R;

@EViewGroup(R.layout.list_item_talk)
public class TalkItemView extends LinearLayout {

    @ViewById(R.id.list_item_title)
    TextView label;

    @AfterViews
    void afterViews() {
        setOrientation(VERTICAL);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
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

    public void setupTalk(TalkBaseApiModel talkBaseApiModel) {
        label.setText(talkBaseApiModel.title);
    }
}
