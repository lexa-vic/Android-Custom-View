package com.kostikov.clippingexample

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View

/**
 * @author Kostikov Aleksey.
 */
class ClippingView

    @JvmOverloads
    constructor(context: Context,
                attrs: AttributeSet? = null,
                defStyleAttr: Int = 0,
                defStyleRes: Int = 0): View(context, attrs, defStyleAttr, defStyleRes){

    private val mClipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val mClipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val mClipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val mClipRectLeft = resources.getDimension(R.dimen.clipRectLeft)
    private val mRectInset = resources.getDimension(R.dimen.rectInset)
    private val mSmallRectOffset = resources.getDimension(R.dimen.smallRectOffset)

    private val mCircleRadius = resources.getDimension(R.dimen.circleRadius)

    private val mTextOffset = resources.getDimension(R.dimen.textOffset)
    private val mTextSize = resources.getDimension(R.dimen.textSize)

    private val mColumnOne = mRectInset
    private val mColumnnTwo = mColumnOne + mRectInset + mClipRectRight

    private val mRowOne = mRectInset
    private val mRowTwo = mRowOne + mRectInset + mClipRectBottom
    private val mRowThree = mRowTwo + mRectInset + mClipRectBottom
    private val mRowFour = mRowThree + mRectInset + mClipRectBottom
    private val mTextRow = mRowFour + (1.5 * mClipRectBottom).toFloat()

    private var mPaint: Paint
    private var mPath: Path
    private var mRectF: RectF

    init {
        isFocusable = true
        mPaint = Paint().apply {

            isAntiAlias = true
            strokeWidth = resources.getDimension(R.dimen.strokeWidth)
            textSize = resources.getDimension(R.dimen.textSize)
        }
        // Smooth out edges of what is drawn without affecting shape.
        mPath = Path()

        mRectF = RectF(Rect(mRectInset.toInt(),
            mRectInset.toInt(),
            mClipRectRight.toInt() - mRectInset.toInt(),
            mClipRectBottom.toInt() - mRectInset.toInt()))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Fill the unclipped canvas with gray background color.
        canvas.drawColor(Color.GRAY)
        // Save the modified canvas to a private stack.
        canvas.save()
        // Instead of drawing the rectangle with new coordinates every time,
        // move the origin of the Canvas and draw in the "same" location.
        // This is more efficient.
        canvas.translate(mColumnOne, mRowOne)
        drawClippedRectangle(canvas)
        canvas.restore()

        // Context maintains a stack of drawing states, including
        // currently applied transformations and clipping regions.
        // Pop the states with the transformations from the stack
        // to undo them.
        // In this example, after moving the origin and drawing
        // the next rectangle, instead of translating again from the
        // new origin, reset to the original (top-left corner)
        // origin and translate again from there.
        canvas.save()
        // Move the origin to the right for the next rectangle.
        canvas.translate(mColumnnTwo, mRowOne)
        // Use the subtraction of two clipping rectangles to create a frame.
        canvas.clipRect(
            2 * mRectInset, 2 * mRectInset,
            mClipRectRight - 2 * mRectInset, mClipRectBottom - 2 * mRectInset
        )
        // The method clipRect(float, float, float, float, Region.Op
        // .DIFFERENCE) was deprecated in API level 26. The recommended
        // alternative method is clipOutRect(float, float, float, float),
        // which is currently available in API level 26 and higher.
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            canvas.clipRect(
                4 * mRectInset, 4 * mRectInset,
                mClipRectRight - 4 * mRectInset, mClipRectBottom - 4 * mRectInset,
                Region.Op.DIFFERENCE
            )
        else {
            canvas.clipOutRect(
                4 * mRectInset, 4 * mRectInset,
                mClipRectRight - 4 * mRectInset,
                mClipRectBottom - 4 * mRectInset
            )
        }


        drawClippedRectangle(canvas)
        canvas.restore()

        // Circular clipping path.
        canvas.save()
        canvas.translate(mColumnOne, mRowTwo)
        // Clears any lines and curves from the path but unlike reset(),
        // keeps the internal data structure for faster reuse.
        mPath.rewind()
        mPath.addCircle(
            mCircleRadius, mClipRectBottom - mCircleRadius,
            mCircleRadius, Path.Direction.CCW
        )
        // The method clipPath(path, Region.Op.DIFFERENCE) was deprecated in
        // API level 26. The recommended alternative method is
        // clipOutPath(Path), which is currently available in API level 26
        // and higher.
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipPath(mPath, Region.Op.DIFFERENCE)
        } else {
            canvas.clipOutPath(mPath)
        }
        drawClippedRectangle(canvas)
        canvas.restore()

        // Use the intersection of two rectangles as the clipping region.
        canvas.save()
        canvas.translate(mColumnnTwo, mRowTwo)
        canvas.clipRect(
            mClipRectLeft, mClipRectTop,
            mClipRectRight - mSmallRectOffset,
            mClipRectBottom - mSmallRectOffset
        )
        // The method clipRect(float, float, float, float, Region.Op
        // .INTERSECT) was deprecated in API level 26. The recommended
        // alternative method is clipRect(float, float, float, float), which
        // is currently available in API level 26 and higher.
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipRect(
                mClipRectLeft + mSmallRectOffset,
                mClipRectTop + mSmallRectOffset, mClipRectRight,
                mClipRectBottom, Region.Op.INTERSECT
            )
        } else {
            canvas.clipRect(
                mClipRectLeft + mSmallRectOffset,
                mClipRectTop + mSmallRectOffset, mClipRectRight,
                mClipRectBottom
            )
        }

        drawClippedRectangle(canvas)
        canvas.restore()

        // You can combine shapes and draw any path to define a clipping region.
        canvas.save()
        canvas.translate(mColumnOne, mRowThree)
        mPath.rewind()
        mPath.addCircle(
            mClipRectLeft + mRectInset + mCircleRadius,
            mClipRectTop + mCircleRadius + mRectInset,
            mCircleRadius, Path.Direction.CCW
        )
        mPath.addRect(
            mClipRectRight / 2 - mCircleRadius,
            mClipRectTop + mCircleRadius + mRectInset,
            mClipRectRight / 2 + mCircleRadius,
            mClipRectBottom - mRectInset, Path.Direction.CCW
        )
        canvas.clipPath(mPath)
        drawClippedRectangle(canvas)
        canvas.restore()

        // Use a rounded rectangle. Use mClipRectRight/4 to draw a circle.
        canvas.save()
        canvas.translate(mColumnnTwo, mRowThree)
        mPath.rewind()
        mPath.addRoundRect(
            mRectF, mClipRectRight as Float / 4,
            mClipRectRight as Float / 4, Path.Direction.CCW
        )
        canvas.clipPath(mPath)
        drawClippedRectangle(canvas)
        canvas.restore()

        // Clip the outside around the rectangle.
        canvas.save()
        // Move the origin to the right for the next rectangle.
        canvas.translate(mColumnOne, mRowFour)
        canvas.clipRect(
            2 * mRectInset, 2 * mRectInset,
            mClipRectRight - 2 * mRectInset,
            mClipRectBottom - 2 * mRectInset
        )
        drawClippedRectangle(canvas)
        canvas.restore()


        // Draw text with a translate transformation applied.
        canvas.save()
        mPaint.color = Color.CYAN
        // Align the RIGHT side of the text with the origin.
        mPaint.textAlign = Paint.Align.LEFT
        // Apply transformation to canvas.
        canvas.translate(mColumnnTwo, mTextRow)
        // Draw text.
        canvas.drawText(
            context.getString(R.string.translated), 0f, 0f, mPaint
        )
        canvas.restore()

        // Draw text with a translate and skew transformations applied.
        canvas.save()
        mPaint.setTextSize(mTextSize)
        mPaint.textAlign = Paint.Align.RIGHT
        // Position text.
        canvas.translate(mColumnnTwo, mTextRow)
        // Apply skew transformation.
        canvas.skew(0.2f, 0.3f)
        canvas.drawText(
            context.getString(R.string.skewed), 0f, 0f, mPaint
        )
        canvas.restore()
    }

    private fun drawClippedRectangle(canvas: Canvas ) {
        // Set the boundaries of the clipping rectangle for whole picture.
        canvas.clipRect(mClipRectLeft, mClipRectTop,
            mClipRectRight, mClipRectBottom)

        // Fill the canvas with white.
        // With the clipped rectangle, this only draws
        // inside the clipping rectangle.
        // The rest of the surface remains gray.
        canvas.drawColor(Color.WHITE)

        // Change the color to red and
        // draw a line inside the clipping rectangle.
        mPaint.setColor(Color.RED);
        canvas.drawLine(mClipRectLeft, mClipRectTop,
            mClipRectRight, mClipRectBottom, mPaint)

        // Set the color to green and
        // draw a circle inside the clipping rectangle.
        mPaint.setColor(Color.GREEN);
        canvas.drawCircle(mCircleRadius, mClipRectBottom - mCircleRadius,
            mCircleRadius, mPaint)

        // Set the color to blue and draw text aligned with the right edge
        // of the clipping rectangle.
        mPaint.setColor(Color.BLUE);
        // Align the RIGHT side of the text with the origin.
        mPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(getContext().getString(R.string.clipping),
            mClipRectRight, mTextOffset, mPaint)
    }
}