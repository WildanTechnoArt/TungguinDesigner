package com.hyperdev.tungguindesigner.view

class TokenFCMView {

    interface Presenter {
        fun sendTokenFCM(authHeader: String, accept: String, token: String)
        fun onDestroy()
    }
}