package cn.ning.screenAdaptation.utils

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * Created by MrNing
 * on 2018/7/26
 * Email Mr_Ning314@163.com
 */
internal object ActualScreen {
    fun screenInfo(context: Context): FloatArray {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return floatArrayOf(displayMetrics.widthPixels.toFloat(), displayMetrics.heightPixels.toFloat(), displayMetrics.density, displayMetrics.densityDpi.toFloat())
    }
}