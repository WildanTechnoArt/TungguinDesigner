package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.withdrawhistori.WithdrawHisResponse
import io.reactivex.Flowable

interface WithdrawHisRepository {
    fun getWithdrawHistori(token: String, accept: String, page: Int) : Flowable<WithdrawHisResponse>
}