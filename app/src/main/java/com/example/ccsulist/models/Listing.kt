package com.example.ccsulist.models

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class Listing (
    var title: String = "",
    var price: String = "",
    var description: String = "",
    var location: String = "1615 Stanley St, New Britain, CT 06053",
    @get: PropertyName("image_url") @set: PropertyName("image_url")var imageUrl: String = "",
    @get: PropertyName("creation_time_ms") @set: PropertyName("creation_time_ms") var creationTimeMs: Long = 0,
    var user: User? = null

) : Serializable


