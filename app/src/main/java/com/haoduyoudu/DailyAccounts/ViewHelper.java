package com.haoduyoudu.DailyAccounts;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.RequiresApi;


public class ViewHelper {

    public static final int RADIUS_ALL = 0;
    public static final int RADIUS_LEFT = 1;
    public static final int RADIUS_TOP = 2;
    public static final int RADIUS_RIGHT = 3;
    public static final int RADIUS_BOTTOM = 4;

    public static void setViewOutline(View view, AttributeSet attributes, int defStyleAttr, int defStyleRes) {
        TypedArray array = view.getContext().obtainStyledAttributes(attributes, R.styleable.viewOutLineStrategy, defStyleAttr, defStyleRes);
        int radius = array.getDimensionPixelSize(R.styleable.viewOutLineStrategy_clip_radius, 0);
        int hideSide = array.getInt(R.styleable.viewOutLineStrategy_clip_side, 0);
        array.recycle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setViewOutline(view, radius, hideSide);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setViewOutline(View owner, final int radius, final int radiusSide) {
        owner.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            @TargetApi(21)
            public void getOutline(View view, Outline outline) {
                int w = view.getWidth(), h = view.getHeight();
                if (w == 0 || h == 0) {
                    return;
                }

                if (radiusSide != RADIUS_ALL) {
                    int left = 0, top = 0, right = w, bottom = h;
                    if (radiusSide == RADIUS_LEFT) {
                        right += radius;
                    } else if (radiusSide == RADIUS_TOP) {
                        bottom += radius;
                    } else if (radiusSide == RADIUS_RIGHT) {
                        left -= radius;
                    } else if (radiusSide == RADIUS_BOTTOM) {
                        top -= radius;
                    }
                    outline.setRoundRect(left, top, right, bottom, radius);
                    return;
                }

                int top = 0, bottom = h, left = 0, right = w;
                if (radius <= 0) {
                    outline.setRect(left, top, right, bottom);
                } else {
                    outline.setRoundRect(left, top, right, bottom, radius);
                }
            }
        });
        //开启剪裁
        owner.setClipToOutline(radius > 0);
        owner.invalidate();
    }
    public static Bitmap getMagicDrawingCache(Context context, View view, boolean quick_cache) {
        Bitmap bitmap = (Bitmap) view.getTag(R.id.cacheBitmapKey);
        Boolean dirty = (Boolean) view.getTag(R.id.cacheBitmapDirtyKey);
        if (view.getWidth() + view.getHeight() == 0) {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();
        if (bitmap == null || bitmap.getWidth() != viewWidth || bitmap.getHeight() != viewHeight) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
            view.setTag(R.id.cacheBitmapKey, bitmap);
            dirty = true;
        }
        if (dirty == true || !quick_cache) {
            bitmap.eraseColor(context.getResources().getColor(android.R.color.transparent));
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            view.setTag(R.id.cacheBitmapDirtyKey, false);
        }
        return bitmap;
    }

}