package cn.ning.screenAdaptation.utils

import android.os.Build
import android.view.View

/**
 * Created by MrNing
 * on 2018/7/26
 * Email Mr_Ning314@163.com
 */
internal object ViewUtils {
    private val METHOD_GET_MAX_WIDTH = "getMaxWidth"
    private val METHOD_GET_MAX_HEIGHT = "getMaxHeight"
    private val METHOD_GET_MIN_WIDTH = "getMinWidth"
    private val METHOD_GET_MIN_HEIGHT = "getMinHeight"
    private val METHOD_SET_MAX_WIDTH = "setMaxWidth"
    private val METHOD_SET_MAX_HEIGHT = "setMaxHeight"

    fun setMaxWidth(view: View, value: Int) {
        setValue(view, METHOD_SET_MAX_WIDTH, value)
    }

    fun setMaxHeight(view: View, value: Int) {
        setValue(view, METHOD_SET_MAX_HEIGHT, value)
    }

    fun setMinWidth(view: View, value: Int) {
        view.minimumWidth = value
    }

    fun setMinHeight(view: View, value: Int) {
        view.minimumHeight = value
    }

    fun getMaxWidth(view: View): Int {
        return getValue(view, METHOD_GET_MAX_WIDTH)
    }

    fun getMaxHeight(view: View): Int {
        return getValue(view, METHOD_GET_MAX_HEIGHT)
    }

    fun getMinWidth(view: View): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.minimumWidth
        } else {
            getValue(view, METHOD_GET_MIN_WIDTH)
        }
    }

    fun getMinHeight(view: View): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.minimumHeight
        } else {
            getValue(view, METHOD_GET_MIN_HEIGHT)
        }
    }

    private fun getValue(view: View, getterMethodName: String): Int {
        var result = 0
        try {
            val getValueMethod = view.javaClass.getMethod(getterMethodName)
            result = getValueMethod.invoke(view) as Int
        } catch (e: Exception) {
            // do nothing
        }

        return result
    }

    private fun setValue(view: View, setterMethodName: String, value: Int) {
        try {
            val setValueMethod = view.javaClass.getMethod(setterMethodName)
            setValueMethod.invoke(view, value)
        } catch (e: Exception) {
            // do nothing
        }

    }
}