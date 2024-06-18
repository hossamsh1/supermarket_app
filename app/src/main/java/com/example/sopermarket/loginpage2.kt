package com.example.sopermarket

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.sopermarket.databinding.ActivityLoginpage2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import android.os.Handler
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider


class loginpage2 : AppCompatActivity() {
    private lateinit var binding: ActivityLoginpage2Binding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient:GoogleSignInClient

    private lateinit var handler: Handler
    private lateinit var imageSwitcherRunnable: Runnable


    private val fs= FirebaseFirestore.getInstance()



    // image switcher
    private val imageResources = listOf(
        R.drawable.splashscreenlogo,
        R.drawable.loginimage
    )

/////////


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(1000)
        installSplashScreen()
        binding= ActivityLoginpage2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // manager pg
        val passwordEditText: EditText = findViewById(R.id.editTextPassword)
        val submitButton: Button = findViewById(R.id.butpass)

        // login with google
val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestIdToken(getString(R.string.default_web_client_id))
    .requestEmail()
    .build()
        googleSignInClient=GoogleSignIn.getClient(this,gso)


        // initialise image switcher
     initImageSwitcher()

        firebaseAuth=FirebaseAuth.getInstance()

        binding?.tvforgetpassword?.setOnClickListener {

            intent=Intent(this,forgotpass::class.java)
            startActivity(intent)
        }


        binding?.tvtologinpage?.setOnClickListener {

            intent=Intent(this,loginpage::class.java)
            startActivity(intent)

        }

        binding?.btnlogin?.setOnClickListener {

            var email = binding.edEmail.text.toString()
            var password = binding.edpassword.text.toString()

            // Check if email is "manager" or "casher" for special handling
            if (email == "manager" && password == "manager") {
                // Special handling for manager login
                val cardNamemanager = findViewById<CardView>(R.id.cardpassmanager)
                cardNamemanager.visibility=View.VISIBLE
                val anim = AnimationUtils.loadAnimation(this, R.anim.slide_down)
                cardNamemanager.startAnimation(anim)

            } else if (email == "casher" && password == "casher") {
                // Special handling for casher login
                val cardNameCash = findViewById<CardView>(R.id.cardnamecash)
                cardNameCash.visibility=View.VISIBLE
                val anim = AnimationUtils.loadAnimation(this, R.anim.slide_up)
                cardNameCash.startAnimation(anim)

            } else {
                // Normal email/password authentication
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    binding.progressbarview.visibility=View.VISIBLE
                    binding.tvWaitinbut.visibility=View.VISIBLE
                    binding.tvLogininbut.visibility=View.GONE

                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, shoppingactivity::class.java)
                            startActivity(intent)

                        } else {
                            // Show Firebase error only if the email is not "casher"
                            if (!email.equals("casher", ignoreCase = true)) {
                                showToast(it.exception.toString())
                                showprogressbarlogin()
                                binding.tvLogininbut.visibility=View.VISIBLE
                            }
                        }
                    }
                } else {
                    showToast("Empty Fields Are Not Allowed")
                    showprogressbarlogin()
                    binding.tvLogininbut.visibility=View.VISIBLE
                }

                // Check for invalid email format
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                    !email.equals("casher", ignoreCase = true) &&
                    !email.equals("manager", ignoreCase = true) &&
                    email.isNotEmpty()) {
                    showToast("Wrong email format")
                    showprogressbarlogin()
                    binding.tvLogininbut.visibility=View.VISIBLE

                }

                // Check for password length
                if (password.length < 6 && password.isNotEmpty()) {
                    showToast("Password should contain at least 6 characters")
                    showprogressbarlogin()
                    binding.tvLogininbut.visibility=View.VISIBLE
                }
            }
        }




        // cashier page
        binding?.butcode?.setOnClickListener {

            if (binding?.editTextcode?.text.isNullOrEmpty()) {
                Toast.makeText(this, "pleas enter name", Toast.LENGTH_SHORT).show()


            } else {
                var intent = Intent(this, infocasher::class.java)
                intent.putExtra(strings.namecasher, binding?.editTextcode?.text.toString())
                startActivity(intent)

            }

            // store name casher and send to manager
            var sname=binding?.editTextcode?.text?.toString()?.trim()

            val savename= hashMapOf("name" to sname)

            fs.collection("casher").document("casher info").set(savename)
                .addOnSuccessListener { Toast.makeText(this, "Your name is saved", Toast.LENGTH_SHORT).show() }
                .addOnFailureListener {  }
        }

       // manager page
        submitButton.setOnClickListener {
            binding.progressbarview.visibility=View.VISIBLE
            val enteredPassword = passwordEditText.text.toString()

            if (isValidPasswordmanager(enteredPassword)) {
                // Password is valid, you can perform your desired action here
                var intent= Intent(this,infomanager::class.java)
                startActivity(intent)
                showprogressbarlogin()

            } else {
                showToast("Invalid password")
            }
        }


        binding.butback.setOnClickListener{
           val cardNameCash = findViewById<CardView>(R.id.cardnamecash)
            cardNameCash.visibility=View.INVISIBLE
            val anim = AnimationUtils.loadAnimation(this, R.anim.slide_unvisable)

            cardNameCash.startAnimation(anim)

        }

        binding.butback2.setOnClickListener{

           val cardNamemanager = findViewById<CardView>(R.id.cardpassmanager)
            cardNamemanager.visibility=View.INVISIBLE
            val animation = animation(binding.cardpassmanager) // Pass the target view to animate
            animation.duration = 1000 // Set the duration in milliseconds
            binding.cardpassmanager.startAnimation(animation) // Start the animation
        }

        binding?.butlogingoogle?.setOnClickListener{
            binding.progressbarview.visibility=View.VISIBLE
            binding.tvWaitinbut.visibility=View.VISIBLE
            binding.tvLogininbut.visibility=View.GONE
            signInWithgoogle()

        }
    }


    private fun isValidPasswordmanager(password: String): Boolean {
        // Add your password validation logic here
        val numericRegex = "123456".toRegex()
        val minimumLength = 6

        return password.matches(numericRegex) && password.length >= minimumLength
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

// image swicher code
    private fun initImageSwitcher() {
        val switcherImageView1: ImageView = findViewById(R.id.imageswitcher1)
        val switcherImageView2: ImageView = findViewById(R.id.imageswitcher2)

        val imageResources = listOf(
            R.drawable.splashscreenlogo,
            R.drawable.loginimage
        )
        var currentIndex = 0

        handler = Handler()
        imageSwitcherRunnable = Runnable {
            // Change the image resource of the image views
            switcherImageView1.setImageResource(imageResources[currentIndex])
            switcherImageView2.setImageResource(imageResources[currentIndex])

            currentIndex = (currentIndex + 1) % imageResources.size

            handler.postDelayed(imageSwitcherRunnable, 3000)
        }

        // Start the image switching
        handler.post(imageSwitcherRunnable)
    }


    private fun signInWithgoogle() {
        val signInIntent=googleSignInClient.signInIntent
        launcher.launch(signInIntent)

    }

    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result->
        if (result.resultCode == Activity.RESULT_OK){
            val task=GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            showToast("Sign-in failed. Please try again later.")
        }
    }
    // signin with google
    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                // Start shopping activity
                startActivity( Intent(this, shoppingactivity::class.java))
                finish()
                showprogressbarlogin()

            } else {
                Log.e("Authentication", "signInWithCredential:failure", authTask.exception)
                showToast("Authentication failed. Please try again later.")
                showprogressbarlogin()
            }
        }
    }

// view progressbar loading
    private fun showprogressbarlogin(){
        binding.progressbarview.visibility=View.GONE
        binding.tvWaitinbut.visibility=View.GONE

    }


    override fun onDestroy() {
        super.onDestroy()
        // Remove any remaining callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
    }



}


