package dev.smoketrees.twist.ui.auth

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dev.smoketrees.twist.R
import dev.smoketrees.twist.model.twist.LoginDetails
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.ui.home.AnimeViewModel
import dev.smoketrees.twist.utils.Constants
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.show
import dev.smoketrees.twist.utils.toast
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

class LoginFragment : Fragment() {

    private val viewModel: AnimeViewModel by sharedViewModel()
    private val pref: SharedPreferences by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (pref.contains(Constants.PreferenceKeys.IS_LOGGED_IN)) {
            findNavController()
                .navigate(LoginFragmentDirections.actionLoginFragment2ToMainActivity())
        }

        val spannable = SpannableString(getString(R.string.twist_moe))
        spannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.toolbar_text_color
                )
            ),
            6, 11,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        login_text.text = spannable

        login_button.setOnClickListener {
            email_til.error = null
            password_til.error = null

            val username = email_til.editText?.text.toString()
            val pass = password_til.editText?.text.toString()
            var formValidationFailed = false

            if (username.isBlank()) {
                email_til.error = getString(R.string.email_username_error)
                formValidationFailed = true
            }

            if (pass.length < 7) {
                password_til.error = getString(R.string.pass_len_err)
                formValidationFailed = true
            }

            if (formValidationFailed) {
                return@setOnClickListener
            }

            val loginDetails = LoginDetails(username, pass)
            viewModel.signIn(loginDetails).observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Result.Status.LOADING -> hideViews()

                    Result.Status.SUCCESS -> {
                        showViews()
                        pref.edit { putBoolean(Constants.PreferenceKeys.IS_LOGGED_IN, true) }
                        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
                        findNavController()
                            .navigate(LoginFragmentDirections.actionLoginFragment2ToMainActivity())
                    }

                    Result.Status.ERROR -> {
                        showViews()
                        toast(it.message?.code.toString())
                    }
                }
            })
        }

        not_registered.setOnClickListener {
            findNavController()
                .navigate(LoginFragmentDirections.actionLoginFragment2ToAccountFragment2())
        }
    }

    private fun hideViews() {
        login_text.hide()
        email_til.hide()
        password_til.hide()
        login_button.hide()
        not_registered.hide()
        spinkit.show()
    }

    private fun showViews() {
        login_text.show()
        email_til.show()
        password_til.show()
        login_button.show()
        not_registered.show()
        spinkit.hide()
    }
}