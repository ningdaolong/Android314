package cn.ning.badge

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by MrNing
 * on 2018/7/26
 * Email Mr_Ning314@163.com
 */
class BadgeViewHelper(private val mBadge: BaseBadge, context: Context, attrs: AttributeSet, defaultBadgeGravity: BadgeGravity) {
    private lateinit var mBitmap: Bitmap
    private lateinit var mBadgePaint: Paint

    /**
     * 徽章背景色
     */
    private var mBadgeBgColor: Int = 0
    /**
     * 徽章文本的颜色
     */
    private var mBadgeTextColor: Int = 0
    /**
     * 徽章文本字体大小
     */
    private var mBadgeTextSize: Int = 0
    /**
     * 徽章背景与宿主控件上下边缘间距离
     */
    private var mBadgeVerticalMargin: Int = 0
    /**
     * 徽章背景与宿主控件左右边缘间距离
     */
    private var mBadgeHorizontalMargin: Int = 0
    /***
     * 徽章文本边缘与徽章背景边缘间的距离
     */
    private var mBadgePadding: Int = 0
    /**
     * 徽章文本
     */
    private var mBadgeText: String? = null
    /**
     * 徽章文本所占区域大小
     */
    private lateinit var mBadgeNumberRect: Rect
    /**
     * 是否显示Badge
     */
    private var mIsShowBadge: Boolean = false
    /**
     * 徽章在宿主控件中的位置
     */
    private lateinit var mBadgeGravity: BadgeGravity
    /**
     * 整个徽章所占区域
     */
    private lateinit var mBadgeRectF: RectF
    /**
     * 是否可拖动
     */
    private var mDragable: Boolean = false
    /**
     * 拖拽徽章超出轨迹范围后，再次放回到轨迹范围时，是否恢复轨迹
     */
    private var mIsResumeTravel: Boolean = false
    /***
     * 徽章描边宽度
     */
    private var mBadgeBorderWidth: Int = 0
    /***
     * 徽章描边颜色
     */
    private var mBadgeBorderColor: Int = 0
    /**
     * 触发开始拖拽徽章事件的扩展触摸距离
     */
    private var mDragExtra: Int = 0
    /**
     * 整个徽章加上其触发开始拖拽区域所占区域
     */
    private lateinit var mBadgeDragExtraRectF: RectF
    /**
     * 拖动时的徽章控件
     */
    private var mDropBadgeView: DragBadgeView
    /**
     * 是否正在拖动
     */
    private var mIsDraging: Boolean = false
    /**
     * 拖动大于BGABadgeViewHelper.mMoveHiddenThreshold后抬起手指徽章消失的代理
     */
    private var mDelegage: DragDismissDelegate? = null
    private var mIsShowDrawable = false

    init {
        initDefaultAttrs(context, defaultBadgeGravity)
        initCustomAttrs(context, attrs)
        afterInitDefaultAndCustomAttrs()
        mDropBadgeView = DragBadgeView(context, this)
    }

    private fun initDefaultAttrs(context: Context, defaultBadgeGravity: BadgeGravity) {
        mBadgeNumberRect = Rect()
        mBadgeRectF = RectF()
        mBadgeBgColor = Color.RED
        mBadgeTextColor = Color.WHITE
        mBadgeTextSize = BadgeViewUtil.sp2px(context, 10f)

        mBadgePaint = Paint()
        mBadgePaint.isAntiAlias = true
        mBadgePaint.style = Paint.Style.FILL
        // 设置mBadgeText居中，保证mBadgeText长度为1时，文本也能居中
        mBadgePaint.textAlign = Paint.Align.CENTER

        mBadgePadding = BadgeViewUtil.dp2px(context, 4f)
        mBadgeVerticalMargin = BadgeViewUtil.dp2px(context, 4f)
        mBadgeHorizontalMargin = BadgeViewUtil.dp2px(context, 4f)

        mBadgeGravity = defaultBadgeGravity
        mIsShowBadge = false
        mBadgeText = null
        mIsDraging = false
        mDragable = false
        mBadgeBorderColor = Color.WHITE

        mDragExtra = BadgeViewUtil.dp2px(context, 4f)
        mBadgeDragExtraRectF = RectF()
    }

    private fun initCustomAttrs(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BadgeView)
        val N = typedArray.indexCount
        for (i in 0 until N) {
            initCustomAttr(typedArray.getIndex(i), typedArray)
        }
        typedArray.recycle()
    }

    private fun initCustomAttr(attr: Int, typedArray: TypedArray) {
        when (attr) {
            R.styleable.BadgeView_badge_bgColor -> mBadgeBgColor = typedArray.getColor(attr, mBadgeBgColor)
            R.styleable.BadgeView_badge_textColor -> mBadgeTextColor = typedArray.getColor(attr, mBadgeTextColor)
            R.styleable.BadgeView_badge_textSize -> mBadgeTextSize = typedArray.getDimensionPixelSize(attr, mBadgeTextSize)
            R.styleable.BadgeView_badge_verticalMargin -> mBadgeVerticalMargin = typedArray.getDimensionPixelSize(attr, mBadgeVerticalMargin)
            R.styleable.BadgeView_badge_horizontalMargin -> mBadgeHorizontalMargin = typedArray.getDimensionPixelSize(attr, mBadgeHorizontalMargin)
            R.styleable.BadgeView_badge_padding -> mBadgePadding = typedArray.getDimensionPixelSize(attr, mBadgePadding)
            R.styleable.BadgeView_badge_gravity -> {
                val ordinal = typedArray.getInt(attr, mBadgeGravity.ordinal)
                mBadgeGravity = BadgeGravity.values()[ordinal]
            }
            R.styleable.BadgeView_badge_dragable -> mDragable = typedArray.getBoolean(attr, mDragable)
            R.styleable.BadgeView_badge_isResumeTravel -> mIsResumeTravel = typedArray.getBoolean(attr, mIsResumeTravel)
            R.styleable.BadgeView_badge_borderWidth -> mBadgeBorderWidth = typedArray.getDimensionPixelSize(attr, mBadgeBorderWidth)
            R.styleable.BadgeView_badge_borderColor -> mBadgeBorderColor = typedArray.getColor(attr, mBadgeBorderColor)
            R.styleable.BadgeView_badge_dragExtra -> mDragExtra = typedArray.getDimensionPixelSize(attr, mDragExtra)
        }
    }

    private fun afterInitDefaultAndCustomAttrs() {
        mBadgePaint.textSize = mBadgeTextSize.toFloat()
    }

    fun setBadgeBgColorInt(badgeBgColor: Int) {
        mBadgeBgColor = badgeBgColor
        mBadge.postInvalidate()
    }

    fun setBadgeTextColorInt(badgeTextColor: Int) {
        mBadgeTextColor = badgeTextColor
        mBadge.postInvalidate()
    }

    fun setBadgeTextSizeSp(badgetextSize: Int) {
        if (badgetextSize >= 0) {
            mBadgeTextSize = BadgeViewUtil.sp2px(mBadge.getContext(), badgetextSize.toFloat())
            mBadgePaint.textSize = mBadgeTextSize.toFloat()
            mBadge.postInvalidate()
        }
    }

    fun setBadgeVerticalMarginDp(badgeVerticalMargin: Int) {
        if (badgeVerticalMargin >= 0) {
            mBadgeVerticalMargin = BadgeViewUtil.dp2px(mBadge.getContext(), badgeVerticalMargin.toFloat())
            mBadge.postInvalidate()
        }
    }

    fun setBadgeHorizontalMarginDp(badgeHorizontalMargin: Int) {
        if (badgeHorizontalMargin >= 0) {
            mBadgeHorizontalMargin = BadgeViewUtil.dp2px(mBadge.getContext(), badgeHorizontalMargin.toFloat())
            mBadge.postInvalidate()
        }
    }

    fun setBadgePaddingDp(badgePadding: Int) {
        if (badgePadding >= 0) {
            mBadgePadding = BadgeViewUtil.dp2px(mBadge.getContext(), badgePadding.toFloat())
            mBadge.postInvalidate()
        }
    }

    fun setBadgeGravity(badgeGravity: BadgeGravity?) {
        if (badgeGravity != null) {
            mBadgeGravity = badgeGravity
            mBadge.postInvalidate()
        }
    }

    fun setDragable(dragable: Boolean) {
        mDragable = dragable
        mBadge.postInvalidate()
    }

    fun setIsResumeTravel(isResumeTravel: Boolean) {
        mIsResumeTravel = isResumeTravel
        mBadge.postInvalidate()
    }

    fun setBadgeBorderWidthDp(badgeBorderWidthDp: Int) {
        if (badgeBorderWidthDp >= 0) {
            mBadgeBorderWidth = BadgeViewUtil.dp2px(mBadge.getContext(), badgeBorderWidthDp.toFloat())
            mBadge.postInvalidate()
        }
    }

    fun setBadgeBorderColorInt(badgeBorderColor: Int) {
        mBadgeBorderColor = badgeBorderColor
        mBadge.postInvalidate()
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mBadgeDragExtraRectF.left = mBadgeRectF.left - mDragExtra
                mBadgeDragExtraRectF.top = mBadgeRectF.top - mDragExtra
                mBadgeDragExtraRectF.right = mBadgeRectF.right + mDragExtra
                mBadgeDragExtraRectF.bottom = mBadgeRectF.bottom + mDragExtra

                if ((mBadgeBorderWidth == 0 || mIsShowDrawable) && mDragable && mIsShowBadge && mBadgeDragExtraRectF.contains(event.x, event.y)) {
                    mIsDraging = true
                    mBadge.getParent().requestDisallowInterceptTouchEvent(true)

                    val badgeableRect = Rect()
                    mBadge.getGlobalVisibleRect(badgeableRect)
                    mDropBadgeView.setStickCenter(badgeableRect.left.toFloat() + mBadgeRectF.left + mBadgeRectF.width() / 2, badgeableRect.top.toFloat() + mBadgeRectF.top + mBadgeRectF.height() / 2)

                    mDropBadgeView.onTouchEvent(event)
                    mBadge.postInvalidate()
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> if (mIsDraging) {
                mDropBadgeView.onTouchEvent(event)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> if (mIsDraging) {
                mDropBadgeView.onTouchEvent(event)
                mIsDraging = false
                return true
            }
            else -> {
            }
        }
        return mBadge.callSuperOnTouchEvent(event)
    }

    fun endDragWithDismiss() {
        hiddenBadge()
        if (mDelegage != null) mDelegage!!.onDismiss(mBadge)
    }

    fun endDragWithoutDismiss() {
        mBadge.postInvalidate()
    }

    fun drawBadge(canvas: Canvas) {
        if (mIsShowBadge && !mIsDraging) {
            if (mIsShowDrawable) {
                drawDrawableBadge(canvas)
            } else {
                drawTextBadge(canvas)
            }
        }
    }

    /**
     * 绘制图像徽章
     *
     * @param canvas
     */
    private fun drawDrawableBadge(canvas: Canvas) {
        mBadgeRectF.left = (mBadge.getWidth() - mBadgeHorizontalMargin - mBitmap.width).toFloat()
        mBadgeRectF.top = mBadgeVerticalMargin.toFloat()
        when (mBadgeGravity) {
            BadgeViewHelper.BadgeGravity.RightTop -> mBadgeRectF.top = mBadgeVerticalMargin.toFloat()
            BadgeViewHelper.BadgeGravity.RightCenter -> mBadgeRectF.top = ((mBadge.getHeight() - mBitmap.height) / 2).toFloat()
            BadgeViewHelper.BadgeGravity.RightBottom -> mBadgeRectF.top = (mBadge.getHeight() - mBitmap.height - mBadgeVerticalMargin).toFloat()
        }
        canvas.drawBitmap(mBitmap, mBadgeRectF.left, mBadgeRectF.top, mBadgePaint)
        mBadgeRectF.right = mBadgeRectF.left + mBitmap.width
        mBadgeRectF.bottom = mBadgeRectF.top + mBitmap.width
    }

    /**
     * 绘制文字徽章
     *
     * @param canvas
     */
    private fun drawTextBadge(canvas: Canvas) {
        val badgeText = if (!TextUtils.isEmpty(mBadgeText)) mBadgeText ?: "" else ""

        // 获取文本宽所占宽高
        mBadgePaint.getTextBounds(badgeText, 0, badgeText.length, mBadgeNumberRect)

        // 计算徽章背景的宽高
        val badgeHeight = mBadgeNumberRect.height() + mBadgePadding * 2

        // 当mBadgeText的长度为1或0时，计算出来的高度会比宽度大，此时设置宽度等于高度
        val badgeWidth = if (badgeText.length == 1 || badgeText.isEmpty()) badgeHeight
        else mBadgeNumberRect.width() + mBadgePadding * 2

        // 计算徽章背景上下的值
        mBadgeRectF.top = mBadgeVerticalMargin.toFloat()
        mBadgeRectF.bottom = (mBadge.getHeight() - mBadgeVerticalMargin).toFloat()
        when (mBadgeGravity) {
            BadgeViewHelper.BadgeGravity.RightTop -> mBadgeRectF.bottom = mBadgeRectF.top + badgeHeight
            BadgeViewHelper.BadgeGravity.RightCenter -> {
                mBadgeRectF.top = ((mBadge.getHeight() - badgeHeight) / 2).toFloat()
                mBadgeRectF.bottom = mBadgeRectF.top + badgeHeight
            }
            BadgeViewHelper.BadgeGravity.RightBottom -> mBadgeRectF.top = mBadgeRectF.bottom - badgeHeight
        }

        // 计算徽章背景左右的值
        mBadgeRectF.right = (mBadge.getWidth() - mBadgeHorizontalMargin).toFloat()
        mBadgeRectF.left = mBadgeRectF.right - badgeWidth

        if (mBadgeBorderWidth > 0) {
            // 设置徽章边框景色
            mBadgePaint.color = mBadgeBorderColor
            // 绘制徽章边框背景
            canvas.drawRoundRect(mBadgeRectF, (badgeHeight / 2).toFloat(), (badgeHeight / 2).toFloat(), mBadgePaint)

            // 设置徽章背景色
            mBadgePaint.color = mBadgeBgColor
            // 绘制徽章背景
            canvas.drawRoundRect(RectF(mBadgeRectF.left + mBadgeBorderWidth, mBadgeRectF.top + mBadgeBorderWidth, mBadgeRectF.right - mBadgeBorderWidth, mBadgeRectF.bottom - mBadgeBorderWidth), ((badgeHeight - 2 * mBadgeBorderWidth) / 2).toFloat(), ((badgeHeight - 2 * mBadgeBorderWidth) / 2).toFloat(), mBadgePaint)
        } else {
            // 设置徽章背景色
            mBadgePaint.color = mBadgeBgColor

            // 绘制徽章背景
            canvas.drawRoundRect(mBadgeRectF, (badgeHeight / 2).toFloat(), (badgeHeight / 2).toFloat(), mBadgePaint)
        }


        if (!TextUtils.isEmpty(mBadgeText)) {
            // 设置徽章文本颜色
            mBadgePaint.color = mBadgeTextColor
            // initDefaultAttrs方法中设置了mBadgeText居中，此处的x为徽章背景的中心点y
            val x = mBadgeRectF.left + badgeWidth / 2
            // 注意：绘制文本时的y是指文本底部，而不是文本的中间
            val y = mBadgeRectF.bottom - mBadgePadding
            // 绘制徽章文本
            canvas.drawText(badgeText, x, y, mBadgePaint)
        }
    }

    fun showCirclePointBadge() {
        showTextBadge(null)
    }

    fun showTextBadge(badgeText: String?) {
        mIsShowDrawable = false
        mBadgeText = badgeText
        mIsShowBadge = true
        mBadge.postInvalidate()
    }

    fun hiddenBadge() {
        mIsShowBadge = false
        mBadge.postInvalidate()
    }

    fun isShowBadge(): Boolean {
        return mIsShowBadge
    }

    fun showDrawable(bitmap: Bitmap) {
        mBitmap = bitmap
        mIsShowDrawable = true
        mIsShowBadge = true
        mBadge.postInvalidate()
    }

    fun isShowDrawable(): Boolean {
        return mIsShowDrawable
    }

    fun getBadgeRectF(): RectF {
        return mBadgeRectF
    }

    fun getBadgePadding(): Int {
        return mBadgePadding
    }

    fun getBadgeText(): String {
        return mBadgeText ?: ""
    }

    fun getBadgeBgColor(): Int {
        return mBadgeBgColor
    }

    fun getBadgeTextColor(): Int {
        return mBadgeTextColor
    }

    fun getBadgeTextSize(): Int {
        return mBadgeTextSize
    }

    fun getBitmap(): Bitmap {
        return mBitmap
    }

    fun setDragDismissDelegage(delegage: DragDismissDelegate) {
        mDelegage = delegage
    }

    fun getRootView(): View {
        return mBadge.getRootView()
    }

    fun isResumeTravel(): Boolean {
        return mIsResumeTravel
    }

    enum class BadgeGravity {
        RightTop,
        RightCenter,
        RightBottom
    }
}