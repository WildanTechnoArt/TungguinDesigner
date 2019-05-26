package com.hyperdev.tungguindesigner.model.ordernotification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CheckOrderData (

    @SerializedName("remain_time")
    @Expose
    var remainTime: Long? = null,

    @SerializedName("is_available")
    @Expose
    var isAvailable: Boolean? = null
)