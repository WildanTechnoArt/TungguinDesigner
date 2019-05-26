package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.withdrawhistori.WithdrawHisResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import io.reactivex.Flowable

class WithdrawHisRepositoryImpl (private val baseApiService: BaseApiService): WithdrawHisRepository {
    override fun getWithdrawHistori(token: String, accept: String, page: Int): Flowable<WithdrawHisResponse> = baseApiService.getWithdrawHistori(token, accept, page)
}