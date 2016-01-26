package io.scalac.degree.android.view.behaviour;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import io.scalac.degree.android.view.speaker.SpeakerDetailsHeader;
import io.scalac.degree33.R;

import static java.lang.Math.abs;
import static java.lang.Math.max;

@SuppressWarnings("unused")
public class SpeakerDetailsHeaderViewBehavior extends CoordinatorLayout.Behavior<SpeakerDetailsHeader> {

    private static final float FULL_FACTOR = 1f;
    private static final float MAX_ALPHA = 255f;
    private static final float TITLE_SCALE_FACTOR = 1.7f;
    private static final float DEFAULT_TITLE_SCALE_FACTOR = 1f;

    private Context mContext;

    private int mStartMarginLeft;
    private int mEndMargintLeft;
    private int mMarginRight;
    private int mStartMarginBottom;
    private boolean isHide;

    public SpeakerDetailsHeaderViewBehavior(Context context, AttributeSet attrs) {
        mContext = context;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, SpeakerDetailsHeader child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, SpeakerDetailsHeader child, View dependency) {
        shouldInitProperties(child, dependency);

        final int maxScroll = ((AppBarLayout) dependency).getTotalScrollRange();
        final float factor = abs(dependency.getY()) / (float) maxScroll;
        final float inverseFactor = FULL_FACTOR - factor;

        final View title = child.getTitle();

        // Sets bottom-left point as pivot.
        title.setPivotX(0);
        title.setPivotY(title.getMeasuredHeight());

        final float titleScaleFactor = max(DEFAULT_TITLE_SCALE_FACTOR, TITLE_SCALE_FACTOR * inverseFactor);
        title.setScaleY(titleScaleFactor);
        title.setScaleX(titleScaleFactor);

        child.applyImageBottomMargin((int) (mStartMarginLeft * inverseFactor));

        final int newImageAlpha = (int) (MAX_ALPHA * inverseFactor);
        child.setImageAlpha(newImageAlpha);

        final int imageHeight = child.getImageHeight();
        final float imageHeightWithFactor = imageHeight * factor;

        float calculatedChildPosition = dependency.getHeight()
                + dependency.getY()
                - child.getHeight() - imageHeightWithFactor
                - (getToolbarHeight() - child.getHeight() - imageHeightWithFactor) * factor / 2;


        calculatedChildPosition = calculatedChildPosition - mStartMarginBottom * inverseFactor;

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.leftMargin = (int) (factor * mEndMargintLeft) + mStartMarginLeft;
        lp.rightMargin = mMarginRight;
        child.setLayoutParams(lp);

        child.setY(calculatedChildPosition);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (isHide && factor < FULL_FACTOR) {
                child.setVisibility(View.VISIBLE);
                isHide = false;
            } else if (!isHide && factor == FULL_FACTOR) {
                child.setVisibility(View.GONE);
                isHide = true;
            }
        }
        return true;
    }

    private void shouldInitProperties(SpeakerDetailsHeader child, View dependency) {
        if (mStartMarginLeft == 0) {
            mStartMarginLeft = mContext.getResources().getDimensionPixelOffset(R.dimen.header_view_start_margin_left);
        }

        if (mEndMargintLeft == 0) {
            mEndMargintLeft = mContext.getResources().getDimensionPixelOffset(R.dimen.header_view_end_margin_left);
        }
        if (mStartMarginBottom == 0) {
            mStartMarginBottom = mContext.getResources().getDimensionPixelOffset(R.dimen.header_view_start_margin_bottom);
        }
        if (mMarginRight == 0) {
            mMarginRight = mContext.getResources().getDimensionPixelOffset(R.dimen.header_view_end_margin_right);
        }
    }

    public int getToolbarHeight() {
        int result = 0;
        TypedValue tv = new TypedValue();
        if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            result = TypedValue.complexToDimensionPixelSize(tv.data, mContext.getResources().getDisplayMetrics());
        }
        return result;
    }

}
