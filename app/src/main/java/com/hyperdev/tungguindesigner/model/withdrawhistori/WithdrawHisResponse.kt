package com.hyperdev.tungguindesigner.model.withdrawhistori

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguindesigner.network.ErrorResponse

data class WithdrawHisResponse (

    @SerializedName("meta")
    @Expose
    var meta: ErrorResponse? = null,

    @SerializedName("data")
    @Expose
    var data: WithdrawData? = null
)