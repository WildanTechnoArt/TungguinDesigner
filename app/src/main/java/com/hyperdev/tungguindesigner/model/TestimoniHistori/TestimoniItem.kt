package com.hyperdev.tungguindesigner.model.TestimoniHistori

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TestimoniItem (

    @SerializedName("star_rating")
    @Expose
    var starRating: Int? = null,

    @SerializedName("designer_testimonial")
    @Expose
    var designerTestimonial: String? = null,

    @SerializedName("designer_tip_formatted")
    @Expose
    var designerTipFormatted: String? = null,

    @SerializedName("formatted_date")
    @Expose
    var formattedDate: String? = null,

    @SerializedName("formatted_order_id")
    @Expose
    var formattedOrderId: String? = null
)