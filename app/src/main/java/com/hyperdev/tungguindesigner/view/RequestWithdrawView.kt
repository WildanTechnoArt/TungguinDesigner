package com.hyperdev.tungguindesigner.view

class RequestWithdrawView {

    interface View {
        fun onSuccess()
        fun showPregressBar()
        fun hidePregressBar()
        fun noInternetConnection(message: String)
    }

    interface Presenter {
        fun withdrawWallet(authHeader: String, accept: String, bankOwner: String,
                           bankName: String, bankAccount: String, bankBranch: String,
                           amount: String)
        fun onDestroy()
    }
}