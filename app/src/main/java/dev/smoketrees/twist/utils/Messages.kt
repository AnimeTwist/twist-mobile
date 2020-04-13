package dev.smoketrees.twist.utils

import android.app.Activity
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.view.View
import dev.smoketrees.twist.R


object Messages {

    // Specify notice messages
    val DEFAULT_NOTICE = Notice(
        null,
        R.string.error_default,
        null
    )
    val NOTICES : HashMap<Int, Notice> = hashMapOf(
        111 to Notice(
            R.drawable.ic_no_connection,
            R.string.no_connection,
            Buttons.REFRESH
        ),
        502 to Notice(
            R.drawable.ic_bad_gateway,
            R.string.server_error_502,
            Buttons.OPEN
        ),
        408 to Notice(
            null,
            R.string.client_error_408,
            Buttons.REFRESH
        )
    )

    class Notice(
        val icon: Int?,
        val msg_id: Int,
        val button: Buttons?
    ) {
        private val listeners = hashMapOf(
            Buttons.REFRESH to View.OnClickListener {
                val a = it.context as Activity
                a.recreate()
            },
            Buttons.OPEN to View.OnClickListener {
                val browser = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.WEB))
                it.context.startActivity(browser)
            }
        )

        var listener: View.OnClickListener? = null
        init {
            if (button != null)
                if (listeners.containsKey(button))
                    listener = listeners[button]!!
        }

    }

    enum class Buttons(val text : String) {
        REFRESH("Refresh"),
        OPEN("Open in browser")
    }
}
