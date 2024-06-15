package com.example.dermai.ui.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.dermai.R
import com.example.dermai.utils.isEmailCorrect

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {
    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                error = if (s != null && !s.toString().isEmailCorrect()) context.getString(R.string.validation_email) else null
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }
}