package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.ordernotification.AcceptResponse
import com.hyperdev.tungguindesigner.model.ordernotification.CheckOrderResponse
import com.hyperdev.tungguindesigner.model.ordernotification.RejectResponse
import io.reactivex.Flowable

interface GetOrderRepository {
    fun acceptOrder(token: String, accept: String, order_id: String) : Flowable<AcceptResponse>
    fun rejectOrder(token: String, accept: String, order_id: String) : Flowable<RejectResponse>
    fun checkOrderOffer(token: String, order_id: String) : Flowable<CheckOrderResponse>
}