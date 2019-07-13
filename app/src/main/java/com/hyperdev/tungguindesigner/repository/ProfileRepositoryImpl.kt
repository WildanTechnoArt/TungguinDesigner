package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.profile.ProfileResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProfileRepositoryImpl (private val baseApiService: BaseApiService): ProfileRepository {

    override fun updateProfile(
        authHeader: String,
        accept: String,
        name: RequestBody,
        email: RequestBody,
        phone: RequestBody
    ): Observable<ProfileResponse> = baseApiService.updateProfile(authHeader, accept, name, email, phone)


    override fun updateProfileWithImage(
        authHeader: String,
        accept: String,
        name: RequestBody,
        email: RequestBody,
        phone: RequestBody,
        image: MultipartBody.Part
    ): Observable<ProfileResponse> = baseApiService.updateProfileWithImage(authHeader, accept, name, email, phone, image)

    override fun getProfile(token: String, accept: String): Flowable<ProfileResponse> = baseApiService.getProfile(token, accept)
}