package com.hyperdev.tungguindesigner.view

class RemoveTokenFCMView {

    interface View {
        fun onSuccessRemoveToken()
        fun showPregressBar()
        fun hidePregressBar()
        fun noInternetConnection(message: String)
    }

    interface Presenter {
        fun removeTokenFCM(authHeader: String, accept: String, token: String?)
        fun onDestroy()
    }
}