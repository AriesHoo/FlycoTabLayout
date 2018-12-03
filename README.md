# TabLayout  FlycoTabLayout V2.1.2 小修改版本，原库已太久没有更新,属性及方法做了一定修改，修复小部分BUG
--------------------------

[![](https://img.shields.io/badge/download-demo-blue.svg)](https://raw.githubusercontent.com/AriesHoo/TabLayout/master/apk/sample.apk)
[![](https://jitpack.io/v/AriesHoo/TabLayout.svg)](https://jitpack.io/#AriesHoo/TabLayout)
[![](https://img.shields.io/github/release/AriesHoo/TabLayout.svg)](https://github.com/AriesHoo/TabLayout/releases)
[![API](https://img.shields.io/badge/API-14%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=14)
[![](https://img.shields.io/badge/简书-AriesHoo-blue.svg)](http://www.jianshu.com/u/a229eee96115)
[![](https://img.shields.io/badge/Fork-FlycoTabLayoutV2.1.2-green.svg)](https://github.com/H07000223/FlycoTabLayout)

[[Download]](https://raw.githubusercontent.com/AriesHoo/TabLayout/master/apk/sample.apk)

![](/apk/qr.png)

## 录屏展示

![](https://github.com/AriesHoo/FastLib/blob/master/screenshot/preview_1.gif)

![](https://github.com/AriesHoo/FastLib/blob/master/screenshot/preview_2.gif)

![](https://github.com/AriesHoo/FastLib/blob/master/screenshot/preview_3.gif)


## Gradle

```groovy
dependencies{
    compile 'com.android.support:support-v4:28.0.0'
    implementation 'com.github.AriesHoo:TabLayout:${LATEST_VERSION}'
    //implementation 'com.github.AriesHoo:TabLayout:1.0.0'
}
```

## 自定义属性-修改了几个参数拼写

|name|format|description|
|:---:|:---:|:---:|
| tl_indicator_color | color |设置显示器颜色
| tl_indicator_height | dimension |设置显示器高度
| tl_indicator_width | dimension |设置显示器固定宽度
| tl_indicator_margin_left | dimension |设置显示器margin,当indicator_width大于0,无效
| tl_indicator_margin_top | dimension |设置显示器margin,当indicator_width大于0,无效
| tl_indicator_margin_right | dimension |设置显示器margin,当indicator_width大于0,无效
| tl_indicator_margin_bottom | dimension |设置显示器margin,当indicator_width大于0,无效 
| tl_indicator_corner_radius | dimension |设置显示器圆角弧度
| tl_indicator_gravity | enum |设置显示器上方(TOP)还是下方(BOTTOM),只对常规显示器有用
| tl_indicator_style | enum |设置显示器为常规(NORMAL)或三角形(TRIANGLE)或背景色块(BLOCK)
| tl_underline_color | color |设置下划线颜色
| tl_underline_height | dimension |设置下划线高度
| tl_underline_gravity | enum |设置下划线上方(TOP)还是下方(BOTTOM)
| tl_divider_color | color |设置分割线颜色
| tl_divider_width | dimension |设置分割线宽度
| tl_divider_padding |dimension| 设置分割线的paddingTop和paddingBottom
| tl_tab_padding |dimension| 设置tab的paddingLeft和paddingRight
| tl_tab_space_equal |boolean| 设置tab大小等分
| tl_tab_width |dimension| 设置tab固定大小
| tl_textSize |dimension| 设置字体大小
| tl_textSelectColor |color| 设置字体选中颜色
| tl_textUnSelectColor |color| 设置字体未选中颜色
| tl_textBold |boolean| 设置字体加粗
| tl_iconWidth |dimension| 设置icon宽度(仅支持CommonTabLayout)
| tl_iconHeight |dimension|设置icon高度(仅支持CommonTabLayout)
| tl_iconVisible |boolean| 设置icon是否可见(仅支持CommonTabLayout)
| tl_iconGravity |enum| 设置icon显示位置,对应Gravity中常量值,左上右下(仅支持CommonTabLayout)
| tl_iconMargin |dimension| 设置icon与文字间距(仅支持CommonTabLayout)
| tl_indicator_anim_enable |boolean| 设置显示器支持动画(only for CommonTabLayout)
| tl_indicator_anim_duration |integer| 设置显示器动画时间(only for CommonTabLayout)
| tl_indicator_bounce_enable |boolean| 设置显示器支持动画回弹效果(only for CommonTabLayout)
| tl_indicator_width_equal_title |boolean| 设置显示器与标题一样长(only for SlidingTabLayout)

## 自定义属性-java代码调用--仅自定义属性通过代理类设置其它如setCurrentTab 还是原来不变

 tab.getDelegate()
    .setXXX()
    .setYYY();
    
    
 ```
  mTabLayout.getDelegate()
                 .setTextSelectColor(ContextCompat.getColor(mContext, R.color.colorTabTextSelect))
                 .setTextUnSelectColor(ContextCompat.getColor(mContext, R.color.colorTabTextUnSelect))
                 .setUnderlineColor(ContextCompat.getColor(mContext, R.color.colorTabUnderline))
                 .setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.dp_tab_text_size))
                 .setUnderlineGravity(Gravity.TOP)
                 .setUnderlineHeight(mContext.getResources().getDimension(R.dimen.dp_tab_underline))
                 .setIconMargin(mContext.getResources().getDimensionPixelSize(R.dimen.dp_tab_margin))
                 .setIconWidth(mContext.getResources().getDimensionPixelSize(R.dimen.dp_tab_icon))
                 .setIconHeight(mContext.getResources().getDimensionPixelSize(R.dimen.dp_tab_icon))
                 //设置指示器高度为0
                 .setIndicatorHeight(0);
 ```
 
## 鸣谢

*   [FlycoTabLayout](https://github.com/H07000223/FlycoTabLayout)
