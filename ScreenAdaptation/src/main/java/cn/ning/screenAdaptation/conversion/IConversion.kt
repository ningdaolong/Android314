package cn.ning.screenAdaptation.conversion

import android.view.View
import cn.ning.screenAdaptation.loadviewhelper.AbsLoadViewHelper

/**
 * Created by MrNing
 * on 2018/7/26
 * Email Mr_Ning314@163.com
 */
interface IConversion {
    abstract fun transform(view: View, loadViewHelper: AbsLoadViewHelper)
}