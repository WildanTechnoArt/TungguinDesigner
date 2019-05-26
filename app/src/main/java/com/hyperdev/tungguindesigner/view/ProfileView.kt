package com.hyperdev.tungguindesigner.view

import android.content.Context
import android.support.v4.app.Fragment
import com.hyperdev.tungguindesigner.model.profile.DataUser
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData

class ProfileView {

    interface View{
        fun displayProfile(profileItem: DataUser)
        fun loadFile(file: FileData?)
        fun displayProgress()
        fun hideProgress()
    }

    interface Presenter{
        fun getUserProfile(token: String, context: Context)
        fun takeImageFromGallry(fragment: Fragment)
        fun onDestroy()
    }

}