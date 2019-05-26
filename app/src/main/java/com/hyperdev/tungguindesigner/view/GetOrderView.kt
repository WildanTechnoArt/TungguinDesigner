package com.hyperdev.tungguindesigner.view

import android.content.Context
import com.hyperdev.tungguindesigner.model.ordernotification.CheckOrderData

class GetOrderView {

    interface View{
        fun onDisplayProgress()
        fun onHideProgress()
        fun acceptSuccess()
        fun rejectSuccess()
        fun orderOffer(check: CheckOrderData)
    }

    interface Presenter{
        fun acceptRequest(context: Context, token: String, order_id: String)
        fun rejectRequest(context: Context, token: String, order_id: String)
        fun checkOrderOffer(token: String, order_id: String)
        fun onDestroy()
    }
}