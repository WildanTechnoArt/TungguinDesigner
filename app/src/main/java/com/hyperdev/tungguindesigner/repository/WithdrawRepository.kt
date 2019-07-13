package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.requestwithdraw.WithdrawResponse
import io.reactivex.Observable

interface WithdrawRepository {
    fun requestWithdraw(authHeader: String, accept: String, bankOwner: String,
                        bankName: String, bankAccount: String, bankBranch: String,
                        amount: String) : Observable<WithdrawResponse>
}