package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.announcement.AnnouncementResponse
import com.hyperdev.tungguindesigner.model.transactionhistori.TransactionResponse
import io.reactivex.Flowable

interface TransactionHisRepository {
    fun getTransactionHistori(token: String, accept: String, page: Int) : Flowable<TransactionResponse>
    fun getDashboard(token: String, accept: String) : Flowable<TransactionResponse>
    fun announcementDesigner(token: String) : Flowable<AnnouncementResponse>
}