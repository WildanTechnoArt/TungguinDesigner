package com.hyperdev.tungguindesigner.model.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguindesigner.network.HandleResponse

data class ProfileResponse (
    @SerializedName("data")
    @Expose
    var data: DataUser? = null,

    @SerializedName("meta")
    @Expose
    var getMeta: HandleResponse? = null
)