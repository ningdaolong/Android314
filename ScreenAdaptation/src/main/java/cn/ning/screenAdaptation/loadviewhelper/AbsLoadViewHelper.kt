package cn.ning.screenAdaptation.loadviewhelper

import android.content.Context
import android.view.View
import android.view.ViewGroup
import cn.ning.screenAdaptation.conversion.IConversion
import cn.ning.screenAdaptation.conversion.SimpleConversion
import cn.ning.screenAdaptation.utils.ActualScreen

/**
 * Created by MrNing
 * on 2018/7/26
 * Email Mr_Ning314@163.com
 */
abstract class AbsLoadViewHelper(context: Context, designWidth: Int, designDpi: Int, fontSize: Float, unit: String) : ILoadViewHelper {
    protected var actualDensity:Float = 0.0f
    protected var actualDensityDpi:Float = 0.0f
    protected var actualWidth:Float = 0.0f
    protected var actualHeight:Float = 0.0f

    init {
        setActualParams(context)
    }

    fun reset(context: Context) {
        setActualParams(context)
    }

    private fun setActualParams(context: Context) {
        val actualScreenInfo = ActualScreen.screenInfo(context)
        if (actualScreenInfo.size == 4) {
            actualWidth = actualScreenInfo[0]
            actualHeight = actualScreenInfo[1]
            actualDensity = actualScreenInfo[2]
            actualDensityDpi = actualScreenInfo[3]
        }
    }

    fun loadView(view: View) {
        loadView(view, SimpleConversion())
    }

    fun loadView(view: View, conversion: IConversion) {
        if (view is ViewGroup) {
            conversion.transform(view, this)
            for (i in 0 until view.childCount) {
                if (view.getChildAt(i) is ViewGroup) {
                    loadView(view.getChildAt(i), conversion)
                } else {
                    conversion.transform(view.getChildAt(i), this)
                }
            }
        } else {
            conversion.transform(view, this)
        }

    }
}