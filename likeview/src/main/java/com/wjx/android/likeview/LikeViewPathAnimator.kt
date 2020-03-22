package com.wjx.android.likeview

import android.animation.*
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.graphics.PointF
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat
import java.util.*

/**
 * Created with Android Studio.
 * Description:
 *
 * @author: Wangjianxian
 * @date: 2020/03/22
 * Time: 17:50
 */
class LikeViewPathAnimator internal constructor(enterDuration: Int, curveDuration: Int) {
    /**
     * 进入、曲线动画时长
     */
    private val mEnterDuration: Int
    private val mCurveDuration: Int

    /**
     * picture宽高
     */
    private var mPicWidth = 0
    private var mPicHeight = 0

    /**
     * View 宽高
     */
    private var mViewWidth = 0
    private var mViewHeight = 0

    /**
     * 已生成的路径数目
     */
    private var mPathCounts = 0

    @JvmField
    var mRandom: Random

    /**
     * 已生成的路径缓存
     */
    private val mPathCacheArray: SparseArray<CurveEvaluator>
    fun setPic(picWidth: Int, picHeight: Int) {
        mPicWidth = picWidth
        mPicHeight = picHeight
    }

    fun setView(viewWidth: Int, viewHeight: Int) {
        mViewWidth = viewWidth
        mViewHeight = viewHeight
    }

    /**
     * View出场动画
     *
     * @param target
     * @return
     */
    private fun generateEnterAnimation(target: View): AnimatorSet {
        val alpha = ObjectAnimator.ofFloat(target, "alpha", 0.2f, 1f)
        val scaleX = ObjectAnimator.ofFloat(target, "scaleX", 0.2f, 0.5f)
        val scaleY = ObjectAnimator.ofFloat(target, "scaleY", 0.2f, 0.5f)
        val enterAnimation = AnimatorSet()
        // 加一些动画差值器
        enterAnimation.setInterpolator(CustomScaleInterpolator(0.5f))
        enterAnimation.playTogether(alpha, scaleX, scaleY)
        enterAnimation.duration = mEnterDuration.toLong()
        return enterAnimation
    }

    /**
     * 贝塞尔曲线动画
     *
     * @param target
     * @return
     */
    private fun generateCurveAnimation(
        evaluator: CurveEvaluator,
        target: View
    ): ValueAnimator? {
        val valueAnimator = ValueAnimator.ofObject(
            evaluator,
            PointF(
                ((mViewWidth - mPicWidth) / 2).toFloat(),
                (mViewHeight - mPicHeight).toFloat()
            ),
            PointF(
                ((mViewWidth - mPicWidth)/ 2 + (if (mRandom.nextBoolean()) 1 else -1) * mRandom.nextInt(100)).toFloat(),
                0.0f
            )
        )
        valueAnimator.duration = mCurveDuration.toLong()
        valueAnimator.addUpdateListener({
            val pointF = it.animatedValue as PointF
            target.setX(pointF.x)
            target.setY(pointF.y)
            // 跟随属性动画执行进度改变对象的透明度
            ViewCompat.setAlpha(target, 1 - it.animatedFraction)
        })

        return valueAnimator
    }

    private fun generateCTRLPointF(value: Int): PointF {
        val pointF = PointF()
        pointF.x = mRandom.nextInt(mViewWidth - 100).toFloat()
        pointF.y = (mRandom.nextInt(mViewHeight - 100) / value).toFloat()
        return pointF
    }

    /**
     * 动画结束监听器,用于释放无用的资源
     */
    private inner class AnimationEndListener(
        private val target: View,
        private val parent: ViewGroup
    ) : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            parent.removeView(target)
        }

    }

    private inner class CurveEvaluator(// 三阶贝塞尔曲线需要两个控制点
        private val ctrlPointF1: PointF, private val ctrlPointF2: PointF
    ) :
        TypeEvaluator<PointF> {
        override fun evaluate(
            fraction: Float,
            startValue: PointF,
            endValue: PointF
        ): PointF {
            val leftTime = 1.0f - fraction
            val resultPointF = PointF()
            // 三阶贝塞尔曲线公式（可以百度到嗷）
            resultPointF.x =
                Math.pow(leftTime.toDouble(), 3.0).toFloat() * startValue.x + 3 * Math.pow(
                    leftTime.toDouble(),
                    2.0
                ).toFloat() * fraction * ctrlPointF1.x + 3 * leftTime * Math.pow(
                    fraction.toDouble(),
                    2.0
                ).toFloat() * ctrlPointF2.x + Math.pow(
                    fraction.toDouble(),
                    3.0
                ).toFloat() * endValue.x
            resultPointF.y =
                Math.pow(leftTime.toDouble(), 3.0).toFloat() * startValue.y + 3 * Math.pow(
                        leftTime.toDouble(),
                        2.0
                    )
                    .toFloat() * fraction * ctrlPointF1.y + 3 * leftTime * fraction * fraction * ctrlPointF2.y + Math.pow(
                    fraction.toDouble(),
                    3.0
                ).toFloat() * endValue.y
            return resultPointF
        }

    }

    fun startAnimation(
        target: View,
        parent: ViewGroup,
        layoutParams: RelativeLayout.LayoutParams?
    ) {
        parent.addView(target, layoutParams)
        ++mPathCounts
        val evaluator: CurveEvaluator
        // 如果已经生成的路径数目超过最大设定，就从路径缓存中随机取一个路径用于绘制，否则新生成一个
        if (mPathCounts > 10) {
            evaluator = mPathCacheArray[Math.abs(mRandom.nextInt() % 10) + 1]
        } else {
            evaluator = CurveEvaluator(generateCTRLPointF(1), generateCTRLPointF(2))
            mPathCacheArray.put(mPathCounts, evaluator)
        }
        val enterAnimator = generateEnterAnimation(target)
        val curveAnimator = generateCurveAnimation(evaluator, target)
        val finalAnimatorSet = AnimatorSet()
        finalAnimatorSet.setTarget(target)
        finalAnimatorSet.playTogether(enterAnimator, curveAnimator)
        finalAnimatorSet.addListener(AnimationEndListener(target, parent))
        finalAnimatorSet.start()
    }

    init {
        mPathCacheArray = SparseArray()
        mEnterDuration = enterDuration
        mCurveDuration = curveDuration
        mRandom = Random()
    }
}