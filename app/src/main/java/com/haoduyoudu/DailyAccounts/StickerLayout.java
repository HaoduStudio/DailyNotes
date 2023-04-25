package com.haoduyoudu.DailyAccounts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import static com.haoduyoudu.DailyAccounts.MyApplication.MAX_STICKER_COUNT;

/**
 * 贴纸布局（管理分发各种贴纸处理事件）
 * Create by: chenWei.li  && haoduyoudu_dev
 * Date: 2019/2/3
 * Time: 6:57 PM
 * Email: lichenwei.me@foxmail.com
 */
public class StickerLayout extends View implements View.OnTouchListener {

    private Context mContext;
    private Paint mPaint;

    //记录当前操作的贴纸对象
    private Sticker mStick;
    private Boolean canEdit = false;
    private Boolean isEditing = false;
    private Boolean isSkOnMove = false;

    private OnPopEditListener mPop = null;
    private OnPopEditListener mPopclose = null;

    private OnMoveSkListener mMovesk = null;

    private Boolean isclosepop = true;

    public void setCanEdit(Boolean cd) {
        this.canEdit = cd;
        if(!cd)
            this.isEditing = false;
    }

    public StickerLayout(Context context) {
        super(context);
        init(context);
    }

    public StickerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StickerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化操作
     */
    private void init(Context context) {
        this.mContext = context;
        //设置触摸监听
        setOnTouchListener(this);
    }

    public Paint getPaint() {
        if (mPaint == null) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(Color.BLACK);
            mPaint.setStrokeWidth(2);
        }
        return mPaint;
    }

    public void setPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }


    /**
     * 添加贴纸
     *
     * @param sticker
     */
    public void addSticker(Sticker sticker) {
        int size = StickerManager.getInstance().getStickerList().size();
        if (size < MAX_STICKER_COUNT) {
            StickerManager.getInstance().addSticker(sticker);
            if(canEdit)
                StickerManager.getInstance().setFocusSticker(sticker);
            invalidate();
        } else {
            Toast.makeText(mContext, MyApplication.context.getString(R.string.stickercount_exceed,MAX_STICKER_COUNT), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 移除贴纸（只有在贴纸聚焦的时候才可以删除，避免误触碰）
     *
     * @param sticker
     */
    public void removeSticker(Sticker sticker) {
        if (sticker.isFocus()) {
            StickerManager.getInstance().removeSticker(sticker);
            invalidate();
        }
    }

    /**
     * 返回贴纸的矩阵，保存用
     * by haoduyoudu_dev
     * @param sticker
     */
    public Matrix returnMatrix(Sticker sticker) {
        return sticker.getMatrix();
    }

    /**
     * 清空贴纸
     */
    public void removeAllSticker() {
        StickerManager.getInstance().removeAllSticker();
        invalidate();
    }

    /**
     * 清空所有焦点
     */
    public void cleanAllFocus() {
        StickerManager.getInstance().clearAllFocus();
        invalidate();

    }

    /**
     * 获取有焦点的贴纸
     */
    public Sticker getFocusSticker() {
        return StickerManager.getInstance().getFocusSticker();
    }
    public void setFocusSticker(Sticker focus){ StickerManager.getInstance().setFocusSticker(focus); }

    public Sticker getTopSticker(){
        List<Sticker> sg = StickerManager.getInstance().getStickerList();
        int minnum = 100000;
        Sticker topsk = null;
        for(Sticker sk:sg){
            if(sk.getMidXy().y < minnum){
                minnum = (int) sk.getMidXy().y;
                topsk = sk;
            };
        }
        return topsk;
    }

    /**
     * 缩放操作
     */
    public void scaleSticker(Sticker mSticker,float sx,float sy) {
        StickerManager.getInstance().scaleSticker(mSticker,sx,sy);
        invalidate();
    }

    /**
     * 旋转操作
     */
    public void rotateSticker(Sticker mSticker,float degrees) {
        StickerManager.getInstance().rotateSticker(mSticker,degrees);
        invalidate();
    }

    /**
     * 旋转操作
     */
    public List<Sticker> returnAllSticker() {
        return StickerManager.getInstance().getStickerList();
    }

    public void updata() {
        StickerManager.getInstance().upalldata();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        List<Sticker> stickerList = StickerManager.getInstance().getStickerList();
        Sticker focusSticker = null;
        for (int i = 0; i < stickerList.size(); i++) {
            Sticker sticker = stickerList.get(i);
            if (sticker.isFocus()) {
                focusSticker = sticker;
            } else {
                sticker.onDraw(canvas, getPaint());
            }
        }
        if (focusSticker != null) {
            focusSticker.onDraw(canvas, getPaint());
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                v.requestFocus();
                if(StickerManager.getInstance().getSticker(event.getX(), event.getY())!=null)
                    isEditing = true;
                else
                    isEditing = false;

                if(!isEditing && !isclosepop && canEdit){
                    mPopclose.run();
                    isclosepop = true;
                }else if(isEditing && isclosepop && canEdit){
                    mPop.run();
                    isclosepop = false;
                }
            case MotionEvent.ACTION_POINTER_DOWN:
                //判断是否按到删除按钮
                //mStick = StickerManager.getInstance().getDelButton(event.getX(), event.getY());
                //if (mStick != null) {
                //    removeSticker(mStick);
                //    mStick = null;
                //}
                //单指是否触摸到贴纸
                mStick = StickerManager.getInstance().getSticker(event.getX(), event.getY());
                if (mStick == null) {
                    if (event.getPointerCount() == 2) {
                        //处理双指触摸屏幕，第一指没有触摸到贴纸，第二指触摸到贴纸情况
                        mStick = StickerManager.getInstance().getSticker(event.getX(1), event.getY(1));
                    }
                }
                if (mStick != null && canEdit) {
                    StickerManager.getInstance().setFocusSticker(mStick);
                    if(!isEditing && !isclosepop){
                        mPopclose.run();
                        isclosepop = true;
                    }
                    isEditing = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!isclosepop && StickerManager.getInstance().getSticker(event.getX(), event.getY())!=null && canEdit && isEditing){
                    mPopclose.run();
                    isclosepop = true;
                    isSkOnMove = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isSkOnMove = false;
                Log.d("mStickerLayout","onUp");
                break;
            default:
                break;

        }
        if(isSkOnMove && canEdit && mStick != null) mMovesk.move(mStick.getMidXy().x,mStick.getMidXy().y);
        if (mStick != null && isEditing) {
            mStick.onTouch(event);
        } else {
            StickerManager.getInstance().clearAllFocus();
        }
        invalidate();
        if(isEditing && canEdit){
            return true;
        }else{
            return false;
        }
    }

    public interface OnPopEditListener {
        public void run();
    }
    public void setOnPopEditListener(OnPopEditListener pop) {
        mPop = pop;
    }
    public void setOnPopcloseEditListener(OnPopEditListener pop) {
        mPopclose = pop;
    }

    public interface OnMoveSkListener {
        public void move(float x,float y);
    }
    public void setOnMoveSkListener(OnMoveSkListener move) {
        mMovesk = move;
    }

}
