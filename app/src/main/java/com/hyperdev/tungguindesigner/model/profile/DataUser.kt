package com.hyperdev.tungguindesigner.model.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DataUser (

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("email")
    @Expose
    var email: String? = null,

    @SerializedName("is_active")
    @Expose
    var isActive: Boolean? = null,

    @SerializedName("phone_number")
    @Expose
    var phoneNumber: String? = null,

    @SerializedName("formatted_id")
    @Expose
    var formattedId: String? = null,

    @SerializedName("photo_url")
    @Expose
    var photoUrl: String? = null,

    @SerializedName("hashed_id")
    @Expose
    var hashedId: String? = null
)