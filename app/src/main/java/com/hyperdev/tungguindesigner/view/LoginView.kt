package com.hyperdev.tungguindesigner.view

import android.content.Context

class LoginView {

    interface View {
        fun onSuccess()
        fun showPregressBar()
        fun hidePregressBar()
        fun noInternetConnection(message: String)
    }

    interface Presenter {
        fun loginUser(context: Context, email: String, password: String)
        fun onDestroy()
    }
}