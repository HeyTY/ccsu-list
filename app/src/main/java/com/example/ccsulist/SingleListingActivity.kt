package com.example.ccsulist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.ccsulist.models.Listing
import kotlinx.android.synthetic.main.activity_single_listing.*
import kotlinx.android.synthetic.main.item_listing.view.*

private const val TAG = "SingleListingActivity"
class SingleListingActivity : AppCompatActivity() {


    private lateinit var listing: Listing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_listing)

        listing = intent.getSerializableExtra(EXTRA_LISTING_INFO) as Listing
        Log.i(TAG, "${listing.title} single activity loaded")


        // Retrieve listing properties from serialized listing obj
        Glide.with(this).load(listing.imageUrl).into(singleListingImageView)
        titleTextView.text = listing.title
        priceTextView.text = "Price: $${listing.price}"
        locationTextView.text = "Location: ${listing.location}"
        descriptionTextView.text = "Description: ${listing.description}"
    }



}
