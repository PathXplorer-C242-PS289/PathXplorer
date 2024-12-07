package com.example.pathxplorer.ui.utils.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class PasswordEditText : TextInputEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.length < 8) {
                    error = "Password must be at least 8 characters"
                }
                if (!s.isNullOrEmpty() && (s.length < 8
                            || !s.any { it.isUpperCase() }
                            || !s.any { !it.isLetterOrDigit() }
                            || !s.any { it.isDigit() })) {
                    error = "Password must be at least 8 characters, contain at least one uppercase letter, one digit, and one special character"
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}