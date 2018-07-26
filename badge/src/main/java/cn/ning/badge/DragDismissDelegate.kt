package cn.ning.badge

/**
 * Created by MrNing
 * on 2018/7/26
 * Email Mr_Ning314@163.com
 */
interface DragDismissDelegate {
    /**
     * 拖动大于BGABadgeViewHelper.mMoveHiddenThreshold后抬起手指徽章消失的回调方法
     *
     * @param badge
     */
    abstract fun onDismiss(badge: BaseBadge)
}