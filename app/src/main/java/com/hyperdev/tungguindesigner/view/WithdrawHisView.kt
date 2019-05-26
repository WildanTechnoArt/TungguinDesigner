package com.hyperdev.tungguindesigner.view

import android.content.Context
import com.hyperdev.tungguindesigner.model.withdrawhistori.WithdrawData
import com.hyperdev.tungguindesigner.model.withdrawhistori.WithdrawItem

class WithdrawHisView {

    interface View{
        fun loadWithdrawHis(item: List<WithdrawItem>)
        fun loadWithdrawData(histori: WithdrawData)
        fun displayProgress()
        fun hideProgress()
        fun onSuccess()
    }

    interface Presenter{
        fun getHistoriWithdraw(token: String, context: Context, page: Int)
        fun onDestroy()
    }

}