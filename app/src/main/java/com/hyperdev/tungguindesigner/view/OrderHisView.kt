package com.hyperdev.tungguindesigner.view

import android.content.Context
import com.hyperdev.tungguindesigner.model.historiorder.HistoryOrderItem
import com.hyperdev.tungguindesigner.model.historiorder.HistoryOrderResponse

class OrderHisView {

    interface View{
        fun loadWithdrawHis(item: List<HistoryOrderItem>)
        fun loadWithdrawData(order: HistoryOrderResponse)
        fun displayProgress()
        fun hideProgress()
        fun onSuccess()
    }

    interface Presenter{
        fun getHistoriOrder(token: String, context: Context, page: Int)
        fun onDestroy()
    }

}