package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.fcm.FcmResponse
import io.reactivex.Observable

interface TokenFCMRepository {
    fun postTokenFCM(authHeader: String, accept: String, token: String?) : Observable<FcmResponse>
}