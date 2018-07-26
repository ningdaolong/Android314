package cn.ning.screenAdaptation.conversion

import android.view.View
import cn.ning.screenAdaptation.loadviewhelper.AbsLoadViewHelper

/**
 * Created by MrNing
 * on 2018/7/26
 * Email Mr_Ning314@163.com
 */
class SimpleConversion : IConversion {
    override fun transform(view: View, loadViewHelper: AbsLoadViewHelper) {
        if (view.layoutParams == null) return
        loadViewHelper.loadWidthHeightFont(view)
        loadViewHelper.loadPadding(view)
        loadViewHelper.loadLayoutMargin(view)
    }
}