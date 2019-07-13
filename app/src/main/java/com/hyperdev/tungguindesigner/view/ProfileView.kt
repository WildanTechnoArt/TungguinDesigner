package com.hyperdev.tungguindesigner.view

import android.content.Context
import com.hyperdev.tungguindesigner.model.profile.DataUser
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProfileView {

    interface View{
        fun displayProfile(profileItem: DataUser)
        fun loadFile(file: FileData?)
        fun displayProgress()
        fun hideProgress()
        fun onSuccessEditProfile()
        fun onSuccessEditFotoProfile()
        fun noInternetConnection(message: String)
    }

    interface DesignerId{
        fun getHashedId(data: DataUser)
    }

    interface DesignerPresenter{
        fun getDesignerId(token: String, context: Context)
        fun onDestroy()
    }

    interface Presenter{

        fun getUserProfile(token: String, context: Context)

        fun updateProfile(authHeader: String, accept: String, name: RequestBody,
                          email: RequestBody, phone: RequestBody)

        fun updateProfileWithImage(authHeader: String, accept: String, name: RequestBody,
                                   email: RequestBody, phone: RequestBody,
                                   image: MultipartBody.Part)

        fun takeImageFromGallry(fragment: androidx.fragment.app.Fragment)
        fun onDestroy()
    }

}