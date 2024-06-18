package com.example.sopermarket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.sopermarket.databinding.ActivityForgotpassBinding
import com.google.firebase.auth.FirebaseAuth

class forgotpass : AppCompatActivity() {
    private var binding:ActivityForgotpassBinding?=null
    private lateinit var auth:FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityForgotpassBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnback?.setOnClickListener {
           var intent= Intent(this ,loginpage2::class.java)
            startActivity(intent)
        }

        auth=FirebaseAuth.getInstance()

        binding?.btnforgotpass?.setOnClickListener {
        val spassword=binding?.edEmailforgotpass?.text.toString()
        auth.sendPasswordResetEmail(spassword)
            .addOnSuccessListener {
                Toast.makeText(this, "Please Check your Email!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }

        }


    }
}