package cn.ning.screenAdaptation

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import cn.ning.screenAdaptation.loadviewhelper.AbsLoadViewHelper
import cn.ning.screenAdaptation.loadviewhelper.LoadViewHelper

/**
 * Created by MrNing
 * on 2018/7/26
 * Email Mr_Ning314@163.com
 */
object ScreenAdapterTools {
    private var sLoadViewHelper: AbsLoadViewHelper? = null

    fun getInstance(): AbsLoadViewHelper? {
        return sLoadViewHelper
    }

    fun init(context: Context) {
        init(context, object : IProvider {
            override fun provide(context: Context, designWidth: Int, designDpi: Int, fontSize: Float, unit: String): AbsLoadViewHelper {
                return LoadViewHelper(context, designWidth, designDpi, fontSize, unit)
            }
        })
    }

    fun init(context: Context, provider: IProvider) {
        var applicationInfo: ApplicationInfo? = null
        try {
            applicationInfo = context.packageManager.getApplicationInfo(context
                    .packageName, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        val designwidth = applicationInfo!!.metaData.getInt("designwidth")
        val designdpi = applicationInfo.metaData.getInt("designdpi")
        val fontsize = applicationInfo.metaData.getFloat("fontsize")
        val unit = applicationInfo.metaData.getString("unit")
        sLoadViewHelper = provider.provide(context, designwidth, designdpi, fontsize, unit)
    }

    interface IProvider {
        fun provide(context: Context, designWidth: Int, designDpi: Int, fontSize: Float, unit: String): AbsLoadViewHelper
    }
}