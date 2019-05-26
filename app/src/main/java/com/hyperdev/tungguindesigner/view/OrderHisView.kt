package com.hyperdev.tungguindesigner.view

import android.content.Context
import com.hyperdev.tungguindesigner.model.historiorder.HistoriOrderItem
import com.hyperdev.tungguindesigner.model.historiorder.HistoriOrderResponse

class OrderHisView {

    interface View{
        fun loadWithdrawHis(item: List<HistoriOrderItem>)
        fun loadWithdrawData(order: HistoriOrderResponse)
        fun displayProgress()
        fun hideProgress()
        fun onSuccess()
    }

    interface Presenter{
        fun getHistoriOrder(token: String, context: Context, page: Int)
        fun onDestroy()
    }

}