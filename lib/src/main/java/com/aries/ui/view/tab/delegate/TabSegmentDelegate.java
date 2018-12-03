package com.aries.ui.view.tab.delegate;

import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.View;

import com.aries.ui.view.tab.R;
import com.aries.ui.view.tab.listener.ITabLayout;

/**
 * @Author: AriesHoo on 2018/12/3 11:16
 * @E-Mail: AriesHoo@126.com
 * @Function: {@link com.aries.ui.view.tab.SegmentTabLayout} 属性及java设置方法代理
 * @Description:
 */
public class TabSegmentDelegate extends TabLayoutDelegate<TabSegmentDelegate> {

    private long mIndicatorAnimDuration;
    private boolean mIndicatorAnimEnable;
    private boolean mIndicatorBounceEnable;

    private ColorStateList mBarColor;
    private ColorStateList mBarStrokeColor;
    private int mBarStrokeWidth;

    public TabSegmentDelegate(View view, AttributeSet attrs, ITabLayout iTabLayout) {
        super(view, attrs, iTabLayout);
        mIndicatorHeight = mTypedArray.getDimension(R.styleable.TabLayout_tl_indicator_height, -1);
        mIndicatorCornerRadius = mTypedArray.getDimension(R.styleable.TabLayout_tl_indicator_corner_radius, -1);
        mTextUnSelectColor = mTypedArray.getColor(R.styleable.TabLayout_tl_textUnSelectColor, mIndicatorColor);
        mIndicatorAnimEnable = mTypedArray.getBoolean(R.styleable.TabLayout_tl_indicator_anim_enable, false);
        mIndicatorBounceEnable = mTypedArray.getBoolean(R.styleable.TabLayout_tl_indicator_bounce_enable, true);
        mIndicatorAnimDuration = mTypedArray.getInt(R.styleable.TabLayout_tl_indicator_anim_duration, -1);

        mBarColor = mTypedArray.getColorStateList(R.styleable.TabLayout_tl_bar_color);
        mBarStrokeColor = mTypedArray.getColorStateList(R.styleable.TabLayout_tl_bar_stroke_color);
        mBarStrokeWidth = mTypedArray.getDimensionPixelSize(R.styleable.TabLayout_tl_bar_stroke_width, dp2px(1));
        mBarStrokeColor = mBarStrokeColor == null ? ColorStateList.valueOf(mIndicatorColor) : mBarStrokeColor;
    }

    public TabSegmentDelegate setIndicatorAnimDuration(long indicatorAnimDuration) {
        this.mIndicatorAnimDuration = indicatorAnimDuration;
        return back();
    }

    public TabSegmentDelegate setIndicatorAnimEnable(boolean indicatorAnimEnable) {
        this.mIndicatorAnimEnable = indicatorAnimEnable;
        return back();
    }

    public TabSegmentDelegate setIndicatorBounceEnable(boolean indicatorBounceEnable) {
        this.mIndicatorBounceEnable = indicatorBounceEnable;
        return back();
    }

    public TabSegmentDelegate setBarColor(int barColor) {
        return setBarColor(ColorStateList.valueOf(barColor));
    }

    public TabSegmentDelegate setBarColor(ColorStateList barColor) {
        mBarColor = barColor;
        return back();
    }

    public TabSegmentDelegate setBarStrokeColor(int barStrokeColor) {
        return setBarStrokeColor(ColorStateList.valueOf(barStrokeColor));
    }

    public TabSegmentDelegate setBarStrokeColor(ColorStateList barStrokeColor) {
        mBarStrokeColor = barStrokeColor;
        return back();
    }

    public TabSegmentDelegate setBarStrokeWidth(int barStrokeWidth) {
        mBarStrokeWidth = barStrokeWidth;
        return back();
    }

    public long getIndicatorAnimDuration() {
        return mIndicatorAnimDuration;
    }

    public boolean isIndicatorAnimEnable() {
        return mIndicatorAnimEnable;
    }

    public boolean isIndicatorBounceEnable() {
        return mIndicatorBounceEnable;
    }

    public ColorStateList getBarColor() {
        return mBarColor;
    }

    public ColorStateList getBarStrokeColor() {
        return mBarStrokeColor;
    }

    public int getBarStrokeWidth() {
        return mBarStrokeWidth;
    }
}
