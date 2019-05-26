package com.hyperdev.tungguindesigner.model.chartorder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ChartData (

    @SerializedName("today")
    @Expose
    var today: Int? = null,

    @SerializedName("this_week")
    @Expose
    var thisWeek: Int? = null,

    @SerializedName("total")
    @Expose
    var total: Int? = null,

    @SerializedName("chart")
    @Expose
    var item: List<ChartItem>? = null
)