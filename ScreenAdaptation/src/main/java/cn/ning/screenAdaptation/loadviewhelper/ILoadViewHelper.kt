package cn.ning.screenAdaptation.loadviewhelper

import android.view.View

/**
 * Created by MrNing
 * on 2018/7/26
 * Email Mr_Ning314@163.com
 */
interface ILoadViewHelper {
    abstract fun loadWidthHeightFont(view: View)

    abstract fun loadPadding(view: View)

    abstract fun loadLayoutMargin(view: View)

    abstract fun loadMaxWidthAndHeight(view: View)

    abstract fun loadMinWidthAndHeight(view: View)

    abstract fun loadCustomAttrValue(px: Int): Int
}