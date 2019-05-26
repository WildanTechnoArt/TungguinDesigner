package com.hyperdev.tungguindesigner.view

import android.content.Context
import com.hyperdev.tungguindesigner.model.announcement.AnnouncementData
import com.hyperdev.tungguindesigner.model.transactionhistori.TransactionData

class DashboardView {

    interface View{
        fun loadSaldoData(saldo: TransactionData)
        fun loaddAnnouncement(text: AnnouncementData)
        fun displayProgress()
        fun hideProgress()
        fun onSuccess()
    }

    interface Presenter{
        fun getDashboardData(token: String, context: Context)
        fun getAnnouncementData(token: String)
        fun onDestroy()
    }

}