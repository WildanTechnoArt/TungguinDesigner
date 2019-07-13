package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.requestwithdraw.WithdrawResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import io.reactivex.Observable

class WithdrawRepositoryImp(private val baseApiService: BaseApiService) : WithdrawRepository{

    override fun requestWithdraw(authHeader: String, accept: String, bankOwner: String,
                                 bankName: String, bankAccount: String, bankBranch: String,
                                 amount: String): Observable<WithdrawResponse>
            = baseApiService.requestWithdraw(authHeader, accept, bankOwner, bankName, bankAccount, bankBranch, amount)
}