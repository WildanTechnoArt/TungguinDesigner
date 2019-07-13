package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.profile.ProfileResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import io.reactivex.Observable

class UpdatePassRepositoryImp(private val baseApiService: BaseApiService) : UpdatePassRepository{
    override fun updatePassword(authHeader: String, accept: String, name: String,
                                email: String, phone: String, password: String,
                                c_password: String): Observable<ProfileResponse>
            = baseApiService.updatePassword(authHeader, accept, name, email, phone, password, c_password)
}