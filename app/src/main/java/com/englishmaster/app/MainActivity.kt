package com.englishmaster.app

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import android.view.Gravity
import android.graphics.Typeface
import android.graphics.Color

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = TextView(this).apply {
            text = "English Master\n\nتعلم الإنجليزية من الصفر"
            textSize = 26f
            setTextColor(Color.rgb(30, 60, 100))
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }

        setContentView(title)
    }
}
