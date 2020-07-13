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
import dev.smoketrees.twist.model.twist.RegisterDetails
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.ui.home.AnimeViewModel
import dev.smoketrees.twist.utils.*
import kotlinx.android.synthetic.main.fragment_account.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel


class AccountFragment : Fragment() {

    private val viewModel: AnimeViewModel by sharedViewModel()
    private val pref: SharedPreferences by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        signup_text.text = spannable

        register_button.setOnClickListener {
            email_til.error = null
            username_til.error = null
            password_til.error = null
            password_til_confirm.error = null

            val username = username_til.editText?.text.toString()
            val email = email_til.editText?.text.toString()
            val pass = password_til.editText?.text.toString()
            val confirmPass = password_til_confirm.editText?.text.toString()
            var formValidationFailed = false

            if (!email.isValidEmail()) {
                email_til.error = getString(R.string.email_err)
                formValidationFailed = true
            }

            if (pass != confirmPass) {
                password_til.error = getString(R.string.pass_match_err)
                password_til_confirm.error = password_til.error
                formValidationFailed = true
            }

            if (pass.length < 7) {
                password_til.error = getString(R.string.pass_len_err)
                formValidationFailed = true
            }

            val usernameRegex = Regex("^[a-zA-Z\\-_]{4,20}$")
            if (!usernameRegex.containsMatchIn(username)) {
                username_til.error = getString(R.string.username_error)
                formValidationFailed = true
            }

            if (formValidationFailed) {
                return@setOnClickListener
            }

            val regDetails = RegisterDetails(username, email, pass, confirmPass)

            viewModel.signUp(regDetails).observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Result.Status.LOADING -> hideViews()

                    Result.Status.SUCCESS -> {
                        showViews()
                        pref.edit { putBoolean(Constants.PreferenceKeys.IS_LOGGED_IN, true) }
                        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
                        findNavController()
                            .navigate(AccountFragmentDirections.actionAccountFragment2ToMainActivity())
                    }

                    Result.Status.ERROR -> {
                        showViews()
                        toast(it.message.toString())
                    }
                }
            })
        }
        already_registered.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun hideViews() {
        signup_text.hide()
        username_til.hide()
        email_til.hide()
        password_til.hide()
        password_til_confirm.hide()
        register_button.hide()
        already_registered.hide()
        spinkit.show()
    }

    private fun showViews() {
        signup_text.show()
        username_til.show()
        email_til.show()
        password_til.show()
        password_til_confirm.show()
        register_button.show()
        already_registered.show()
        spinkit.hide()
    }
}
