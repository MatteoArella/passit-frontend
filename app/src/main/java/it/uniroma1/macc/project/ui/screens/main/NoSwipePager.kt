package it.uniroma1.macc.project.ui.screens.main

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class NoSwipePager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (this.isEnabled) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return if (this.isEnabled) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.isEnabled = enabled
    }
}