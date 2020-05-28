package vn.vistark.nkktts.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.gms.maps.MapView

class VistarkMapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MapView(context, attrs, defStyleAttr) {

//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        val action: Int = event.getAction()
//        when (action) {
//            MotionEvent.ACTION_DOWN ->         // Disallow ScrollView to intercept touch events.
//                this.parent.requestDisallowInterceptTouchEvent(true)
//            MotionEvent.ACTION_UP ->         // Allow ScrollView to intercept touch events.
//                this.parent.requestDisallowInterceptTouchEvent(false)
//        }
//
//        // Handle MapView's touch events.
//
//        // Handle MapView's touch events.
//        super.onTouchEvent(event)
//        return true
//    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.action) {
            MotionEvent.ACTION_UP -> {
                println("unlocked")
                this.parent.requestDisallowInterceptTouchEvent(false)
            }
            MotionEvent.ACTION_DOWN -> {
                println("locked")
                this.parent.requestDisallowInterceptTouchEvent(true)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}