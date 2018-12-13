package com.example.user.canvasexample

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.support.v4.content.res.ResourcesCompat
import android.view.MotionEvent


class CanvasView
    @JvmOverloads
    constructor(context: Context,
                attrs: AttributeSet? = null,
                defStyleAttr: Int = 0,
                defStyleRes: Int = 0): View(context, attrs, defStyleAttr, defStyleRes) {

    private val TOUCH_TOLERANCE = 4f

    private val mPaint: Paint
    private val mPath: Path
    private var mDrawColor: Int = 0
    private var mBackgroundColor: Int = 0
    private lateinit var mExtraCanvas: Canvas
    private lateinit var mExtraBitmap: Bitmap

    private var mX: Float = 0f
    private var mY: Float = 0f

    init {
        mBackgroundColor = ResourcesCompat.getColor(
            resources,
            R.color.opaque_orange, null
        )
        mDrawColor = ResourcesCompat.getColor(
            resources,
            R.color.opaque_yellow, null
        )

        // Holds the path we are currently drawing.
        mPath = Path()
        // Set up the paint with which to draw.
        mPaint = Paint().apply {

            color = mDrawColor
            // Smoothes out edges of what is drawn without affecting shape.
            isAntiAlias = true
            // Dithering affects how colors with higher-precision device
            // than the are down-sampled.
            isDither = true
            style = Paint.Style.STROKE       // default: FILL
            strokeJoin = Paint.Join.ROUND    // default: MITER
            strokeCap = Paint.Cap.ROUND      // default: BUTT
            setStrokeWidth(12f)              // default: Hairline-width (really thin)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mExtraBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mExtraCanvas = Canvas(mExtraBitmap).apply {
            drawColor(mBackgroundColor)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the bitmap that stores the path the user has drawn.
        // Initially the user has not drawn anything
        // so we see only the colored bitmap.
        canvas.drawBitmap(mExtraBitmap, 0f, 0f, null)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.getX()
        val y = event.getY()

        // Invalidate() is inside the case statements because there are many
        // other types of motion events passed into this listener,
        // and we don't want to invalidate the view for those.
        when (event.getAction()) {
            MotionEvent.ACTION_DOWN -> touchStart(x, y)
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> touchUp()
        }
        // No need to invalidate because we are not drawing anything.
        // Do nothing.
        return true
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            // Reset mX and mY to the last drawn point.
            mX = x
            mY = y
            // Save the path in the extra bitmap,
            // which we access through its canvas.
            mExtraCanvas.drawPath(mPath, mPaint)
        }
    }

    private fun touchUp() {
        // Reset the path so it doesn't get drawn again.
        mPath.reset()
    }

    private fun touchStart(x: Float, y: Float) {
        mPath.moveTo(x, y)
        mX = x
        mY = y
    }
}