package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.login.LoginResponse
import io.reactivex.Observable

interface LoginRepository {
    fun loginUser(email: String, password: String) : Observable<LoginResponse>
}