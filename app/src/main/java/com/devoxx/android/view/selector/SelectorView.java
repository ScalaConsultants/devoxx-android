package com.devoxx.android.view.selector;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.devoxx.R;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.res.IntegerRes;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

@EViewGroup
public class SelectorView extends FrameLayout implements View.OnClickListener, Animator.AnimatorListener {

    private static final int LAYOUTING_ARC_START_DEG = -90;
    private static final int FULL_CIRCLE_DEG = 360;

    @DimensionPixelOffsetRes(R.dimen.selector_circle_padding)
    int mainCirclePadding;

    @DimensionPixelOffsetRes(R.dimen.selector_circle_item_size)
    int itemCircleSize;

    @ColorRes(R.color.primary)
    int mainCircleColor;

    @ColorRes(R.color.degree_link)
    int pointCircleColor;

    @DrawableRes(R.drawable.selector_sub_menu_inactive_background)
    Drawable itemInactiveBackground;

    @DrawableRes(R.drawable.selector_sub_menu_active_background)
    Drawable itemActiveBackground;

    @IntegerRes(android.R.integer.config_mediumAnimTime)
    int rotateAnimationTime;

    private Paint mainCirclePaint;

    private int centerX, centerY;
    private int globalCircleRadius;
    private boolean animationGuard;

    @AfterInject
    void afterInject() {
        mainCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mainCirclePaint.setColor(mainCircleColor);
    }

    @AfterViews
    void afterViews() {
        setWillNotDraw(false);
    }

    public void addNewItem() {
        final int index = getChildCount();

        final View view = new View(getContext());
        view.setBackground(itemInactiveBackground);
        view.setOnClickListener(this);
        view.setTag(new ItemViewInfo(index, index == 0));

        addView(view, createLayoutParams());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /* Makes square of the view. */
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;

        globalCircleRadius = (int) (w / 2.5f - mainCirclePadding);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        final int size = getChildCount();

        int arcStart = LAYOUTING_ARC_START_DEG;
        int arcStep = calculateArcStep();

        for (int i = 0; i < size; i++) {
            final View child = getChildAt(i);
            final int chW = child.getMeasuredWidth();
            final int chH = child.getMeasuredHeight();
            final int hW = chW / 2;
            final int hH = chH / 2;

            final int newChX = (int) (centerX + globalCircleRadius * cos(toRadians(arcStart)));
            final int newChY = (int) (centerY + globalCircleRadius * sin(toRadians(arcStart)));

            child.layout(newChX - hW, newChY - hH, newChX + hW, newChY + hH);

            arcStart += arcStep;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMainCircle(canvas);
    }

    private void drawMainCircle(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, globalCircleRadius, mainCirclePaint);
    }

    public SelectorView(Context context) {
        super(context);
    }

    public SelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SelectorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onClick(View clickedView) {
        final ItemViewInfo clickedViewInfo = (ItemViewInfo) clickedView.getTag();
        if (animationGuard || clickedViewInfo.isActive()) {
            return;
        }

        final int clickedViewOldIndex = clickedViewInfo.getIndex();

        final int childCount = getChildCount();
        final int middleIndex = Math.round(childCount / 2);

        int steps;
        if (clickedViewOldIndex > middleIndex) { // Rotate clockwise.
            steps = childCount - clickedViewOldIndex;
        } else { // Rotate counter-clockwise.
            steps = clickedViewOldIndex * -1;
        }

        recalculateIndexes(clickedView, steps);

        clearAnimation();
        animate().rotation(getRotation() + calculateArcStep() * steps).setDuration(rotateAnimationTime)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(this)
                .start();
    }

    private void recalculateIndexes(View clickedView, int steps) {
        final boolean isClockwise = Math.signum(steps) == 1;

        final ItemViewInfo clickedViewInfo = (ItemViewInfo) clickedView.getTag();
        clickedViewInfo.setActive(true);
        clickedViewInfo.setIndex(0);

        final int size = getChildCount();

        for (int i = 0; i < size; i++) {
            final View child = getChildAt(i);
            if (!child.equals(clickedView)) {
                final ItemViewInfo viewInfo = (ItemViewInfo) child.getTag();
                viewInfo.setActive(false);

                final int currentIndex = viewInfo.getIndex();

                int newIndex;
                if (isClockwise) {
                    newIndex = (currentIndex + steps) % size;
                } else {
                    final int localIndex = currentIndex - Math.abs(steps);
                    if (localIndex < 0) {
                        newIndex = localIndex + size;
                    } else {
                        newIndex = localIndex;
                    }
                }
                viewInfo.setIndex(newIndex);

                child.setBackground(itemInactiveBackground);
            } else {
                child.setBackground(itemActiveBackground);
            }
        }
    }

    private int calculateArcStep() {
        return FULL_CIRCLE_DEG / getChildCount();
    }

    private LayoutParams createLayoutParams() {
        return new LayoutParams(itemCircleSize, itemCircleSize);
    }

    @Override
    public void onAnimationStart(Animator animation) {
        animationGuard = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        animationGuard = false;
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        // Nothing.
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        // Nothing.
    }

    private static class ItemViewInfo {
        private int index;
        private boolean isActive;

        public ItemViewInfo(int index, boolean isActive) {
            this.index = index;
            this.isActive = isActive;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }


        @Override
        public String toString() {
            return "ItemViewInfo{" +
                    "active=" + isActive() +
                    ", index=" + index +
                    ", isActive=" + isActive +
                    '}';
        }
    }
}
