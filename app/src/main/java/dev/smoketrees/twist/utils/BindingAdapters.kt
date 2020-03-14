package dev.smoketrees.twist.utils

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    if (isGone) view.hide() else view.show()
}