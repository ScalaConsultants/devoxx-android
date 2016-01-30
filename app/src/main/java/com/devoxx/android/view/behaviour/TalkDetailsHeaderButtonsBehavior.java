package com.devoxx.android.view.behaviour;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.devoxx.R;

public class TalkDetailsHeaderButtonsBehavior extends CoordinatorLayout.Behavior<LinearLayout> {

    private static final float HIDE_FACTOR = 0.4f;

    private final Context context;
    private boolean isHidden;
    private final int translationYOffset;

    public TalkDetailsHeaderButtonsBehavior(Context context, AttributeSet attrs) {
        this.context = context;
        translationYOffset = context.getResources().getDimensionPixelOffset(R.dimen.value_16dp);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency) {
        int maxScroll = ((AppBarLayout) dependency).getTotalScrollRange();
        float factor = Math.abs(dependency.getY()) / (float) maxScroll;

        float calculatedChildPosition = dependency.getHeight()
                + dependency.getY()
                - child.getHeight()
                - (getToolbarHeight() - child.getHeight()) * factor / 2
                + translationYOffset * 2;

        child.setY(calculatedChildPosition);

        if (isHidden && factor < HIDE_FACTOR) {
            showFabs(child, true);
            isHidden = false;
        } else if (!isHidden && factor > HIDE_FACTOR) {
            showFabs(child, false);
            isHidden = true;
        }

        return true;
    }

    private void showFabs(LinearLayout ll, boolean shoudlShow) {
        ll.clearAnimation();
        ll.animate().alpha(shoudlShow ? 1f : 0f).setDuration(200).start();
    }

    public int getToolbarHeight() {
        int result = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            result = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return result;
    }
}
