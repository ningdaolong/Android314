package cn.ning.badge

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import java.lang.ref.WeakReference

/**
 * Created by MrNing
 * on 2018/7/26
 * Email Mr_Ning314@163.com
 */
class DragBadgeView(context: Context, val mBadgeViewHelper: BadgeViewHelper) : View(context) {
    private val mWindowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val TAG = DragBadgeView::class.java.simpleName

    private lateinit var mBadgePaint: Paint
    private lateinit var mLayoutParams: WindowManager.LayoutParams
    private var mStartX: Int = 0
    private var mStartY: Int = 0
    private var mExplosionAnimator: ExplosionAnimator? = null
    private val mSetExplosionAnimatorNullTask: SetExplosionAnimatorNullTask

    /**
     * 针圆切线的切点
     */
    private var mStickPoints = arrayOf(PointF(0f, 0f),PointF(0f, 0f))
    /**
     * 拖拽圆切线的切点
     */
    private var mDragPoints = arrayOf(PointF(0f, 0f), PointF(0f, 0f))
    /**
     * 控制点
     */
    private var mControlPoint = PointF(0f, 0f)
    /**
     * 拖拽圆中心点
     */
    private val mDragCenter = PointF(0f, 0f)
    /**
     * 拖拽圆半径
     */
    private var mDragRadius: Float = 0.toFloat()

    /**
     * 针圆中心点
     */
    private var mStickCenter = PointF(0f, 0f)
    /**
     * 针圆半径
     */
    private var mStickRadius: Float = 0.toFloat()
    /**
     * 拖拽圆最大半径
     */
    private var mMaxDragRadius: Int = 0
    /**
     * 拖拽圆半径和针圆半径的差值
     */
    private var mDragStickRadiusDifference: Int = 0
    /**
     * 拖动mDismissThreshold距离后抬起手指徽章消失
     */
    private var mDismissThreshold: Int = 0

    private var mDismissAble: Boolean = false
    private var mIsDragDisappear: Boolean = false

    init {
        initBadgePaint()
        initLayoutParams()
        initStick()

        mSetExplosionAnimatorNullTask = SetExplosionAnimatorNullTask(this)
    }

    private fun initBadgePaint() {
        mBadgePaint = Paint()
        mBadgePaint.isAntiAlias = true
        mBadgePaint.style = Paint.Style.FILL
        //设置mBadgeText居中，保证mBadgeText长度为1时，文本也能居中
        mBadgePaint.textAlign = Paint.Align.CENTER
        mBadgePaint.textSize = mBadgeViewHelper.getBadgeTextSize().toFloat()
    }

    private fun initLayoutParams() {
        mLayoutParams = WindowManager.LayoutParams()
        mLayoutParams.gravity = Gravity.START + Gravity.TOP
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        mLayoutParams.format = PixelFormat.TRANSLUCENT
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
    }

    private fun initStick() {
        mMaxDragRadius = BadgeViewUtil.dp2px(context, 10f)
        mDragStickRadiusDifference = BadgeViewUtil.dp2px(context, 1f)
    }

    override fun onDraw(canvas: Canvas) {
        try {
            if (mExplosionAnimator == null) {
                if (mBadgeViewHelper.isShowDrawable()) {
                    if (mBadgeViewHelper.getBadgeBgColor() == Color.RED) {
                        mBadgePaint.color = mBadgeViewHelper.getBitmap().getPixel(mBadgeViewHelper.getBitmap().getWidth() / 2, mBadgeViewHelper.getBitmap().getHeight() / 2)
                    } else {
                        mBadgePaint.color = mBadgeViewHelper.getBadgeBgColor()
                    }
                    drawStick(canvas)
                    drawDrawableBadge(canvas)
                } else {
                    mBadgePaint.color = mBadgeViewHelper.getBadgeBgColor()
                    drawStick(canvas)
                    drawTextBadge(canvas)
                }
            } else {
                mExplosionAnimator!!.draw(canvas)
            }
        } catch (e: Exception) {
            // 确保自己能被移除
            removeSelfWithException()
        }

    }

    private fun drawDrawableBadge(canvas: Canvas) {
        canvas.drawBitmap(mBadgeViewHelper.getBitmap(), mStartX.toFloat(), mStartY.toFloat(), mBadgePaint)
    }

    private fun drawTextBadge(canvas: Canvas) {
        // 设置徽章背景色
        mBadgePaint.color = mBadgeViewHelper.getBadgeBgColor()
        // 绘制徽章背景
        canvas.drawRoundRect(RectF(mStartX.toFloat(), mStartY.toFloat(), mStartX + mBadgeViewHelper.getBadgeRectF().width(), mStartY + mBadgeViewHelper.getBadgeRectF().height()), mBadgeViewHelper.getBadgeRectF().height() / 2, mBadgeViewHelper.getBadgeRectF().height() / 2, mBadgePaint)

        // 设置徽章文本颜色
        mBadgePaint.color = mBadgeViewHelper.getBadgeTextColor()
        val x = mStartX + mBadgeViewHelper.getBadgeRectF().width() / 2
        // 注意：绘制文本时的y是指文本底部，而不是文本的中间
        val y = mStartY + mBadgeViewHelper.getBadgeRectF().height() - mBadgeViewHelper.getBadgePadding()
        // 绘制徽章文本
        val badgeText = if (mBadgeViewHelper.getBadgeText() == null) "" else mBadgeViewHelper.getBadgeText()
        canvas.drawText(badgeText, x, y, mBadgePaint)
    }

    private fun drawStick(canvas: Canvas) {
        val currentStickRadius = getCurrentStickRadius()

        // 2. 获取直线与圆的交点
        val yOffset = mStickCenter.y - mDragCenter.y
        val xOffset = mStickCenter.x - mDragCenter.x
        var lineK: Double? = null
        if (xOffset != 0f) {
            lineK = (yOffset / xOffset).toDouble()
        }
        // 通过几何图形工具获取交点坐标
        mDragPoints = BadgeViewUtil.getIntersectionPoints(mDragCenter, mDragRadius, lineK)
        mStickPoints = BadgeViewUtil.getIntersectionPoints(mStickCenter, currentStickRadius, lineK)

        // 3. 获取控制点坐标
        mControlPoint = BadgeViewUtil.getMiddlePoint(mDragCenter, mStickCenter)

        // 保存画布状态
        canvas.save()
        canvas.translate(0f, (-BadgeViewUtil.getStatusBarHeight(mBadgeViewHelper.getRootView())).toFloat())

        if (!mIsDragDisappear) {
            if (!mDismissAble) {

                // 3. 画连接部分
                val path = Path()
                // 跳到点1
                path.moveTo(mStickPoints[0].x, mStickPoints[0].y)
                // 画曲线1 -> 2
                path.quadTo(mControlPoint.x, mControlPoint.y, mDragPoints[0].x, mDragPoints[0].y)
                // 画直线2 -> 3
                path.lineTo(mDragPoints[1].x, mDragPoints[1].y)
                // 画曲线3 -> 4
                path.quadTo(mControlPoint.x, mControlPoint.y, mStickPoints[1].x, mStickPoints[1].y)
                path.close()
                canvas.drawPath(path, mBadgePaint)

                // 2. 画固定圆
                canvas.drawCircle(mStickCenter.x, mStickCenter.y, currentStickRadius, mBadgePaint)
            }

            // 1. 画拖拽圆
            canvas.drawCircle(mDragCenter.x, mDragCenter.y, mDragRadius, mBadgePaint)
        }

        // 恢复上次的保存状态
        canvas.restore()
    }

    /**
     * 获取针圆实时半径
     *
     * @return
     */
    private fun getCurrentStickRadius(): Float {
        /**
         * distance 0 -> mDismissThreshold
         * percent 0.0f -> 1.0f
         * currentStickRadius mStickRadius * 100% -> mStickRadius * 20%
         */
        var distance = BadgeViewUtil.getDistanceBetween2Points(mDragCenter, mStickCenter)
        distance = Math.min(distance, mDismissThreshold.toFloat())
        val percent = distance / mDismissThreshold
        return BadgeViewUtil.evaluate(percent, mStickRadius, mStickRadius * 0.2f)
    }

    fun setStickCenter(x: Float, y: Float) {
        mStickCenter = PointF(x, y)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        try {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> handleActionDown(event)
                MotionEvent.ACTION_MOVE -> handleActionMove(event)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> handleActionUp(event)
            }
        } catch (e: Exception) {
            // 确保自己能被移除
            removeSelfWithException()
        }

        return true
    }

    private fun handleActionDown(event: MotionEvent) {
        if (mExplosionAnimator == null && parent == null) {
            mDragRadius = Math.min(mBadgeViewHelper.getBadgeRectF().width() / 2, mMaxDragRadius.toFloat())
            mStickRadius = mDragRadius - mDragStickRadiusDifference
            mDismissThreshold = (mStickRadius * 10).toInt()

            mDismissAble = false
            mIsDragDisappear = false

            mWindowManager.addView(this, mLayoutParams)

            updateDragPosition(event.rawX, event.rawY)
        }
    }

    private fun handleActionMove(event: MotionEvent) {
        if (mExplosionAnimator == null && parent != null) {
            updateDragPosition(event.rawX, event.rawY)

            // 处理断开事件
            if (BadgeViewUtil.getDistanceBetween2Points(mDragCenter, mStickCenter) > mDismissThreshold) {
                mDismissAble = true
                postInvalidate()
            } else if (mBadgeViewHelper.isResumeTravel()) {
                mDismissAble = false
                postInvalidate()
            }
        }
    }

    private fun handleActionUp(event: MotionEvent) {
        handleActionMove(event)

        if (mDismissAble) {
            // 拖拽点超出过范围
            if (BadgeViewUtil.getDistanceBetween2Points(mDragCenter, mStickCenter) > mDismissThreshold) {
                // 现在也超出范围,消失
                try {
                    mIsDragDisappear = true
                    startDismissAnim(getNewStartX(event.rawX), getNewStartY(event.rawY))
                } catch (e: Exception) {
                    removeSelf()
                    mBadgeViewHelper.endDragWithDismiss()
                }

            } else {
                // 现在没有超出范围,放回去
                removeSelf()
                mBadgeViewHelper.endDragWithoutDismiss()
            }
        } else {
            //	拖拽点没超出过范围,弹回去
            try {
                startSpringAnim()
            } catch (e: Exception) {
                removeSelf()
                mBadgeViewHelper.endDragWithoutDismiss()
            }

        }
    }

    @SuppressLint("WrongConstant")
    private fun startSpringAnim() {
        val startReleaseDragCenter = PointF(mDragCenter.x, mDragCenter.y)
        val springAnim = ValueAnimator.ofFloat(1.0f)
        springAnim.addUpdateListener { mAnim ->
            // 0.0 -> 1.0f
            val percent = mAnim.animatedFraction
            val p = BadgeViewUtil.getPointByPercent(startReleaseDragCenter, mStickCenter, percent)
            updateDragPosition(p.x, p.y)
        }
        springAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                removeSelf()
                mBadgeViewHelper.endDragWithoutDismiss()
            }

            override fun onAnimationCancel(animation: Animator) {
                removeSelf()
                mBadgeViewHelper.endDragWithoutDismiss()
            }
        })

        springAnim.interpolator = OvershootInterpolator(4f)
        springAnim.repeatCount = 1
        springAnim.repeatMode = ValueAnimator.INFINITE
        springAnim.duration = (ANIM_DURATION / 2).toLong()
        springAnim.start()
    }

    private fun startDismissAnim(newX: Int, newY: Int) {
        val badgeWidth = mBadgeViewHelper.getBadgeRectF().width().toInt()
        val badgeHeight = mBadgeViewHelper.getBadgeRectF().height().toInt()
        val rect = Rect(newX - badgeWidth / 2, newY - badgeHeight / 2, newX + badgeWidth / 2, newY + badgeHeight / 2)

        val badgeBitmap = BadgeViewUtil.createBitmapSafely(this, rect, 1)
        if (badgeBitmap == null) {
            removeSelf()
            mBadgeViewHelper.endDragWithDismiss()
            return
        }

        if (mExplosionAnimator != null) {
            removeSelf()
            mBadgeViewHelper.endDragWithDismiss()
            return
        }

        mExplosionAnimator = ExplosionAnimator(this, rect, badgeBitmap)
        mExplosionAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                removeSelf()
                mBadgeViewHelper.endDragWithDismiss()
            }

            override fun onAnimationCancel(animation: Animator) {
                removeSelf()
                mBadgeViewHelper.endDragWithDismiss()
            }
        })
        mExplosionAnimator!!.start()
    }

    private fun removeSelf() {
        if (parent != null) {
            mWindowManager.removeView(this)
        }
        mDismissAble = false
        mIsDragDisappear = false

        // 处理有时候爆炸效果结束后出现一瞬间的拖拽效果
        postDelayed(mSetExplosionAnimatorNullTask, 60)
    }

    /**
     * 修改拖拽位置
     *
     * @param rawX
     * @param rawY
     */
    private fun updateDragPosition(rawX: Float, rawY: Float) {
        mStartX = getNewStartX(rawX)
        mStartY = getNewStartY(rawY)

        mDragCenter.set(rawX, rawY)
        postInvalidate()
    }

    private fun getNewStartX(rawX: Float): Int {
        val badgeWidth = mBadgeViewHelper.getBadgeRectF().width().toInt()
        var newX = rawX.toInt() - badgeWidth / 2
        if (newX < 0) {
            newX = 0
        }
        if (newX > mWindowManager.defaultDisplay.width - badgeWidth) {
            newX = mWindowManager.defaultDisplay.width - badgeWidth
        }
        return newX
    }

    private fun getNewStartY(rawY: Float): Int {
        val badgeHeight = mBadgeViewHelper.getBadgeRectF().height().toInt()
        val maxNewY = height - badgeHeight
        val newStartY = rawY.toInt() - badgeHeight / 2 - BadgeViewUtil.getStatusBarHeight(mBadgeViewHelper.getRootView())
        return Math.min(Math.max(0, newStartY), maxNewY)
    }

    private fun removeSelfWithException() {
        removeSelf()
        if (BadgeViewUtil.getDistanceBetween2Points(mDragCenter, mStickCenter) > mDismissThreshold) {
            mBadgeViewHelper.endDragWithDismiss()
        } else {
            mBadgeViewHelper.endDragWithoutDismiss()
        }
    }

    private class SetExplosionAnimatorNullTask(dragBadgeView: DragBadgeView) : Runnable {
        private val mDragBadgeView: WeakReference<DragBadgeView> = WeakReference(dragBadgeView)

        override fun run() {
            val dragBadgeView = mDragBadgeView.get()
            if (dragBadgeView != null) {
                dragBadgeView.mExplosionAnimator = null
            }
        }
    }
}