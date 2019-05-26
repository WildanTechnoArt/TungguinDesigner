package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.ordernotification.AcceptResponse
import com.hyperdev.tungguindesigner.model.ordernotification.CheckOrderResponse
import com.hyperdev.tungguindesigner.model.ordernotification.RejectResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import io.reactivex.Flowable

class GetOrderRepositoryImpl (private val baseApiService: BaseApiService): GetOrderRepository {
    override fun acceptOrder(token: String, accept: String, order_id: String): Flowable<AcceptResponse> = baseApiService.acceptOrder(token, accept, order_id)
    override fun rejectOrder(token: String, accept: String, order_id: String): Flowable<RejectResponse> = baseApiService.rejectOrder(token, accept, order_id)
    override fun checkOrderOffer(token: String, order_id: String): Flowable<CheckOrderResponse> = baseApiService.checkOrderOffer(token, order_id)
}