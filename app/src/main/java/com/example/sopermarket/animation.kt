package com.example.sopermarket

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class animation(view: View) : Animation() {

    private val targetView = view

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        // Scale down the view
        val scale = 1 - interpolatedTime
        t?.matrix?.setScale(scale, scale)

        // Fade out the view
        val alpha = (1 - interpolatedTime) * 255
        t?.alpha = alpha.toInt().toFloat()

        // Rotate the view
        val rotation = interpolatedTime * 360
        t?.matrix?.setRotate(rotation)

        // Translate the view
        val translation = interpolatedTime * targetView.height
        t?.matrix?.postTranslate(0f, translation)

        // Invalidate the view to trigger redraw
        targetView.invalidate()
    }
}