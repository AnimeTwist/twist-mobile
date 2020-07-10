package dev.smoketrees.twist.ui.home

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import dev.smoketrees.twist.R
import dev.smoketrees.twist.utils.Constants
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.show
import kotlinx.android.synthetic.main.activity_main.*
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
