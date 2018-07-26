package cn.ning.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cn.ning.screenAdaptation.ScreenAdapterTools
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ScreenAdapterTools.getInstance()?.reset(this)//如果希望android7.0分屏也适配的话,加上这句
        ScreenAdapterTools.getInstance()?.loadView(window.decorView)

        imageView.showCirclePointBadge()
        imageView.setOnClickListener {
            imageView.hiddenBadge()
        }
    }
}
