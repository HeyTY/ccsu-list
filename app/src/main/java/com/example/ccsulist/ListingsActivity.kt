package com.example.ccsulist

import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ccsulist.models.Listing
import com.example.ccsulist.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.item_listing.*


private const val TAG = "Listing"
 const val EXTRA_USERNAME = "EXTRA_USERNAME"
 const val EXTRA_LISTING_INFO = "EXTRA_LISTING_INFO"
open class ListingsActivity : AppCompatActivity() {



    private var userLoggedIn: User? = null
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var listings: MutableList<Listing>
    private lateinit var adapter: ListingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)



        // Create layout file that represents a single listing

        // Create data source
        listings = mutableListOf()
        // Create adapter
        adapter = ListingsAdapter(this,listings, object: ListingsAdapter.OnClickListener{
            override fun onItemClick(position: Int) {
                Log.i(TAG, "onItemClick $position")
                // Navigate user to single activity when click
                val intent = Intent(this@ListingsActivity,SingleListingActivity::class.java)
                intent.putExtra(EXTRA_LISTING_INFO, listings[position])
                startActivity(intent)
            }
        })
        // Bind adapter and layout manager to Recycler View
        rvListings.adapter = adapter
        rvListings.layoutManager = LinearLayoutManager(this)

        // When navigate to listing when user clicks

        // Make query to firebase to get data
        firestoreDb = FirebaseFirestore.getInstance()


        // Look at firebase user collection for signedInUser
        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                userLoggedIn = userSnapshot.toObject(User::class.java)
                Log.i(TAG, "Logged in user:  $userLoggedIn")
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "Error retrieving logged in user", exception)
            }


        var listingsReference = firestoreDb
            .collection("listings")
            .limit(50)
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)
        Log.i(TAG,"${listingsReference}")


        // Update listingReference we are in the profile activity
        val username = intent.getStringExtra(EXTRA_USERNAME)
        if (username != null){
            // Display custom actionbar
            supportActionBar?.title = "Hello, ${username}!"
            listingsReference = listingsReference.whereEqualTo("user.username", username)
        }

        // Snapshot listener, to update whenever there is a change to firestore
        listingsReference.addSnapshotListener { snapshot, exception ->

            // error handler for request
            if (exception != null || snapshot == null){
                Log.e(TAG,"Exception when querying listing", exception)
                return@addSnapshotListener
            }

            // Convert to list of objects
            val listingsList = snapshot.toObjects(Listing::class.java)

            listings.clear() //clear old data
            // Update adapter, tell we have receive updated data
            listings.addAll(listingsList)
            adapter.notifyDataSetChanged()

            for(listing in listingsList){
                Log.i(TAG,"Listing: ${listing}")
            }

        }

        // Create listing button click listener
        createAB.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_listings,menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Which item on the menu the user has chosen
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // Validate user has chosen menu profile
        if (item.itemId == R.id.menu_profile){
            val intent = Intent(this,ProfileActivity::class.java)
            intent.putExtra(EXTRA_USERNAME, userLoggedIn?.username)
            startActivity(intent)

        }
        return super.onOptionsItemSelected(item)
    }
}
