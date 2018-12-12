package com.kostikov.customfancontroller

import android.content.Context
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

    private var width: Float = Float.NaN    // Custom view width.
    private var height: Float = Float.NaN   // Custom view height.
    private lateinit var textPaint: Paint   // For text in the view.
    private lateinit var dialPaint: Paint   // For dial circle in the view.
    private var radius: Float = Float.NaN   // Radius of the circle.
    private var activeSelection: Int = 0    // The active selection.

    // String buffer for dial labels and float for ComputeXY result.
    private val tempLabel: StringBuffer = StringBuffer(8)
    private val tempResult: FloatArray = FloatArray(2)

}