package dev.smoketrees.twist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import dev.smoketrees.twist.model.twist.RegisterDetails
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.ui.home.AnimeViewModel
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.isValidEmail
import dev.smoketrees.twist.utils.show
import dev.smoketrees.twist.utils.toast
import kotlinx.android.synthetic.main.fragment_account.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : Fragment() {

    private val viewModel: AnimeViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        register_fab.setOnClickListener {
            email_til.error = null
            username_til.error = null
            password_til.error = null
            password_til_confirm.error = null

            val username = username_til.editText?.text.toString()
            val email = email_til.editText?.text.toString()
            val pass = password_til.editText?.text.toString()
            val confirmPass = password_til_confirm.editText?.text.toString()
            var flag = false

            if (!email.isValidEmail()) {
                email_til.error = getString(R.string.email_err)
                flag = true
            }

            if (pass != confirmPass) {
                password_til.error = getString(R.string.pass_match_err)
                password_til_confirm.error = password_til.error
                flag = true
            }

            if (pass.length < 7) {
                password_til.error = getString(R.string.pass_len_err)
                flag = true
            }

            val usernameRegex = Regex("^[a-zA-Z\\-_]{4,20}$")
            if (!usernameRegex.containsMatchIn(username)) {
                username_til.error = "4-20 letters/dashes/underscores"
                flag = true
            }

            if (flag) {
                return@setOnClickListener
            }

            val regDetails = RegisterDetails(username, email, pass, confirmPass)

            viewModel.signUp(regDetails).observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Result.Status.LOADING -> {
                        username_til.hide()
                        email_til.hide()
                        password_til.hide()
                        password_til_confirm.hide()
                        register_fab.hide()
                        spinkit.show()
                    }

                    Result.Status.SUCCESS -> {
                        username_til.show()
                        email_til.show()
                        password_til.show()
                        password_til_confirm.show()
                        register_fab.show()
                        spinkit.hide()
                    }

                    Result.Status.ERROR -> {
                        username_til.show()
                        email_til.show()
                        password_til.show()
                        password_til_confirm.show()
                        register_fab.show()
                        spinkit.hide()
                        toast(it.message.toString())
                    }
                }
            })
        }
    }
}
