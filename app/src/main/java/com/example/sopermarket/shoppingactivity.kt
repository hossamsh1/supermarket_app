package com.example.sopermarket

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.sopermarket.databinding.ActivityShoppingactivityBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class shoppingactivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    val dp=FirebaseFirestore.getInstance()


    // Initialize GoogleSignInOptions
    private lateinit var googleSignInOptions: GoogleSignInOptions

    // choise user image account
    private var userimage: Uri? = null
    private var userImageUrl:Uri?=null

    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        userimage = it
        binding.userpecture.setImageURI(userimage)
        // Call UploadImage() here after selecting the image
        UploadImage()
    }


    val binding by lazy {
        ActivityShoppingactivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val usersCollection=dp.collection("users")
        val usercurrentref=usersCollection.document(FirebaseAuth.getInstance()
            .currentUser!!.uid)
        usercurrentref.get()

        // get user name from document
        dp.collection("users").document(usercurrentref.toString()).get()
            .addOnSuccessListener {document->
                if(document!=null){
                    binding.tvnameuser.text=document.get("name").toString()
                }

            }
            .addOnFailureListener {exibtion->
                // error handling
            }






        // Configure GoogleSignInOptions
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        /////
        val account = GoogleSignIn.getLastSignedInAccount(this)

        // Display user's name and image if the user is signed in
        account?.let { googleAccount ->
            binding.apply {
                // Set user's name
                tvnameuser.text = googleAccount.displayName

                // Set user's profile image
                googleAccount.photoUrl?.let { photoUrl ->
                    Glide.with(this@shoppingactivity)
                        .load(photoUrl)
                        .circleCrop()
                        .into(userpecture)
                }
            }
////
            val navController = findNavController(R.id.shoppingHostfragment)
            binding.buttomNavigation2.setupWithNavController(navController)



            val auth = FirebaseAuth.getInstance()

            binding.butLogout.setOnClickListener {
                auth.signOut()            // Sign out from Google account
                googleSignInClient.signOut().addOnCompleteListener {
                    intent = Intent(this, loginpage2::class.java)
                    startActivity(intent)
                    // On complete listener
                    // You can redirect the user to the login screen or perform any other action
                }

            }


        }
        binding.buttonback.setOnClickListener {
            intent = Intent(this, loginpage2::class.java)
            startActivity(intent)
        }

        binding.userpecture.setOnClickListener {
            selectImage.launch("Image/*")
        }

    }


    private fun UploadImage() {
        userimage?.let { imageUri ->
            val storeReference = FirebaseStorage.getInstance().getReference("profile")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("profile.jpg")

            storeReference.putFile(imageUri)
                .addOnSuccessListener { uploadTask ->
                    // Image upload success
                    uploadTask.storage.downloadUrl.addOnSuccessListener { uri ->
                        // Image download URL retrieved successfully, store it
                        userImageUrl = uri
                        // Call function to store data with image URL
                        storeData(uri)
                    }.addOnFailureListener { exception ->
                        // Handle failure to get image download URL
                        Toast.makeText(this, "Failed to retrieve image URL: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle image upload failure
                    Toast.makeText(this, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun storeData(imageUrl: Uri?) {
        var simage = userImageUrl?:""
    }
}