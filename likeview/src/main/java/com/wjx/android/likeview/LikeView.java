package com.wjx.android.likeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import android.widget.ImageView;
import android.widget.RelativeLayout;


import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio.
 * Description:
 *
 * @author: Wangjianxian
 * @date: 2020/03/22
 * Time: 15:37
 */
public class LikeView extends RelativeLayout {
    private List<Drawable> mLikeDrawables;

    private LayoutParams mLayoutParams;

    public int mChildViewHeight = 0;

    private LikeViewPathAnimator mLikeViewPathAnimator;

    public LikeView(Context context) {
        this(context, null);
    }

    public LikeView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LikeView);
        // 默认图片 根据他获取飘心图的宽高 *重要
        int defaultFavor = typedArray.getResourceId(R.styleable.LikeView_default_image, -1);
        // 进入动画时长
        int enterDuration = typedArray.getInteger(R.styleable.LikeView_enter_duration, 1500);
        // 曲线动画时长
        int curveDuration = typedArray.getInteger(R.styleable.LikeView_curve_duration, 4500);

        typedArray.recycle();

        init(defaultFavor, enterDuration, curveDuration);
    }

    private void init(int defaultFavor, int enterDuration, int curveDuration) {
        mLikeDrawables = new ArrayList<>();
        if (defaultFavor == -1) {
            defaultFavor = R.drawable.heart0;
        }
        Drawable drawable = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), defaultFavor));
        // 获取图片的宽高, 由于图片大小一致,故直接获取第一张图片的宽高
        int picWidth = drawable.getIntrinsicWidth();
        int picHeight = drawable.getIntrinsicHeight();

        // 初始化布局参数
        this.mLayoutParams = new LayoutParams(picWidth - 10, picHeight - 10);
        this.mLayoutParams.addRule(CENTER_HORIZONTAL);
        this.mLayoutParams.addRule(ALIGN_PARENT_BOTTOM);
        this.mLikeViewPathAnimator = new LikeViewPathAnimator(enterDuration, curveDuration);
        this.mLikeViewPathAnimator.setPic(picWidth, picHeight);
    }

    public void addLikeImage(Drawable resId) {
        this.mLikeDrawables.add(resId);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mLikeViewPathAnimator.setView(getWidth(), getHeight());
    }

    /**
     * 及时获取View高度
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mLikeViewPathAnimator.setView(getWidth(), getHeight());
    }

    public void addLikeView() {
        ImageView likeView = new ImageView(getContext());
        likeView.setImageDrawable(mLikeDrawables.get(mLikeViewPathAnimator.mRandom.nextInt(mLikeDrawables.size())));
        likeView.setLayoutParams(mLayoutParams);
        mLikeViewPathAnimator.startAnimation(likeView, this,mLayoutParams);
    }


}
