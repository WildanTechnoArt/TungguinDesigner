package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.announcement.AnnouncementResponse
import com.hyperdev.tungguindesigner.model.transactionhistori.TransactionResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import io.reactivex.Flowable

class TransactionHisRepositoryImpl (private val baseApiService: BaseApiService): TransactionHisRepository {
    override fun getTransactionHistori(token: String, accept: String, page: Int): Flowable<TransactionResponse> = baseApiService.getTransactionHistori(token, accept, page)
    override fun getDashboard(token: String, accept: String): Flowable<TransactionResponse> = baseApiService.getDashboard(token, accept)
    override fun announcementDesigner(token: String): Flowable<AnnouncementResponse> = baseApiService.announcementDesigner(token)
}