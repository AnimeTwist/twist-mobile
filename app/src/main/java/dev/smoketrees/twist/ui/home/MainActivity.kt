package dev.smoketrees.twist.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import dev.smoketrees.twist.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val spannable = SpannableString("twist.moe")
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.toolbar_text_color)),
            6, 9,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        toolbar_text.text = spannable
    }
}
