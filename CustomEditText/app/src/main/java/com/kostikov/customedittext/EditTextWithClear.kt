package com.kostikov.customedittext

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Custom EditText view with clear button at the end of field.
 *
 * @author Kostikov Aleksey.
 */
class EditTextWithClear: AppCompatEditText {

    private lateinit var clearButtonImage: Drawable

    constructor(context: Context?) : super(context) {
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    private fun init() {
        clearButtonImage = ResourcesCompat.getDrawable(resources, R.drawable.ic_clear_opaque_24dp, null)!!

        setOnTouchListener(ClearTouchEventListener())
        addTextChangedListener(ClearButtonWatcher())

        text?.isNotEmpty().apply {
            showClearButton()
        }
    }

    /**
    *   Shows the clear (X) button.
    */
    private fun showClearButton()=
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, clearButtonImage, null)

    /**
    *   Hides the clear button.
    */
    private fun hideClearButton() =
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)

    private inner class ClearButtonWatcher : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            if (s != null) {
                if (s.isEmpty()) {
                    this@EditTextWithClear.hideClearButton()
                } else {
                    this@EditTextWithClear.showClearButton()
                }
            }
        }
    }

    private inner class ClearTouchEventListener : OnTouchListener {

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            var result = false;
            if (getCompoundDrawablesRelative()[2] != null) {
                var clearButtonStart: Float
                var clearButtonEnd: Float
                var isClearButtonClicked = false

                // Detect the touch in RTL or LTR layout direction.
                if (layoutDirection == LAYOUT_DIRECTION_RTL) {
                    // If RTL, get the end of the button on the left side.
                    clearButtonEnd = clearButtonImage.intrinsicWidth + paddingStart.toFloat()
                    // If the touch occurred before the end of the button,
                    // set isClearButtonClicked to true.
                    if (event.getX() < clearButtonEnd) {
                        isClearButtonClicked = true;
                    }
                } else {
                    // Layout is LTR.
                    // Get the start of the button on the right side.
                    clearButtonStart = (getWidth() - getPaddingEnd()
                            - clearButtonImage.intrinsicWidth).toFloat();
                    // If the touch occurred after the start of the button,
                    // set isClearButtonClicked to true.
                    if (event.getX() > clearButtonStart) {
                        isClearButtonClicked = true;
                    }
                }

                if (isClearButtonClicked) {
                    // Check for ACTION_DOWN (always occurs before ACTION_UP).
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        // Switch to the black version of clear button.
                        clearButtonImage =
                                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_clear_black_24dp, null)!!
                        showClearButton()

                        result = true;
                    }
                    // Check for ACTION_UP.
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        // Switch to the opaque version of clear button.
                        clearButtonImage =
                                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_clear_opaque_24dp, null)!!
                        // Clear the text and hide the clear button.
                        getText()?.clear();
                        hideClearButton();

                        result = true;
                    }
                }
            }
            return result
        }
    }
}