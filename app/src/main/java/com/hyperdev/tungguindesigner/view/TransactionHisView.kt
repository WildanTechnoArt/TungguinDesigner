package com.hyperdev.tungguindesigner.view

import android.content.Context
import com.hyperdev.tungguindesigner.model.transactionhistori.TransactionHistori
import com.hyperdev.tungguindesigner.model.transactionhistori.TransactionItem

class TransactionHisView {

    interface View{
        fun loadTransactionHis(item: List<TransactionItem>)
        fun loadTransactionData(histori: TransactionHistori)
        fun displayProgress()
        fun hideProgress()
        fun onSuccess()
    }

    interface Presenter{
        fun getHistoriTransaction(token: String, context: Context, page: Int)
        fun onDestroy()
    }

}