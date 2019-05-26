package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.historiorder.HistoriOrderResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import io.reactivex.Flowable

class OrderHisRepositoryImpl (private val baseApiService: BaseApiService): OrderHisRepository {
    override fun getOrderHistori(token: String, accept: String, page: Int): Flowable<HistoriOrderResponse> = baseApiService.getOrderHistori(token, accept, page)
}