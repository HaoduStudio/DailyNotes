package com.haoduyoudu.DailyAccounts;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CleanRAMhelper {
    public void releaseImageViewResouce(ImageView imageView) {
        if (imageView == null) return;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            //将imageView从父容器移除，并将imag置为null
            if (imageView != null) {
                removeSelfFromParent(imageView);
                imageView = null;
            }
        }
    }
    public void removeSelfFromParent (View child){
        if (child != null) {
            ViewGroup parent = (ViewGroup) child.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                parent.removeView(child);
                Log.d("removeSelfFromParent","删除View*1");
            }
        }
    }
}
