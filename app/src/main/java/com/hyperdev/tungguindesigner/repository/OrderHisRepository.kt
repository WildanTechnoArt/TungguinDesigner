package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.historiorder.HistoriOrderResponse
import io.reactivex.Flowable

interface OrderHisRepository {
    fun getOrderHistori(token: String, accept: String, page: Int) : Flowable<HistoriOrderResponse>
}