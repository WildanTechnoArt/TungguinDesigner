package com.hyperdev.tungguindesigner.view

class UpdatePassView {

    interface View {
        fun onSuccess()
        fun showPregressBar()
        fun hidePregressBar()
        fun noInternetConnection(message: String)
    }

    interface Presenter {
        fun updatePassword(authHeader: String, accept: String, name: String,
                           email: String, phone: String, password: String,
                           c_password: String)
        fun onDestroy()
    }
}