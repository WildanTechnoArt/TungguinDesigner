package com.hyperdev.tungguindesigner.view

import android.content.Context
import com.hyperdev.tungguindesigner.model.ordernotification.CheckOrderData
import com.hyperdev.tungguindesigner.model.profile.DataUser

class NewOrderView {

    interface View{
        fun displayProfile(profileItem: DataUser)
        fun onDisplayProgress()
        fun onHideProgress()
        fun acceptSuccess()
        fun rejectSuccess()
        fun orderOffer(check: CheckOrderData)
    }

    interface Presenter{
        fun getUserProfile(token: String, context: Context)
        fun acceptRequest(context: Context, token: String, order_id: String)
        fun rejectRequest(context: Context, token: String, order_id: String)
        fun checkOrderOffer(token: String, order_id: String)
        fun onDestroy()
    }
}