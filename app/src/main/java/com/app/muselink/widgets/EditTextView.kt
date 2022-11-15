package com.app.muselink.widgets

import android.content.Context
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.app.muselink.R

class EditTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatEditText(context, attrs, defStyleAttr) {
    private var hashtagindex = -1
    private var enableTagging:Boolean = false
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.EditTextView, 0, 0).apply {
            try {
                enableTagging = getBoolean(R.styleable.EditTextView_enableTagging, false)
            } finally {
                recycle()
            }
        }
    }


    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        text?.let { handleTagging(text, start, lengthBefore, lengthAfter) }
    }

    private fun handleTagging(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        if (!enableTagging || text.length <= start) return

        val formattedText = text.substring(0, start).trim();
        val lastWord =  formattedText.split(Regex("\\s+")).last()
        val tagIndex = if (lastWord.isNotEmpty() && lastWord[0] == '#') formattedText.lastIndexOf('#') else -1

        if (tagIndex >= 0) {
            val foregroundSpan = ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary))
            getText()?.setSpan(foregroundSpan, tagIndex, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}