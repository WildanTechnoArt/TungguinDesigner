package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.login.LoginResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import io.reactivex.Observable

class LoginRepositoryImp (private val baseApiService: BaseApiService): LoginRepository {
    override fun loginUser(email: String, password: String): Observable<LoginResponse> = baseApiService.loginRequest(email, password)
}