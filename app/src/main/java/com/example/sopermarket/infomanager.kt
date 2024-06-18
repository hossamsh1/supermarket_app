package com.example.sopermarket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.sopermarket.databinding.ActivityInfomanagerBinding
import com.google.firebase.firestore.FirebaseFirestore

class infomanager : AppCompatActivity() {
    private var binding: ActivityInfomanagerBinding?=null

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityInfomanagerBinding.inflate(layoutInflater)
        setContentView(binding?.root)

// Retrieve the sales information from Firestore
        db.collection("sales").document("salesInfo").get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val item1 = document["item1"] as HashMap<*, *>?
                    val item2 = document["item2"] as HashMap<*, *>?


                    val totalCost = document.getLong("totalCost")

                    // Update the corresponding TextViews in the second table
                    binding?.tvItemCode1?.text = item1?.get("code").toString()
                    binding?.tvItemName1?.text = item1?.get("name").toString()
                    binding?.tvItemCost1?.text = item1?.get("cost").toString()

                    binding?.tvItemCode2?.text = item2?.get("code").toString()
                    binding?.tvItemName2?.text = item2?.get("name").toString()
                    binding?.tvItemCost2?.text = item2?.get("cost").toString()

                   binding?.tvTotalCost?.text = totalCost.toString()

                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }

        val casherDocRef = db.collection("casher").document("casher info")

        casherDocRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val casherName = document.getString("name")

                    // Update the corresponding TextView with the casher name
                 binding?.tvnamecash?.text= casherName
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }
}