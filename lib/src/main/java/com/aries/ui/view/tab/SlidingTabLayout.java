package com.aries.ui.view.tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aries.ui.view.tab.delegate.TabSlidingDelegate;
import com.aries.ui.view.tab.listener.ITabLayout;
import com.aries.ui.view.tab.listener.OnTabSelectListener;
import com.aries.ui.view.tab.utils.UnreadMsgUtils;
import com.aries.ui.view.tab.widget.MsgView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @Author: AriesHoo on 2018/11/30 11:18
 * @E-Mail: AriesHoo@126.com
 * @Function: 滑动TabLayout, 对于ViewPager的依赖性强
 * @Description: 1、2018年11月30日11:18:41 修改原库 https://github.com/H07000223/FlycoTabLayout 选中粗体当初始化选中第一项不生效BUG
 * {@link #updateTabStyles()}
 */
public class SlidingTabLayout extends HorizontalScrollView implements ViewPager.OnPageChangeListener, ITabLayout {
    private TabSlidingDelegate mDelegate;
    private Context mContext;
    private ViewPager mViewPager;
    private ArrayList<String> mTitles;
    private LinearLayout mTabsContainer;
    private int mCurrentTab;
    private float mCurrentPositionOffset;
    private int mTabCount;
    /**
     * 用于绘制显示器
     */
    private Rect mIndicatorRect = new Rect();
    /**
     * 用于实现滚动居中
     */
    private Rect mTabRect = new Rect();
    private GradientDrawable mIndicatorDrawable = new GradientDrawable();

    private Paint mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTrianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mTrianglePath = new Path();
    private static final int STYLE_NORMAL = 0;
    private static final int STYLE_TRIANGLE = 1;
    private static final int STYLE_BLOCK = 2;

    private int mLastScrollX;
    private int mHeight;
    private boolean mSnapOnTabClick;

    public SlidingTabLayout(Context context) {
        this(context, null, 0);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDelegate = new TabSlidingDelegate(this, attrs, this);
        //设置滚动视图是否可以伸缩其内容以填充视口
        setFillViewport(true);
        //重写onDraw方法,需要调用这个方法来清除flag
        setWillNotDraw(false);
        setClipChildren(false);
        setClipToPadding(false);

        this.mContext = context;
        mTabsContainer = new LinearLayout(context);
        addView(mTabsContainer);

        //get layout_height
        String height = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");

        if (height.equals(ViewGroup.LayoutParams.MATCH_PARENT + "")) {
        } else if (height.equals(ViewGroup.LayoutParams.WRAP_CONTENT + "")) {
        } else {
            int[] systemAttrs = {android.R.attr.layout_height};
            TypedArray a = context.obtainStyledAttributes(attrs, systemAttrs);
            mHeight = a.getDimensionPixelSize(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            a.recycle();
        }
    }

    public TabSlidingDelegate getDelegate() {
        return mDelegate;
    }

    /**
     * 关联ViewPager
     */
    public void setViewPager(ViewPager vp) {
        if (vp == null || vp.getAdapter() == null) {
            throw new IllegalStateException("ViewPager or ViewPager adapter can not be NULL !");
        }

        this.mViewPager = vp;

        this.mViewPager.removeOnPageChangeListener(this);
        this.mViewPager.addOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    /**
     * 关联ViewPager,用于不想在ViewPager适配器中设置titles数据的情况
     */
    public void setViewPager(ViewPager vp, String[] titles) {
        if (vp == null || vp.getAdapter() == null) {
            throw new IllegalStateException("ViewPager or ViewPager adapter can not be NULL !");
        }

        if (titles == null || titles.length == 0) {
            throw new IllegalStateException("Titles can not be EMPTY !");
        }

        if (titles.length != vp.getAdapter().getCount()) {
            throw new IllegalStateException("Titles length must be the same as the page count !");
        }

        this.mViewPager = vp;
        mTitles = new ArrayList<>();
        Collections.addAll(mTitles, titles);

        this.mViewPager.removeOnPageChangeListener(this);
        this.mViewPager.addOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    /**
     * 关联ViewPager,用于连适配器都不想自己实例化的情况
     */
    public void setViewPager(ViewPager vp, String[] titles, FragmentActivity fa, ArrayList<Fragment> fragments) {
        if (vp == null) {
            throw new IllegalStateException("ViewPager can not be NULL !");
        }

        if (titles == null || titles.length == 0) {
            throw new IllegalStateException("Titles can not be EMPTY !");
        }

        this.mViewPager = vp;
        this.mViewPager.setAdapter(new InnerPagerAdapter(fa.getSupportFragmentManager(), fragments, titles));

        this.mViewPager.removeOnPageChangeListener(this);
        this.mViewPager.addOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    /**
     * 更新数据
     */
    public void notifyDataSetChanged() {
        mTabsContainer.removeAllViews();
        this.mTabCount = mTitles == null ? mViewPager.getAdapter().getCount() : mTitles.size();
        View tabView;
        for (int i = 0; i < mTabCount; i++) {
            tabView = View.inflate(mContext, R.layout.layout_tab, null);
            CharSequence pageTitle = mTitles == null ? mViewPager.getAdapter().getPageTitle(i) : mTitles.get(i);
            addTab(i, pageTitle.toString(), tabView);
        }

        updateTabStyles();
    }

    public void addNewTab(String title) {
        View tabView = View.inflate(mContext, R.layout.layout_tab, null);
        if (mTitles != null) {
            mTitles.add(title);
        }

        CharSequence pageTitle = mTitles == null ? mViewPager.getAdapter().getPageTitle(mTabCount) : mTitles.get(mTabCount);
        addTab(mTabCount, pageTitle.toString(), tabView);
        this.mTabCount = mTitles == null ? mViewPager.getAdapter().getCount() : mTitles.size();

        updateTabStyles();
    }

    /**
     * 创建并添加tab
     */
    private void addTab(final int position, String title, View tabView) {
        TextView tv_tab_title = tabView.findViewById(R.id.tv_tab_title);
        if (tv_tab_title != null && title != null) {
            tv_tab_title.setText(title);
        }

        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mTabsContainer.indexOfChild(v);
                if (position != -1) {
                    if (mViewPager.getCurrentItem() != position) {
                        if (mSnapOnTabClick) {
                            mViewPager.setCurrentItem(position, false);
                        } else {
                            mViewPager.setCurrentItem(position);
                        }

                        if (mListener != null) {
                            mListener.onTabSelect(position);
                        }
                    } else {
                        if (mListener != null) {
                            mListener.onTabReselect(position);
                        }
                    }
                }
            }
        });

        /** 每一个Tab的布局参数 */
        LinearLayout.LayoutParams lp_tab = getDelegate().isTabSpaceEqual() ?
                new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f) :
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        if (getDelegate().getTabWidth() > 0) {
            lp_tab = new LinearLayout.LayoutParams((int) getDelegate().getTabWidth(), LayoutParams.MATCH_PARENT);
        }

        mTabsContainer.addView(tabView, position, lp_tab);
    }

    @Override
    public void updateTabStyles() {
        for (int i = 0; i < mTabCount; i++) {
            View v = mTabsContainer.getChildAt(i);
            TextView tv_tab_title = v.findViewById(R.id.tv_tab_title);
            if (tv_tab_title != null) {
                tv_tab_title.setTextColor(i == mCurrentTab ? getDelegate().getTextSelectColor() : getDelegate().getTextUnSelectColor());
                tv_tab_title.setTextSize(getDelegate().getTextSizeUnit(), getDelegate().getTextSize());
                tv_tab_title.setPadding((int) getDelegate().getTabPadding(), 0, (int) getDelegate().getTabPadding(), 0);
                if (getDelegate().isTextAllCaps()) {
                    tv_tab_title.setText(tv_tab_title.getText().toString().toUpperCase());
                }
                if (getDelegate().getTextBold() == com.aries.ui.view.tab.TextBold.BOTH) {
                    tv_tab_title.getPaint().setFakeBoldText(true);
                } else if (getDelegate().getTextBold() == com.aries.ui.view.tab.TextBold.SELECT) {
                    //增加-以修正原库第一次选中粗体不生效问题
                    tv_tab_title.getPaint().setFakeBoldText(mCurrentTab == i);
                } else {
                    tv_tab_title.getPaint().setFakeBoldText(false);
                }
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        /**
         * position:当前View的位置
         * mCurrentPositionOffset:当前View的偏移量比例.[0,1)
         */
        this.mCurrentTab = position;
        this.mCurrentPositionOffset = positionOffset;
        scrollToCurrentTab();
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        updateTabSelection(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * HorizontalScrollView滚到当前tab,并且居中显示
     */
    private void scrollToCurrentTab() {
        if (mTabCount <= 0) {
            return;
        }

        int offset = (int) (mCurrentPositionOffset * mTabsContainer.getChildAt(mCurrentTab).getWidth());
        /**当前Tab的left+当前Tab的Width乘以positionOffset*/
        int newScrollX = mTabsContainer.getChildAt(mCurrentTab).getLeft() + offset;

        if (mCurrentTab > 0 || offset > 0) {
            /**HorizontalScrollView移动到当前tab,并居中*/
            newScrollX -= getWidth() / 2 - getPaddingLeft();
            calcIndicatorRect();
            newScrollX += ((mTabRect.right - mTabRect.left) / 2);
        }

        if (newScrollX != mLastScrollX) {
            mLastScrollX = newScrollX;
            /** scrollTo（int x,int y）:x,y代表的不是坐标点,而是偏移量
             *  x:表示离起始位置的x水平方向的偏移量
             *  y:表示离起始位置的y垂直方向的偏移量
             */
            scrollTo(newScrollX, 0);
        }
    }

    private void updateTabSelection(int position) {
        for (int i = 0; i < mTabCount; ++i) {
            View tabView = mTabsContainer.getChildAt(i);
            final boolean isSelect = i == position;
            TextView tab_title = tabView.findViewById(R.id.tv_tab_title);

            if (tab_title != null) {
                tab_title.setTextColor(isSelect ? getDelegate().getTextSelectColor() : getDelegate().getTextUnSelectColor());
                if (getDelegate().getTextBold() == com.aries.ui.view.tab.TextBold.SELECT) {
                    tab_title.getPaint().setFakeBoldText(isSelect);
                }
            }
        }
    }

    private float margin;

    private void calcIndicatorRect() {
        View currentTabView = mTabsContainer.getChildAt(this.mCurrentTab);
        float left = currentTabView.getLeft();
        float right = currentTabView.getRight();

        //for mIndicatorWidthEqualTitle
        if (getDelegate().getIndicatorStyle() == STYLE_NORMAL && getDelegate().isIndicatorWidthEqualTitle()) {
            TextView tab_title = currentTabView.findViewById(R.id.tv_tab_title);
            mTextPaint.setTextSize(getDelegate().getTextSize());
            float textWidth = mTextPaint.measureText(tab_title.getText().toString());
            margin = (right - left - textWidth) / 2;
        }

        if (this.mCurrentTab < mTabCount - 1) {
            View nextTabView = mTabsContainer.getChildAt(this.mCurrentTab + 1);
            float nextTabLeft = nextTabView.getLeft();
            float nextTabRight = nextTabView.getRight();

            left = left + mCurrentPositionOffset * (nextTabLeft - left);
            right = right + mCurrentPositionOffset * (nextTabRight - right);

            //for mIndicatorWidthEqualTitle
            if (getDelegate().getIndicatorStyle() == STYLE_NORMAL && getDelegate().isIndicatorWidthEqualTitle()) {
                TextView next_tab_title = nextTabView.findViewById(R.id.tv_tab_title);
                mTextPaint.setTextSize(getDelegate().getTextSize());
                float nextTextWidth = mTextPaint.measureText(next_tab_title.getText().toString());
                float nextMargin = (nextTabRight - nextTabLeft - nextTextWidth) / 2;
                margin = margin + mCurrentPositionOffset * (nextMargin - margin);
            }
        }

        mIndicatorRect.left = (int) left;
        mIndicatorRect.right = (int) right;
        //for mIndicatorWidthEqualTitle
        if (getDelegate().getIndicatorStyle() == STYLE_NORMAL && getDelegate().isIndicatorWidthEqualTitle()) {
            mIndicatorRect.left = (int) (left + margin - 1);
            mIndicatorRect.right = (int) (right - margin - 1);
        }

        mTabRect.left = (int) left;
        mTabRect.right = (int) right;

        //indicatorWidth小于0时,原jpardogo's PagerSlidingTabStrip
        if (getDelegate().getIndicatorWidth() < 0) {

        } else {//indicatorWidth大于0时,圆角矩形以及三角形
            float indicatorLeft = currentTabView.getLeft() + (currentTabView.getWidth() - getDelegate().getIndicatorWidth()) / 2;

            if (this.mCurrentTab < mTabCount - 1) {
                View nextTab = mTabsContainer.getChildAt(this.mCurrentTab + 1);
                indicatorLeft = indicatorLeft + mCurrentPositionOffset * (currentTabView.getWidth() / 2 + nextTab.getWidth() / 2);
            }

            mIndicatorRect.left = (int) indicatorLeft;
            mIndicatorRect.right = (int) (mIndicatorRect.left + getDelegate().getIndicatorWidth());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || mTabCount <= 0) {
            return;
        }

        int height = getHeight();
        int paddingLeft = getPaddingLeft();
        // draw divider
        if (getDelegate().getDividerWidth() > 0) {
            mDividerPaint.setStrokeWidth(getDelegate().getDividerWidth());
            mDividerPaint.setColor(getDelegate().getDividerColor());
            for (int i = 0; i < mTabCount - 1; i++) {
                View tab = mTabsContainer.getChildAt(i);
                canvas.drawLine(paddingLeft + tab.getRight(), getDelegate().getDividerPadding(),
                        paddingLeft + tab.getRight(), height - getDelegate().getDividerPadding(), mDividerPaint);
            }
        }

        // draw underline
        if (getDelegate().getUnderlineHeight() > 0) {
            mRectPaint.setColor(getDelegate().getUnderlineColor());
            if (getDelegate().getUnderlineGravity() == Gravity.BOTTOM) {
                canvas.drawRect(paddingLeft, height - getDelegate().getUnderlineHeight(),
                        mTabsContainer.getWidth() + paddingLeft, height, mRectPaint);
            } else {
                canvas.drawRect(paddingLeft, 0, mTabsContainer.getWidth() + paddingLeft,
                        getDelegate().getUnderlineHeight(), mRectPaint);
            }
        }

        //draw indicator line

        calcIndicatorRect();
        if (getDelegate().getIndicatorStyle() == STYLE_TRIANGLE) {
            if (getDelegate().getIndicatorHeight() > 0) {
                mTrianglePaint.setColor(getDelegate().getIndicatorColor());
                mTrianglePath.reset();
                mTrianglePath.moveTo(paddingLeft + mIndicatorRect.left, height);
                mTrianglePath.lineTo(paddingLeft + mIndicatorRect.left / 2 + mIndicatorRect.right / 2, height - getDelegate().getIndicatorHeight());
                mTrianglePath.lineTo(paddingLeft + mIndicatorRect.right, height);
                mTrianglePath.close();
                canvas.drawPath(mTrianglePath, mTrianglePaint);
            }
        } else if (getDelegate().getIndicatorStyle() == STYLE_BLOCK) {
            if (getDelegate().getIndicatorHeight() < 0) {
                getDelegate().setIndicatorHeight(height - getDelegate().getIndicatorMarginTop() - getDelegate().getIndicatorMarginBottom());
            }

            if (getDelegate().getIndicatorHeight() > 0) {
                if (getDelegate().getIndicatorCornerRadius() < 0 || getDelegate().getIndicatorCornerRadius() > getDelegate().getIndicatorHeight() / 2) {
                    getDelegate().setIndicatorCornerRadius(getDelegate().getIndicatorHeight() / 2);
                }

                mIndicatorDrawable.setColor(getDelegate().getIndicatorColor());
                mIndicatorDrawable.setBounds(paddingLeft + (int) getDelegate().getIndicatorMarginLeft() + mIndicatorRect.left,
                        (int) getDelegate().getIndicatorMarginTop(), (int) (paddingLeft + mIndicatorRect.right - getDelegate().getIndicatorMarginRight()),
                        (int) (getDelegate().getIndicatorMarginTop() + getDelegate().getIndicatorHeight()));
                mIndicatorDrawable.setCornerRadius(getDelegate().getIndicatorCornerRadius());
                mIndicatorDrawable.draw(canvas);
            }
        } else {
            if (getDelegate().getIndicatorHeight() > 0) {
                mIndicatorDrawable.setColor(getDelegate().getIndicatorColor());
                if (getDelegate().getIndicatorGravity() == Gravity.BOTTOM) {
                    mIndicatorDrawable.setBounds(paddingLeft + getDelegate().getIndicatorMarginLeft() + mIndicatorRect.left,
                            height - (int) getDelegate().getIndicatorHeight() - getDelegate().getIndicatorMarginBottom(),
                            paddingLeft + mIndicatorRect.right - getDelegate().getIndicatorMarginRight(),
                            height - (int) getDelegate().getIndicatorMarginBottom());
                } else {
                    mIndicatorDrawable.setBounds(paddingLeft + getDelegate().getIndicatorMarginLeft() + mIndicatorRect.left,
                            getDelegate().getIndicatorMarginTop(),
                            paddingLeft + mIndicatorRect.right - getDelegate().getIndicatorMarginRight(),
                            (int) getDelegate().getIndicatorHeight() + getDelegate().getIndicatorMarginTop());
                }
                mIndicatorDrawable.setCornerRadius(getDelegate().getIndicatorCornerRadius());
                mIndicatorDrawable.draw(canvas);
            }
        }
    }

    //setter and getter
    public void setCurrentTab(int currentTab) {
        this.mCurrentTab = currentTab;
        mViewPager.setCurrentItem(currentTab);

    }

    public void setCurrentTab(int currentTab, boolean smoothScroll) {
        this.mCurrentTab = currentTab;
        mViewPager.setCurrentItem(currentTab, smoothScroll);
    }

//    public void setIndicatorStyle(int indicatorStyle) {
//        this.mIndicatorStyle = indicatorStyle;
//        invalidate();
//    }
//
//    public void setTabPadding(float tabPadding) {
//        this.mTabPadding = dp2px(tabPadding);
//        updateTabStyles();
//    }
//
//    public void setTabSpaceEqual(boolean tabSpaceEqual) {
//        this.mTabSpaceEqual = tabSpaceEqual;
//        updateTabStyles();
//    }
//
//    public void setTabWidth(float tabWidth) {
//        this.mTabWidth = dp2px(tabWidth);
//        updateTabStyles();
//    }
//
//    public void setIndicatorColor(int indicatorColor) {
//        this.mIndicatorColor = indicatorColor;
//        invalidate();
//    }
//
//    public void setIndicatorHeight(float indicatorHeight) {
//        this.mIndicatorHeight = dp2px(indicatorHeight);
//        invalidate();
//    }
//
//    public void setIndicatorWidth(float indicatorWidth) {
//        this.mIndicatorWidth = dp2px(indicatorWidth);
//        invalidate();
//    }
//
//    public void setIndicatorCornerRadius(float indicatorCornerRadius) {
//        this.mIndicatorCornerRadius = dp2px(indicatorCornerRadius);
//        invalidate();
//    }
//
//    public void setIndicatorGravity(int indicatorGravity) {
//        this.mIndicatorGravity = indicatorGravity;
//        invalidate();
//    }
//
//    public void setIndicatorMargin(float indicatorMarginLeft, float indicatorMarginTop,
//                                   float indicatorMarginRight, float indicatorMarginBottom) {
//        this.mIndicatorMarginLeft = dp2px(indicatorMarginLeft);
//        this.mIndicatorMarginTop = dp2px(indicatorMarginTop);
//        this.mIndicatorMarginRight = dp2px(indicatorMarginRight);
//        this.mIndicatorMarginBottom = dp2px(indicatorMarginBottom);
//        invalidate();
//    }
//
//    public void setIndicatorWidthEqualTitle(boolean indicatorWidthEqualTitle) {
//        this.mIndicatorWidthEqualTitle = indicatorWidthEqualTitle;
//        invalidate();
//    }
//
//    public void setUnderlineColor(int underlineColor) {
//        this.mUnderlineColor = underlineColor;
//        invalidate();
//    }
//
//    public void setUnderlineHeight(float underlineHeight) {
//        this.mUnderlineHeight = dp2px(underlineHeight);
//        invalidate();
//    }
//
//    public void setUnderlineGravity(int underlineGravity) {
//        this.mUnderlineGravity = underlineGravity;
//        invalidate();
//    }
//
//    public void setDividerColor(int dividerColor) {
//        this.mDividerColor = dividerColor;
//        invalidate();
//    }
//
//    public void setDividerWidth(float dividerWidth) {
//        this.mDividerWidth = dp2px(dividerWidth);
//        invalidate();
//    }
//
//    public void setDividerPadding(float dividerPadding) {
//        this.mDividerPadding = dp2px(dividerPadding);
//        invalidate();
//    }

//    public void setTextSize(float textSize) {
//        this.mTextSize = sp2px(textSize);
//        updateTabStyles();
//    }
//
//    public void setTextSelectColor(int textSelectColor) {
//        this.mTextSelectColor = textSelectColor;
//        updateTabStyles();
//    }
//
//    public void setTextUnselectColor(int textUnselectColor) {
//        this.mTextUnselectColor = textUnselectColor;
//        updateTabStyles();
//    }
//
//    /**
//     * 新增方法
//     *
//     * @param textBold
//     * @return
//     */
//    public SlidingTabLayout setTextBold(TextBold textBold) {
//        this.mTextBold = textBold;
//        updateTabStyles();
//        return this;
//    }
//
//    public void setTextAllCaps(boolean textAllCaps) {
//        this.mTextAllCaps = textAllCaps;
//        updateTabStyles();
//    }

    public void setSnapOnTabClick(boolean snapOnTabClick) {
        mSnapOnTabClick = snapOnTabClick;
    }


    public int getTabCount() {
        return mTabCount;
    }

    public int getCurrentTab() {
        return mCurrentTab;
    }

//    public int getIndicatorStyle() {
//        return mIndicatorStyle;
//    }
//
//    public float getTabPadding() {
//        return mTabPadding;
//    }
//
//    public boolean isTabSpaceEqual() {
//        return mTabSpaceEqual;
//    }
//
//    public float getTabWidth() {
//        return mTabWidth;
//    }
//
//    public int getIndicatorColor() {
//        return mIndicatorColor;
//    }
//
//    public float getIndicatorHeight() {
//        return mIndicatorHeight;
//    }
//
//    public float getIndicatorWidth() {
//        return mIndicatorWidth;
//    }
//
//    public float getIndicatorCornerRadius() {
//        return mIndicatorCornerRadius;
//    }
//
//    public float getIndicatorMarginLeft() {
//        return mIndicatorMarginLeft;
//    }
//
//    public float getIndicatorMarginTop() {
//        return mIndicatorMarginTop;
//    }
//
//    public float getIndicatorMarginRight() {
//        return mIndicatorMarginRight;
//    }
//
//    public float getIndicatorMarginBottom() {
//        return mIndicatorMarginBottom;
//    }
//
//    public int getUnderlineColor() {
//        return mUnderlineColor;
//    }
//
//    public float getUnderlineHeight() {
//        return mUnderlineHeight;
//    }
//
//    public int getDividerColor() {
//        return mDividerColor;
//    }
//
//    public float getDividerWidth() {
//        return mDividerWidth;
//    }
//
//    public float getDividerPadding() {
//        return mDividerPadding;
//    }

//    public float getTextsize() {
//        return mTextSize;
//    }
//
//    public int getTextSelectColor() {
//        return mTextSelectColor;
//    }
//
//    public int getTextUnselectColor() {
//        return mTextUnselectColor;
//    }
//
//    public TextBold getTextBold() {
//        return mTextBold;
//    }
//
//
//    public boolean isTextAllCaps() {
//        return mTextAllCaps;
//    }

    public TextView getTitleView(int tab) {
        View tabView = mTabsContainer.getChildAt(tab);
        TextView tv_tab_title = (TextView) tabView.findViewById(R.id.tv_tab_title);
        return tv_tab_title;
    }

    //setter and getter

    // show MsgTipView
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private SparseArray<Boolean> mInitSetMap = new SparseArray<>();

    /**
     * 显示未读消息
     *
     * @param position 显示tab位置
     * @param num      num小于等于0显示红点,num大于0显示数字
     */
    public SlidingTabLayout showMsg(int position, int num) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }

        View tabView = mTabsContainer.getChildAt(position);
        MsgView tipView = tabView.findViewById(R.id.rtv_msg_tip);
        if (tipView != null) {
            UnreadMsgUtils.show(tipView, num);

            if (mInitSetMap.get(position) != null && mInitSetMap.get(position)) {
                return this;
            }

            setMsgMargin(position, 4f, 2f);
            mInitSetMap.put(position, true);
        }
        return this;
    }

    /**
     * 显示未读红点
     *
     * @param position 显示tab位置
     */
    public SlidingTabLayout showDot(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
       return showMsg(position, 0);
    }

    /**
     * 隐藏未读消息
     */
    public SlidingTabLayout hideMsg(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        View tabView = mTabsContainer.getChildAt(position);
        MsgView tipView = tabView.findViewById(R.id.rtv_msg_tip);
        if (tipView != null) {
            tipView.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 设置未读消息偏移,原点为文字的右上角.当控件高度固定,消息提示位置易控制,显示效果佳
     */
    public SlidingTabLayout setMsgMargin(int position, int leftPadding, int bottomPadding) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        View tabView = mTabsContainer.getChildAt(position);
        MsgView tipView = tabView.findViewById(R.id.rtv_msg_tip);
        if (tipView != null) {
            TextView tv_tab_title = tabView.findViewById(R.id.tv_tab_title);
            mTextPaint.setTextSize(getDelegate().getTextSize());
            float textWidth = mTextPaint.measureText(tv_tab_title.getText().toString());
            float textHeight = mTextPaint.descent() - mTextPaint.ascent();
            MarginLayoutParams lp = (MarginLayoutParams) tipView.getLayoutParams();
            lp.leftMargin = getDelegate().getTabWidth() >= 0
                    ? (int) (getDelegate().getTabWidth() / 2 + textWidth / 2 + leftPadding)
                    : (int) (getDelegate().getTabPadding() + textWidth + leftPadding);
            lp.topMargin = mHeight > 0 ? (int) (mHeight - textHeight) / 2 - bottomPadding : 0;
            tipView.setLayoutParams(lp);
        }
        return this;
    }

    public SlidingTabLayout setMsgMargin(int position, float leftPadding, float bottomPadding) {
        return setMsgMargin(position, getDelegate().dp2px(leftPadding), getDelegate().dp2px(bottomPadding));
    }

    /**
     * 当前类只提供了少许设置未读消息属性的方法,可以通过该方法获取MsgView对象从而各种设置
     */
    public MsgView getMsgView(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        View tabView = mTabsContainer.getChildAt(position);
        MsgView tipView = tabView.findViewById(R.id.rtv_msg_tip);
        return tipView;
    }

    private OnTabSelectListener mListener;

    public void setOnTabSelectListener(OnTabSelectListener listener) {
        this.mListener = listener;
    }

    class InnerPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private String[] titles;

        public InnerPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, String[] titles) {
            super(fm);
            this.fragments = fragments;
            this.titles = titles;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // 覆写destroyItem并且空实现,这样每个Fragment中的视图就不会被销毁
            // super.destroyItem(container, position, object);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("mCurrentTab", mCurrentTab);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mCurrentTab = bundle.getInt("mCurrentTab");
            state = bundle.getParcelable("instanceState");
            if (mCurrentTab != 0 && mTabsContainer.getChildCount() > 0) {
                updateTabSelection(mCurrentTab);
                //原库恢复状态时未将Fragment选中CurrentTab
                setCurrentTab(mCurrentTab);
                scrollToCurrentTab();
            }
        }
        super.onRestoreInstanceState(state);
    }
}
