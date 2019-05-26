package com.hyperdev.tungguindesigner.model.announcement

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguindesigner.network.ErrorResponse

data class AnnouncementResponse (

    @SerializedName("meta")
    @Expose
    var meta: ErrorResponse? = null,

    @SerializedName("data")
    @Expose
    var data: AnnouncementData? = null
)