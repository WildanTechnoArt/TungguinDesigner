package com.hyperdev.tungguindesigner.model.requestwithdraw

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguindesigner.network.HandleResponse

data class WithdrawResponse (

    @SerializedName("meta")
    @Expose
    var meta: HandleResponse? = null,

    @SerializedName("data")
    @Expose
    var data: WithdrawData? = null
)