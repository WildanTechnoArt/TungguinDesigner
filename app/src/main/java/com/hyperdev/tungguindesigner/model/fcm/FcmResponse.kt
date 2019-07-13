package com.hyperdev.tungguindesigner.model.fcm

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguindesigner.network.HandleResponse

data class FcmResponse (

    @SerializedName("meta")
    @Expose
    var meta: HandleResponse? = null
)