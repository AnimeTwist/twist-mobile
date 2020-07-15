package dev.smoketrees.twist.ui.home

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import dev.smoketrees.twist.R
import dev.smoketrees.twist.utils.Constants
import dev.smoketrees.twist.utils.Messages
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.show
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val navController by lazy { (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).findNavController() }

    private val pref by inject<SharedPreferences>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndApplyTheme()
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        val spannable = SpannableString(getString(R.string.twist_moe))
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.toolbar_text_color)),
            6, 11,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        toolbar_text.text = spannable

        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp() = navController.navigateUp()

    fun showLoader() = spinkit.show()
    fun hideLoader() = spinkit.hide(500)

    fun notice(errCode: Int?) {
        if (errCode == null) {
            noticeClear()
            return
        }
        val noticeObject = if (errCode == 0) Messages.DEFAULT_NOTICE else Messages.NOTICES[errCode]!!

        if (noticeObject.icon != null) {
            notice_icon.setImageDrawable(ContextCompat.getDrawable(this, noticeObject.icon))
            notice_icon.show()
        }
        notice.text = resources.getString(noticeObject.msg_id)
        if (noticeObject.button != null) {
            notice_button.text = noticeObject.button.text
            notice_button.setOnClickListener(noticeObject.listener)
            notice_button.show()
        }

        spinkit.hide()
        notice_bar.show()

        // Disable scrolling while error notice displayed
        val bar = toolbar as MaterialToolbar
        val params = bar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
        bar.layoutParams = params
    }

    fun noticeClear() {
        if (notice_bar.isVisible) {
            notice_bar.hide()
            notice_icon.hide()
            notice_button.hide()
            notice.text = emptySequence<Char>().toString()
        }

        // Restore scrolling behaviour
        val bar = toolbar as MaterialToolbar
        val params = bar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        bar.layoutParams = params
    }

    private fun checkAndApplyTheme() {
        if (pref.contains(Constants.PreferenceKeys.IS_DAY)) { // Check if this pref exists or not, if not then check what theme does system have
            if (pref.getBoolean(Constants.PreferenceKeys.IS_DAY, true)) { // Day
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else { // Night
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        } else { // Check what theme does system follow
            if (systemIsDark()) { // System in dark
                pref.edit { putBoolean(Constants.PreferenceKeys.IS_DAY, false) }
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else { // System in light
                pref.edit { putBoolean(Constants.PreferenceKeys.IS_DAY, true) }
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun systemIsDark(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false // Assume day
        }
    }
}
