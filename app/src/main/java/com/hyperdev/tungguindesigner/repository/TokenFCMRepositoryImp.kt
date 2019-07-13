package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.fcm.FcmResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import io.reactivex.Observable

class TokenFCMRepositoryImp(private val baseApiService: BaseApiService) : TokenFCMRepository{
    override fun postTokenFCM(authHeader: String, accept: String, token: String?): Observable<FcmResponse>
            = baseApiService.fcmRequest(authHeader, accept, token)
}