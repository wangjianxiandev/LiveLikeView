package com.wjx.android.livelikeview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var clickCount = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        initView()
        live_view_single.setOnClickListener({
            clickCount++
            live_view.addLikeView()
            click_count.setText(clickCount.toString())
        })
    }

    private fun initView() {
        live_view.addLikeImage(R.drawable.zan0)
        live_view.addLikeImage(R.drawable.zan1)
        live_view.addLikeImage(R.drawable.zan2)
        live_view.addLikeImage(R.drawable.zan3)
        live_view.addLikeImage(R.drawable.zan4)
        live_view.addLikeImage(R.drawable.zan5)
        live_view.addLikeImage(R.drawable.zan6)
    }
}
