package com.example.sopermarket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.sopermarket.databinding.ActivityLoginpageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.newFixedThreadPoolContext

class loginpage : AppCompatActivity() {
    private lateinit var binding: ActivityLoginpageBinding
    private lateinit var firebaseAuth:FirebaseAuth
    private lateinit var firebasestore:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginpageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebasestore= FirebaseFirestore.getInstance()
        firebaseAuth=FirebaseAuth.getInstance()


        binding?.tvtologinpage?.setOnClickListener {
            intent = Intent(this, loginpage2::class.java)
            startActivity(intent)
        }

        binding.btnsignup.setOnClickListener {

            var semail = binding.edEmailup.text.toString().trim()
            var spassword = binding.edpasswordup.text.toString().trim()
            var sconfigrationpass = binding.edconfermpass.text.toString().trim()
            var sname = binding.edName.text.toString().trim()

            // Check if all fields are filled
            if (semail.isNotEmpty() && spassword.isNotEmpty() && sconfigrationpass.isNotEmpty() && sname.isNotEmpty()) {
                binding.progressbarview.visibility = View.VISIBLE
                binding.tvWaitinbut.visibility = View.VISIBLE
                binding.tvLogininbut.visibility=View.GONE
            }else{
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if email is in valid format
            if (!Patterns.EMAIL_ADDRESS.matcher(semail).matches()) {
                showprogressbar()
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if password length is sufficient
            if (spassword.length < 6) {
                showprogressbar()
                Toast.makeText(this, "Password should contain at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if password matches confirmation password
            if (spassword != sconfigrationpass) {
                showprogressbar()
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create user in Firebase Authentication
            firebaseAuth.createUserWithEmailAndPassword(semail, spassword)
                .addOnCompleteListener { createUserTask ->
                    if (createUserTask.isSuccessful) {
                        showprogressbar()
                        // Get the user ID of the newly created user
                        val userId = createUserTask.result?.user?.uid

                        // Save user data in Firestore if user creation is successful
                        if (userId != null) {
                            val userMap = hashMapOf(
                                "name" to sname,
                                "email" to semail
                            )

                            firebasestore.collection("user").document(userId).set(userMap)
                                .addOnSuccessListener {
                                    showprogressbar()
                                    Toast.makeText(this, "User data stored successfully", Toast.LENGTH_SHORT).show()
                                    // Clear input fields after successful storage
                                    binding.edName.text.clear()
                                    binding.edEmailup.text.clear()
                                    binding.edpasswordup.text.clear()
                                    binding.edconfermpass.text.clear()

                                    intent=Intent(this,loginpage2::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    showprogressbar()
                                    Toast.makeText(this, "Failed to store user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            showprogressbar()
                            Toast.makeText(this, "Failed to get user ID", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        showprogressbar()
                        Toast.makeText(this, "Failed to create user: ${createUserTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
   // show progressbar gone
    private fun showprogressbar(){
        binding.progressbarview.visibility=View.GONE
        binding.tvWaitinbut.visibility=View.GONE
       binding.tvLogininbut.visibility=View.VISIBLE
    }
}



