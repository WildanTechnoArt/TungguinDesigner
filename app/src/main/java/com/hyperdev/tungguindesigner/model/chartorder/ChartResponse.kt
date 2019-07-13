package com.hyperdev.tungguindesigner.model.chartorder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguindesigner.network.HandleResponse

data class ChartResponse (

    @SerializedName("meta")
    @Expose
    var meta: HandleResponse? = null,

    @SerializedName("data")
    @Expose
    var data: ChartData? = null
)