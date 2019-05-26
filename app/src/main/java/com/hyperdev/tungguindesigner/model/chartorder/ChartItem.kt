package com.hyperdev.tungguindesigner.model.chartorder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ChartItem (

    @SerializedName("date")
    @Expose
    var date: String? = null,

    @SerializedName("count")
    @Expose
    var count: Int? = null
)