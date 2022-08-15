package com.haoduyoudu.DailyAccounts

import android.content.Context

// 获取顶部圆角 radius
fun getCornerRadiusTop(context: Context):Int {
    var radius = 0;
    val resourceId =
        context.getResources().getIdentifier("rounded_corner_radius_top", "dimen", "android");
    if (resourceId > 0) {
        radius = context.getResources().getDimensionPixelSize(resourceId);
        return radius;
    }
    return 16
}


//标注：这一版完全未由Rechrd & Rex修改过
//完全由原作者修改