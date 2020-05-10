package com.example.ccsulist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

private const val TAG = "LoginActivity"
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val auth = FirebaseAuth.getInstance()  // Create authentication instance

        // Return user to listing, if logged in thru firebase
        if (auth.currentUser != null){
            goListingsActivity()
        }

        loginBtn.setOnClickListener {
            // Prevent user from creating multiple instances
            loginBtn.isEnabled = false
            val email = editTextEmail.text.toString()
            val password = editPassword.text.toString()

            if (email.isBlank() || password.isBlank()){
            Toast.makeText(this, "Email/Password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



            // Async call to sign in user
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{ task ->
                loginBtn.isEnabled = true
                if (task.isSuccessful){
                    Toast.makeText(this,"Success!", Toast.LENGTH_SHORT).show()
                    goListingsActivity()
                } else {
                    Log.e(TAG,"signInWithEmail failed", task.exception)
                    Toast.makeText(this,"Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun goListingsActivity(){
        Log.i(TAG,"goListingsActivity")
        val intent = Intent(this,ListingsActivity::class.java)
        startActivity(intent)
        // Finish current activity, no longer part of back stack
        finish()
    }

}
