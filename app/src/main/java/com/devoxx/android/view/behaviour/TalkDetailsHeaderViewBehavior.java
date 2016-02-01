package com.devoxx.android.view.behaviour;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.devoxx.android.view.talk.TalkDetailsHeader;
import com.devoxx.R;
import com.devoxx.utils.DeviceUtil;
import com.devoxx.utils.DeviceUtil_;
import com.devoxx.utils.Logger;

@SuppressWarnings("unused")
public class TalkDetailsHeaderViewBehavior extends CoordinatorLayout.Behavior<TalkDetailsHeader> {

    private static final float FULL_FACTOR = 1f;

    private Context mContext;

    private int mStartMarginLeft;
    private int mEndMargintLeft;
    private int mMarginRight;
    private int mStartMarginBottom;
    private boolean isHide;
    private DeviceUtil deviceUtil;

    public TalkDetailsHeaderViewBehavior(Context context, AttributeSet attrs) {
        mContext = context;
        deviceUtil = DeviceUtil_.getInstance_(context);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, TalkDetailsHeader child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, TalkDetailsHeader child, View dependency) {
        shouldInitProperties(child, dependency);

        int maxScroll = ((AppBarLayout) dependency).getTotalScrollRange();
        float factor = Math.abs(dependency.getY()) / (float) maxScroll;

        float calculatedChildPosition = dependency.getHeight()
                + dependency.getY()
                - child.getHeight()
                - (getToolbarHeight() - child.getHeight()) * factor / 2;


        calculatedChildPosition = calculatedChildPosition - mStartMarginBottom * (FULL_FACTOR - factor);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.leftMargin = deviceUtil.isLandscapeTablet() ? mStartMarginLeft : (int) (factor * mEndMargintLeft) + mStartMarginLeft;
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

    private void shouldInitProperties(TalkDetailsHeader child, View dependency) {
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
