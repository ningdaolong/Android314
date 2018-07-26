package cn.ning.badge

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.ViewParent

/**
 * Created by MrNing
 * on 2018/7/26
 * Email Mr_Ning314@163.com
 */
interface BaseBadge {
    /**
     * 显示圆点徽章
     */
    abstract fun showCirclePointBadge()

    /**
     * 显示文字徽章
     *
     * @param badgeText
     */
    abstract fun showTextBadge(badgeText: String)

    /**
     * 隐藏徽章
     */
    abstract fun hiddenBadge()

    /**
     * 显示图像徽章
     *
     * @param bitmap
     */
    abstract fun showDrawableBadge(bitmap: Bitmap)

    /**
     * 调用父类的onTouchEvent方法
     *
     * @param event
     * @return
     */
    abstract fun callSuperOnTouchEvent(event: MotionEvent): Boolean

    /**
     * 拖动大于BGABadgeViewHelper.mMoveHiddenThreshold后抬起手指徽章消失的代理
     *
     * @param delegate
     */
    abstract fun setDragDismissDelegage(delegate: DragDismissDelegate)

    /**
     * 是否显示徽章
     *
     * @return
     */
    abstract fun isShowBadge(): Boolean

    abstract fun getBadgeViewHelper(): BadgeViewHelper

    abstract fun getWidth(): Int

    abstract fun getHeight(): Int

    abstract fun postInvalidate()

    abstract fun getParent(): ViewParent

    abstract fun getId(): Int

    abstract fun getGlobalVisibleRect(r: Rect): Boolean

    abstract fun getContext(): Context

    abstract fun getRootView(): View
}