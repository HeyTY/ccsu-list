package com.example.ccsulist

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ccsulist.models.Listing
import kotlinx.android.synthetic.main.item_listing.view.*
import java.math.BigInteger
import java.security.MessageDigest
import androidx.core.content.ContextCompat.startActivity


private const val TAG = "ListingsAdapter"
class ListingsAdapter(val context: Context, val listings: List<Listing>, val onClickListener: OnClickListener) :
    RecyclerView.Adapter<ListingsAdapter.ViewHolder>() {

    interface OnClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        // Create view
        val view = LayoutInflater.from(context).inflate(R.layout.item_listing,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount() = listings.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listings[position])
        holder.itemView.setOnClickListener{
            Log.i(TAG, "Tapped on position ${position}")
            onClickListener.onItemClick(position)
        }


    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(listing: Listing) {
            val username = listing.user?.username as String
            itemView.tvUsername.text = listing.user?.username
            itemView.tvTitle.text = listing.title
            itemView.tvPrice.text = "$${listing.price}"
            Glide.with(context).load(listing.imageUrl).into(itemView.ivListing)
            Glide.with(context).load(getUniqueProfileAvatar(username)).into(itemView.ivProfile)
            itemView.tvRelativeTime.text = DateUtils.getRelativeTimeSpanString(listing.creationTimeMs)


            itemView.locationBtn.setOnClickListener {
                val gmmIntentUri =
                    Uri.parse("geo:0,0?q=${listing.location}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                context.startActivity(mapIntent)
            }
        }

        // Create unique user avatar
        private fun getUniqueProfileAvatar(username: String) : String{
            val digest = MessageDigest.getInstance("MD5")
            val hash = digest.digest(username.toByteArray())
            val bigInt = BigInteger(hash)
            val hex = bigInt.abs().toString(16)
            return "https://www.gravatar.com/avatar/$hex?d=identicon"

        }
    }






}