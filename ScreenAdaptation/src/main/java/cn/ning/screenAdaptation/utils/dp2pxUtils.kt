package cn.ning.screenAdaptation.utils

import android.content.Context

/**
 * Created by MrNing
 * on 2018/7/26
 * Email Mr_Ning314@163.com
 */
internal object dp2pxUtils {
    private val TAG = "dp2pxUtils"

    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun dip2px(density: Float, dpValue: Float): Int {
        return (dpValue * density + 0.5f).toInt()
    }

    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun px2dip(density: Float, pxValue: Float): Int {
        return (pxValue / density + 0.5f).toInt()
    }
}