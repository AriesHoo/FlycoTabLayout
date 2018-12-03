package com.aries.ui.view.tab.delegate;

import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.aries.ui.view.tab.IndicatorStyle;
import com.aries.ui.view.tab.R;
import com.aries.ui.view.tab.listener.ITabLayout;

/**
 * @Author: AriesHoo on 2018/11/30 16:59
 * @E-Mail: AriesHoo@126.com
 * @Function: {@link com.aries.ui.view.tab.CommonTabLayout}及{@link com.aries.ui.view.tab.SlidingTabLayout}共有属性及Java设置方法代理
 * @Description:
 */
public class TabCommonSlidingDelegate<T extends TabCommonSlidingDelegate> extends TabLayoutDelegate<T> {

    private int mIndicatorStyle;
    private float mIndicatorWidth;
    private int mIndicatorGravity;

    /**
     * underline
     */
    private int mUnderlineColor;
    private float mUnderlineHeight;
    private int mUnderlineGravity;


    public TabCommonSlidingDelegate(View view, AttributeSet attrs, ITabLayout iTabLayout) {
        super(view, attrs, iTabLayout);
        mIndicatorStyle = mTypedArray.getInt(R.styleable.TabLayout_tl_indicator_style, IndicatorStyle.NORMAL);
        mIndicatorWidth = mTypedArray.getDimension(R.styleable.TabLayout_tl_indicator_width, dp2px(mIndicatorStyle == IndicatorStyle.TRIANGLE ? 10 : -1));
        mIndicatorGravity = mTypedArray.getInt(R.styleable.TabLayout_tl_indicator_gravity, Gravity.BOTTOM);

        mUnderlineColor = mTypedArray.getColor(R.styleable.TabLayout_tl_underline_color, Color.parseColor("#ffffff"));
        mUnderlineHeight = mTypedArray.getDimension(R.styleable.TabLayout_tl_underline_height, dp2px(0));
        mUnderlineGravity = mTypedArray.getInt(R.styleable.TabLayout_tl_underline_gravity, Gravity.BOTTOM);
    }

    /**
     * 设置指示器样式{@link IndicatorStyle#NORMAL}{@link IndicatorStyle#TRIANGLE}{@link IndicatorStyle#BLOCK}
     *
     * @param indicatorStyle
     * @return
     */
    public T setIndicatorStyle(int indicatorStyle) {
        this.mIndicatorStyle = indicatorStyle;
        return back();
    }

    public T setIndicatorWidth(float indicatorWidth) {
        return setIndicatorWidth(dp2px(indicatorWidth));
    }

    /**
     * 新增方法
     *
     * @param indicatorWidth
     * @return
     */
    public T setIndicatorWidth(int indicatorWidth) {
        this.mIndicatorWidth = indicatorWidth;
        return back();
    }

    public T setIndicatorGravity(int indicatorGravity) {
        this.mIndicatorGravity = indicatorGravity;
        return back();
    }

    public T setUnderlineColor(int underlineColor) {
        this.mUnderlineColor = underlineColor;
        return back();
    }

    public T setUnderlineHeight(float underlineHeight) {
        this.mUnderlineHeight = underlineHeight;
        return back();
    }

    public T setUnderlineGravity(int underlineGravity) {
        this.mUnderlineGravity = underlineGravity;
        return back();
    }

    public int getIndicatorStyle() {
        return mIndicatorStyle;
    }

    public float getIndicatorWidth() {
        return mIndicatorWidth;
    }


    public int getIndicatorGravity() {
        return mIndicatorGravity;
    }

    public int getUnderlineColor() {
        return mUnderlineColor;
    }

    public float getUnderlineHeight() {
        return mUnderlineHeight;
    }

    public int getUnderlineGravity() {
        return mUnderlineGravity;
    }
}
