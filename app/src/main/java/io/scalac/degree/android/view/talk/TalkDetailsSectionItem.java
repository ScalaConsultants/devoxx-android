package io.scalac.degree.android.view.talk;

import com.bumptech.glide.Glide;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.scalac.degree33.R;

@EViewGroup(R.layout.talk_details_section_item)
public class TalkDetailsSectionItem extends LinearLayout {

    @ViewById(R.id.talkDetailsSectionIcon)
    ImageView icon;

    @ViewById(R.id.talkDetailsSectionTitle)
    TextView title;

    @ViewById(R.id.talkDetailsSectionSubtitle)
    TextView subtitle;

    @AfterViews
    void afterViews() {
        setOrientation(HORIZONTAL);
    }

    public void setupView(String iconUrl, @StringRes int titleResId, String subtitleVal) {
        // TODO Proper icon!
        Glide.with(getContext()).load(R.drawable.ic_star_grey600_18dp)
                .fallback(R.drawable.ic_star_grey600_18dp).into(icon);
        title.setText(titleResId);
        subtitle.setText(subtitleVal);
    }

    public TalkDetailsSectionItem(Context context) {
        super(context);
    }

    public TalkDetailsSectionItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TalkDetailsSectionItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TalkDetailsSectionItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
