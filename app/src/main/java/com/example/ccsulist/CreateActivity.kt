package com.example.ccsulist

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.ccsulist.models.Listing
import com.example.ccsulist.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create.*


private const val TAG = "CreateActivity"
private const val PICK_PHOTO_CODE = 717
class CreateActivity : AppCompatActivity() {
    private var userLoggedIn: User? = null
    private var photoUri: Uri? = null
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        // Init storage reference
        storageReference = FirebaseStorage.getInstance().reference

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


        // Handle onClick for selectImage
        btnSelectImage.setOnClickListener {
            Log.i(TAG, "Open image picker on android device")
            val imagePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            imagePickerIntent.type = "image/*"
            if (imagePickerIntent.resolveActivity(packageManager) != null){
                startActivityForResult(imagePickerIntent, PICK_PHOTO_CODE)
            }
        }

        btnSubmit.setOnClickListener {
            submitButtonHandler()
        }
    }

    // Submit Helper Function
    private fun submitButtonHandler(){
        if(photoUri == null){
            Toast.makeText(this, "Please select a photo", Toast.LENGTH_SHORT).show()
            return
        }
        if(titleET.text.isBlank()){
            Toast.makeText(this,"Please enter a title", Toast.LENGTH_SHORT).show()
            return
        }
        if(priceET.text.isBlank()){
            Toast.makeText(this,"Please enter a price", Toast.LENGTH_SHORT).show()
            return
        }
        if(locationET.text.isBlank()){
            Toast.makeText(this,"Please enter a location", Toast.LENGTH_SHORT).show()
            return
        }
        if(descriptionML.text.isBlank()){
            Toast.makeText(this,"Please enter a description", Toast.LENGTH_SHORT).show()
            return
        }
        if (userLoggedIn == null) {
            Toast.makeText(this, "No user logged in, please sign in", Toast.LENGTH_SHORT).show()
            return
        }

        btnSubmit.isEnabled = false
        val photoUploadUri = photoUri as Uri
        val photoReference = storageReference.child("images/${System.currentTimeMillis()}-photo.jpg")
        // Upload listing image to firebase storage with async tasks
        photoReference.putFile(photoUploadUri)
            .continueWithTask { photoUploadTask ->
                Log.i(TAG, "uploaded bytes: ${photoUploadTask.result?.bytesTransferred}")
                // Get image url of uploaded photo
                photoReference.downloadUrl
            }.continueWithTask { downloadUrlTask ->
                // Create a listing object with the image url then add it to the firebase db collection
                val listing = Listing(
                    titleET.text.toString(),
                    priceET.text.toString(),
                    descriptionML.text.toString(),
                    locationET.text.toString(),
                    downloadUrlTask.result.toString(),
                    System.currentTimeMillis(),
                    userLoggedIn)
                Log.e(TAG, "Title:${listing.title} Price:${listing.price} descrip:${listing.description} Image:${listing.imageUrl}")
                firestoreDb.collection("listings").add(listing)
            }.addOnCompleteListener { listingCreationTask ->
                btnSubmit.isEnabled = true
                if(!listingCreationTask.isSuccessful){
                    Log.e(TAG, "Exception found during Firebase operations", listingCreationTask.exception)
                    Toast.makeText(this,"Failed to save listing", Toast.LENGTH_SHORT).show()
                }
                titleET.text.clear()
                priceET.text.clear()
                descriptionML.text.clear()
                Toast.makeText(this, "Success, new listing added.", Toast.LENGTH_SHORT).show()
                val profileIntent = Intent(this, ProfileActivity::class.java)
                profileIntent.putExtra(EXTRA_USERNAME, userLoggedIn?.username)
                startActivity(profileIntent)

                // Remove from back stack
                finish()
            }
        }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_PHOTO_CODE){
            photoUri = data?.data
            Log.i(TAG, "photoUri $photoUri")
            singleListingImageView.setImageURI((photoUri))

        } else {
            Toast.makeText(this, "Selection of image has been canceled", Toast.LENGTH_SHORT).show()
        }
    }
}
