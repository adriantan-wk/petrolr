package com.example.apptivitylab.demoapp.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.models.User
import kotlinx.android.synthetic.main.fragment_user_profile.*
import java.text.SimpleDateFormat

/**
 * Created by ApptivityLab on 23/02/2018.
 */

class UserProfileFragment: Fragment() {

    companion object {
        const val USER_EXTRA = "user_object"

        fun newInstance(context: Context, currentUser: User): UserProfileFragment {
            val fragment = UserProfileFragment()

            var args = Bundle()
            args.putParcelable(USER_EXTRA, currentUser)

            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var currentUser: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            this.currentUser = it.getParcelable(USER_EXTRA)
        }

        this.updateUserProfileDetails(this.currentUser)
    }

    private fun updateUserProfileDetails(user: User) {
        this.usernameTextView.text = user.username
        this.emailTextView.text = user.email
        this.phoneNoTextView.text = user.phoneNo

        this.createdOnTextView.text = SimpleDateFormat("dd MMMM yyyy").format(user.userCreatedAt)
    }
}