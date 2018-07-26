package cn.ning.screenAdaptation.loadviewhelper

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.ning.screenAdaptation.utils.ViewUtils
import cn.ning.screenAdaptation.utils.dp2pxUtils

/**
 * Created by MrNing
 * on 2018/7/26
 * Email Mr_Ning314@163.com
 */
class LoadViewHelper(var context: Context,
                     var designWidth: Int,
                     var designDpi: Int,
                     var fontSize: Float,
                     var unit: String) :
        AbsLoadViewHelper(context, designWidth, designDpi, fontSize, unit) {

    override fun loadWidthHeightFont(view: View) {
        val layoutParams = view.layoutParams
        if (layoutParams.width > 0) {
            layoutParams.width = setValue(layoutParams.width)
        }
        if (layoutParams.height > 0) {
            layoutParams.height = setValue(layoutParams.height)
        }
        loadViewFont(view)
    }

    private fun loadViewFont(view: View) {
        if (view is TextView) {
            view.setTextSize(0, setFontSize(view))
        }
    }

    private fun setFontSize(textView: TextView): Float {
        return calculateValue(textView.textSize * fontSize)
    }

    override fun loadPadding(view: View) {
        view.setPadding(setValue(view.paddingLeft), setValue(view.paddingTop), setValue(view.paddingRight), setValue(view.paddingBottom))
    }

    override fun loadLayoutMargin(view: View) {
        val params = view.layoutParams
        val marginLayoutParams: ViewGroup.MarginLayoutParams
        if (params is ViewGroup.MarginLayoutParams) {
            marginLayoutParams = params
            marginLayoutParams.leftMargin = setValue(marginLayoutParams.leftMargin)
            marginLayoutParams.topMargin = setValue(marginLayoutParams.topMargin)
            marginLayoutParams.rightMargin = setValue(marginLayoutParams.rightMargin)
            marginLayoutParams.bottomMargin = setValue(marginLayoutParams.bottomMargin)
            view.layoutParams = marginLayoutParams
        }
    }

    override fun loadMaxWidthAndHeight(view: View) {
        ViewUtils.setMaxWidth(view, setValue(ViewUtils.getMaxWidth(view)))
        ViewUtils.setMaxHeight(view, setValue(ViewUtils.getMaxHeight(view)))
    }

    override fun loadMinWidthAndHeight(view: View) {
        ViewUtils.setMinWidth(view, setValue(ViewUtils.getMinWidth(view)))
        ViewUtils.setMinHeight(view, setValue(ViewUtils.getMinHeight(view)))
    }

    override fun loadCustomAttrValue(px: Int): Int {
        return setValue(px)
    }


    private fun setValue(value: Int): Int {
        if (value == 0) {
            return 0
        } else if (value == 1) {
            return 1
        }
        return calculateValue(value.toFloat()).toInt()
    }

    private fun calculateValue(value: Float): Float {
        return when (unit) {
            "px" -> {
                value * (actualWidth / designWidth)
            }

            "dp" -> {
                val dip = dp2pxUtils.px2dip(actualDensity, value)
                (designDpi / 160 * dip) * (actualWidth / designWidth)
            }

            else -> 0f
        }
    }
}