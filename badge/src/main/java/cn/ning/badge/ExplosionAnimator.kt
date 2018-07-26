package cn.ning.badge

import android.animation.ValueAnimator
import android.graphics.*
import android.view.animation.AccelerateInterpolator
import java.util.*

/**
 * Created by MrNing
 * on 2018/7/26
 * Email Mr_Ning314@163.com
 */
internal val ANIM_DURATION = 300
class ExplosionAnimator(dragBadgeView: DragBadgeView, rect: Rect, bitmap: Bitmap) : ValueAnimator() {
    private val DEFAULT_INTERPOLATOR = AccelerateInterpolator(0.6f)
    private val END_VALUE = 1.4f
    private val REFRESH_RATIO = 3
    private var X: Float
    private var Y: Float
    private var V: Float
    private var W: Float

    private val partLen = 15
    private var mParticles = Array(partLen * partLen) { Particle() }
    private var mPaint: Paint
    private var mDragBadgeView: DragBadgeView
    private var mRect: Rect
    private var mInvalidateRect: Rect

    init {
        setFloatValues(0.0f, END_VALUE)
        duration = ANIM_DURATION.toLong()
        interpolator = DEFAULT_INTERPOLATOR

        X = BadgeViewUtil.dp2px(dragBadgeView.context, 5f).toFloat()
        Y = BadgeViewUtil.dp2px(dragBadgeView.context, 20f).toFloat()
        V = BadgeViewUtil.dp2px(dragBadgeView.context, 2f).toFloat()
        W = BadgeViewUtil.dp2px(dragBadgeView.context, 1f).toFloat()

        mPaint = Paint()
        mDragBadgeView = dragBadgeView
        mRect = rect
        mInvalidateRect = Rect(mRect.left - mRect.width() * REFRESH_RATIO, mRect.top - mRect.height() * REFRESH_RATIO, mRect.right + mRect.width() * REFRESH_RATIO, mRect.bottom + mRect.height() * REFRESH_RATIO)

        val random = Random(System.currentTimeMillis())
        val w = bitmap.width / (partLen + 2)
        val h = bitmap.height / (partLen + 2)
        for (i in 0 until partLen) {
            for (j in 0 until partLen) {
                mParticles[i * partLen + j] = generateParticle(bitmap.getPixel((j + 1) * w, (i + 1) * h), random)
            }
        }
    }

    private fun generateParticle(color: Int, random: Random): Particle {
        val particle = Particle()
        particle.color = color
        particle.radius = V
        if (random.nextFloat() < 0.2f) {
            particle.baseRadius = V + (X - V) * random.nextFloat()
        } else {
            particle.baseRadius = W + (V - W) * random.nextFloat()
        }
        val nextFloat = random.nextFloat()
        particle.top = mRect.height() * (0.18f * random.nextFloat() + 0.2f)
        particle.top = if (nextFloat < 0.2f) particle.top else particle.top + particle.top * 0.2f * random.nextFloat()
        particle.bottom = mRect.height() * (random.nextFloat() - 0.5f) * 1.8f
        var f = if (nextFloat < 0.2f) particle.bottom else if (nextFloat < 0.8f) particle.bottom * 0.6f else particle.bottom * 0.3f
        particle.bottom = f
        particle.mag = 4.0f * particle.top / particle.bottom
        particle.neg = -particle.mag / particle.bottom
        f = mRect.centerX() + Y * (random.nextFloat() - 0.5f)
        particle.baseCx = f + mRect.width() / 2
        particle.cx = particle.baseCx
        f = mRect.centerY() + Y * (random.nextFloat() - 0.5f)
        particle.baseCy = f
        particle.cy = f
        particle.life = END_VALUE / 10 * random.nextFloat()
        particle.overflow = 0.4f * random.nextFloat()
        particle.alpha = 1f
        return particle
    }

    fun draw(canvas: Canvas) {
        if (!isStarted) {
            return
        }
        for (particle in mParticles) {
            particle.advance(animatedValue as Float)
            if (particle.alpha > 0f) {
                mPaint.color = particle.color
                mPaint.alpha = (Color.alpha(particle.color) * particle.alpha).toInt()
                canvas.drawCircle(particle.cx, particle.cy, particle.radius, mPaint)
            }
        }
        postInvalidate()
    }

    override fun start() {
        super.start()
        postInvalidate()
    }

    /**
     * 只刷新徽章附近的区域
     */
    private fun postInvalidate() {
        mDragBadgeView.postInvalidate(mInvalidateRect.left, mInvalidateRect.top, mInvalidateRect.right, mInvalidateRect.bottom)
    }


    private inner class Particle {
        internal var alpha: Float = 0.toFloat()
        internal var color: Int = 0
        internal var cx: Float = 0.toFloat()
        internal var cy: Float = 0.toFloat()
        internal var radius: Float = 0.toFloat()
        internal var baseCx: Float = 0.toFloat()
        internal var baseCy: Float = 0.toFloat()
        internal var baseRadius: Float = 0.toFloat()
        internal var top: Float = 0.toFloat()
        internal var bottom: Float = 0.toFloat()
        internal var mag: Float = 0.toFloat()
        internal var neg: Float = 0.toFloat()
        internal var life: Float = 0.toFloat()
        internal var overflow: Float = 0.toFloat()

        fun advance(factor: Float) {
            var f = 0f
            var normalization = factor / END_VALUE
            if (normalization < life || normalization > 1f - overflow) {
                alpha = 0f
                return
            }
            normalization = (normalization - life) / (1f - life - overflow)
            val f2 = normalization * END_VALUE
            if (normalization >= 0.7f) {
                f = (normalization - 0.7f) / 0.3f
            }
            alpha = 1f - f
            f = bottom * f2
            cx = baseCx + f
            cy = (baseCy - this.neg * Math.pow(f.toDouble(), 2.0)).toFloat() - f * mag
            radius = V + (baseRadius - V) * f2
        }
    }
}