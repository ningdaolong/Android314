package cn.ning.badge

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView

/**
 * Created by MrNing
 * on 2018/7/26
 * Email Mr_Ning314@163.com
 */
class BadgeImageView : ImageView, BaseBadge {
    private lateinit var mBadgeViewHeler: BadgeViewHelper

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        mBadgeViewHeler = BadgeViewHelper(this, context, attrs, BadgeViewHelper.BadgeGravity.RightTop)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mBadgeViewHeler.onTouchEvent(event)
    }

    override fun callSuperOnTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mBadgeViewHeler.drawBadge(canvas)
    }

    override fun showCirclePointBadge() {
        mBadgeViewHeler.showCirclePointBadge()
    }

    override fun showTextBadge(badgeText: String) {
        mBadgeViewHeler.showTextBadge(badgeText)
    }

    override fun hiddenBadge() {
        mBadgeViewHeler.hiddenBadge()
    }

    override fun showDrawableBadge(bitmap: Bitmap) {
        mBadgeViewHeler.showDrawable(bitmap)
    }

    override fun setDragDismissDelegage(delegate: DragDismissDelegate) {
        mBadgeViewHeler.setDragDismissDelegage(delegate)
    }

    override fun isShowBadge(): Boolean {
        return mBadgeViewHeler.isShowBadge()
    }

    override fun getBadgeViewHelper(): BadgeViewHelper {
        return mBadgeViewHeler
    }
}