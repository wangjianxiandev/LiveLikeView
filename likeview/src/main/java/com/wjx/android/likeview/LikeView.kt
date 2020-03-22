package com.wjx.android.likeview

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.RelativeLayout
import java.util.*

/**
 * Created with Android Studio.
 * Description:
 *
 * @author: Wangjianxian
 * @date: 2020/03/22
 * Time: 15:37
 */
class LikeView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null
) : RelativeLayout(context, attrs, 0) {
    private var mLikeDrawables: MutableList<Int>? = null
    private var mLayoutParams: LayoutParams? = null
    private var mLikeViewPathAnimator: LikeViewPathAnimator? = null
    private fun init(defaultFavor: Int, enterDuration: Int, curveDuration: Int) {
        var defaultFavor = defaultFavor
        mLikeDrawables = ArrayList()
        if (defaultFavor == -1) {
            defaultFavor = R.drawable.zan0
        }
        val drawable: Drawable = BitmapDrawable(
            resources,
            BitmapFactory.decodeResource(resources, defaultFavor)
        )
        // 获取图片的宽高, 由于图片大小一致,故直接获取第一张图片的宽高
        val picWidth = drawable.intrinsicWidth
        val picHeight = drawable.intrinsicHeight

        // 初始化布局参数
        mLayoutParams = LayoutParams(picWidth+10, picHeight+10)
        mLayoutParams!!.addRule(CENTER_HORIZONTAL)
        mLayoutParams!!.addRule(ALIGN_PARENT_BOTTOM)
        mLikeViewPathAnimator = LikeViewPathAnimator(enterDuration, curveDuration)
        mLikeViewPathAnimator!!.setPic(picWidth, picHeight)
    }

    fun addLikeImage(resId: Int) {
        mLikeDrawables!!.add(resId)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mLikeViewPathAnimator!!.setView(width, height)
    }

    /**
     * 及时获取View高度
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mLikeViewPathAnimator!!.setView(width, height)
    }

    fun addLikeView() {
        val likeView = ImageView(context)
        likeView.setImageResource(
            mLikeDrawables!![mLikeViewPathAnimator!!.mRandom.nextInt(
                mLikeDrawables!!.size
            )]
        )
        likeView.layoutParams = mLayoutParams
        mLikeViewPathAnimator!!.startAnimation(likeView, this, mLayoutParams)
    }

    init {
        val typedArray =
            getContext().obtainStyledAttributes(attrs, R.styleable.LikeView)
        // 默认图片 根据他获取飘心图的宽高 *重要
        val defaultFavor = typedArray.getResourceId(R.styleable.LikeView_default_image, -1)
        // 进入动画时长
        val enterDuration = typedArray.getInteger(R.styleable.LikeView_enter_duration, 1500)
        // 曲线动画时长
        val curveDuration = typedArray.getInteger(R.styleable.LikeView_curve_duration, 4500)
        typedArray.recycle()
        init(defaultFavor, enterDuration, curveDuration)
    }
}