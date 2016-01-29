package com.devoxx.android.view.selector;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import com.devoxx.R;
import com.devoxx.connection.cfp.model.ConferenceApiModel;
import com.devoxx.connection.model.ConferencesApiModel;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.res.IntegerRes;

import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

@EViewGroup
public class SelectorView extends FrameLayout implements View.OnClickListener {

    public interface IWheelItemActionListener {
        void onWheelItemSelected(ConferenceApiModel data);

        void onWheelItemClicked(ConferenceApiModel data);
    }

    private static final int LAYOUTING_ARC_START_DEG = -90;
    private static final int FULL_CIRCLE_DEG = 360;

    @DimensionPixelOffsetRes(R.dimen.selector_circle_item_size)
    int itemCircleSize;

    @ColorRes(R.color.accent)
    int mainCircleColor;

    @ColorRes(R.color.degree_link)
    int pointCircleColor;

    @IntegerRes(android.R.integer.config_mediumAnimTime)
    int rotateAnimationTime;

    private IWheelItemActionListener listener;

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
        setClipToPadding(false);
        setClipChildren(false);
    }

    public void prepareForConferences(List<ConferenceApiModel> conferences) {
        clearView();

        for (ConferenceApiModel conference : conferences) {
            addNewItem(conference);
        }
        // By default first button is selected.
        onClick(getChildAt(0));
    }

    public void setListener(IWheelItemActionListener listener) {
        this.listener = listener;
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

        globalCircleRadius = (int) (w / 2f - (getPaddingLeft() + getPaddingRight()));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (!isAnyChild()) {
            return;
        }

        final int size = getChildCount();

        int arcStart = LAYOUTING_ARC_START_DEG;
        int arcStep = calculateArcStep();
        int childRotation = 0;

        for (int i = 0; i < size; i++) {
            final View child = getChildAt(i);
            final int chW = child.getMeasuredWidth();
            final int chH = child.getMeasuredHeight();
            final int hW = chW / 2;
            final int hH = chH / 2;

            final int newChX = (int) (centerX + globalCircleRadius * cos(toRadians(arcStart)));
            final int newChY = (int) (centerY + globalCircleRadius * sin(toRadians(arcStart)));

            child.layout(newChX - hW, newChY - hH, newChX + hW, newChY + hH);
            child.setRotation(childRotation);

            childRotation += arcStep;
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

    private void addNewItem(ConferenceApiModel conferenceApiModel) {
        final int index = getChildCount();

        final SelectorItemView view = SelectorItemView_.build(getContext());
        view.setupLabel(conferenceApiModel.country);
        view.setupIcon(getConferenceIcon(conferenceApiModel));
        view.setOnClickListener(this);
        view.setTag(new ItemViewInfo(index, index == 0, conferenceApiModel));

        addView(view, createLayoutParams());
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

        if (clickedViewInfo.isActive()) {
            listener.onWheelItemSelected(clickedViewInfo.getData());
            animateClickedViewIfNeeded(clickedView);
            return;
        }

        if (animationGuard) {
            return;
        }

        defaultApperance();

        listener.onWheelItemClicked(clickedViewInfo.getData());

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
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        animationGuard = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        listener.onWheelItemSelected(clickedViewInfo.data);
                        animationGuard = false;

                        animateClickedViewIfNeeded(clickedView);
                    }
                })
                .start();
    }

    private void animateClickedViewIfNeeded(View clickedView) {
        clickedView.clearAnimation();

        if (clickedView.getScaleX() == 1f) {
            clickedView.animate()
                    .scaleY(1.75f).scaleX(1.75f)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .setDuration(300)
                    .start();
        }
    }

    private void defaultApperance() {
        final int size = getChildCount();
        for (int i = 0; i < size; i++) {
            final View child = getChildAt(i);
            child.clearAnimation();
            if (child.getScaleX() > 1f) {
                child.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(150).start();
            }
        }
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
            }
        }
    }

    private boolean isAnyChild() {
        return getChildCount() > 0;
    }

    private int calculateArcStep() {
        return FULL_CIRCLE_DEG / getChildCount();
    }

    private LayoutParams createLayoutParams() {
        return new LayoutParams(itemCircleSize, itemCircleSize);
    }

    private void clearView() {
        removeAllViews();
        setRotation(0);
    }

    @android.support.annotation.DrawableRes
    private static int getConferenceIcon(ConferenceApiModel conf) {
        if (conf.country.toLowerCase().contains("france")) {
            return R.drawable.splash_btn_paris;
        } else if (conf.country.toLowerCase().contains("uk")) {
            return R.drawable.splash_btn_uk;
        } else if (conf.country.toLowerCase().contains("poland")) {
            return R.drawable.splash_btn_krakow;
        } else if (conf.country.toLowerCase().contains("belgium")) {
            return R.drawable.splash_btn_brussels;
        } else if (conf.country.toLowerCase().contains("morocco")) {
            return R.drawable.splash_btn_ma;
        } else {
            return R.drawable.splash_btn_paris;
        }
    }

    private static class ItemViewInfo {
        private int index;
        private boolean isActive;
        private ConferenceApiModel data;

        public ItemViewInfo(int index, boolean isActive, ConferenceApiModel data) {
            this.index = index;
            this.isActive = isActive;
            this.data = data;
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

        public ConferenceApiModel getData() {
            return data;
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
