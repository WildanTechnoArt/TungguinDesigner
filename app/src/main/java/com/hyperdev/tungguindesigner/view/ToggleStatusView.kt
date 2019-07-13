package com.hyperdev.tungguindesigner.view

class ToggleStatusView {

    interface View {
        fun onSuccessSendStatus(kondisi: Boolean)
        fun showToggleProgress()
        fun hideTogglePregress()
        fun noInternetConnection(message: String)
    }

    interface Presenter {
        fun toggleStatus(authHeader: String, accept: String, status: String, kondisi: Boolean)
        fun onDestroy()
    }
}