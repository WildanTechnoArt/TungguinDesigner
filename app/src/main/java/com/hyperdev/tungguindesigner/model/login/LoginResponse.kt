package com.hyperdev.tungguindesigner.model.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguindesigner.network.HandleResponse

data class LoginResponse (
    @SerializedName("data")
    @Expose
    var getData: DataLogin? = null,

    @SerializedName("meta")
    @Expose
    var getMeta: HandleResponse? = null
)