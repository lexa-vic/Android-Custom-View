package com.kostikov.customfancontroller

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View



/**
 * @author Kostikov Aleksey.
 */
class DialView
    @JvmOverloads
    constructor(context: Context,
             attrs: AttributeSet? = null,
             defStyleAttr: Int = 0,
             defStyleRes: Int = 0): View(context, attrs, defStyleAttr, defStyleRes){

    private val SELECTION_COUNT = 4         // Total number of selections.

    private val SELECTOR_RADIUS_PX: Float = 20.0.toFloat()     // Selector circle radius in px

    private var width: Float = Float.NaN    // Custom view width.
    private var height: Float = Float.NaN   // Custom view height.
    private val textPaint: Paint            // For text in the view.
    private val dialPaint: Paint            // For dial circle in the view.
    private var radius: Float = Float.NaN   // Radius of the circle.
    private var activeSelection: Int = 0    // The active selection.

    // String buffer for dial labels and float for ComputeXY result.
    private val tempLabel: StringBuffer = StringBuffer(8)
    private val tempResult: FloatArray = FloatArray(2)

    init {
        textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            style = Paint.Style.FILL_AND_STROKE
            textAlign = Paint.Align.CENTER
            textSize = 40f
        }

        dialPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GRAY
        }

        // Initialize current selection.
        activeSelection = 0

        // TODO: Set up onClick listener for this view.
        setOnClickListener {
            // Rotate selection to the next valid choice.
            activeSelection = (activeSelection + 1) % SELECTION_COUNT;
            // Set dial background color to green if selection is >= 1.
            if (activeSelection >= 1) {
                dialPaint.color = Color.GREEN
            } else {
                dialPaint.color = Color.GRAY
            }
            // Redraw the view.
            invalidate();
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        width = w.toFloat()
        height = h.toFloat()

        radius = ((Math.min(width, height) / 2) * 0.8).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas);
        // Draw the dial.
        canvas.drawCircle(width / 2, height / 2, radius, dialPaint)

        // Draw the text labels.
        val labelRadius = radius + 20
        val label = tempLabel
        for (i in 0 until SELECTION_COUNT) {
            val xyData = computeXYForPosition(i, labelRadius)
            val x = xyData[0]
            val y = xyData[1]
            label.setLength(0)
            label.append(i)
            canvas.drawText(label, 0, label.length, x, y, textPaint)
        }
        // Draw the indicator mark.
        val markerRadius = radius - 35
        val xyData = computeXYForPosition(activeSelection, markerRadius)
        val x = xyData[0]
        val y = xyData[1]
        canvas.drawCircle(x, y, SELECTOR_RADIUS_PX, textPaint)
    }

    private fun computeXYForPosition(pos: Int, radius: Float): FloatArray {
        val result = tempResult
        val startAngle = Math.PI * (9 / 8.0)                            // Angles are in radians.
        val angle = startAngle + pos * (Math.PI / 4)
        result[0] = (radius * Math.cos(angle)).toFloat() + width / 2
        result[1] = (radius * Math.sin(angle)).toFloat() + height / 2
        return result
    }
}