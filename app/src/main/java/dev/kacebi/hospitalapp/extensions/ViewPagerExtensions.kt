package dev.kacebi.hospitalapp.extensions

import android.os.Handler
import androidx.viewpager.widget.ViewPager

fun ViewPager.autoScroll(interval: Long) {

    var position: Int
    val handler = Handler()
    val size = adapter?.count ?: 0

    val runnable = object : Runnable {

        override fun run() {

            position = currentItem

            if (position == size - 1)
                position = -1

            setCurrentItem(++position % size, true)
            handler.postDelayed(this, interval)

        }
    }

    handler.postDelayed(runnable, interval)
}