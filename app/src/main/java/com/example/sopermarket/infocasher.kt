package com.example.sopermarket

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sopermarket.databinding.ActivityInfocasherBinding
import com.google.firebase.firestore.FirebaseFirestore

class infocasher : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var itemCodeEditText: EditText
    private lateinit var itemCodeEditText2: EditText
    private lateinit var itemNameTextView: TextView
    private lateinit var itemCostTextView: TextView
    private lateinit var itemNameTextView1: TextView
    private lateinit var itemCostTextView1: TextView
    private lateinit var totalTextView:TextView

    private var binding: ActivityInfocasherBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfocasherBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.tvnamecasher?.text = intent.getStringExtra(strings.namecasher)

        itemCodeEditText = findViewById(R.id.itemcode1)
        itemCodeEditText2 = findViewById(R.id.itemcode2)
        itemNameTextView = findViewById(R.id.tvnameitem1)
        itemCostTextView = findViewById(R.id.tvcostitem1)
        itemNameTextView1 = findViewById(R.id.tvnameitem2)
        itemCostTextView1 = findViewById(R.id.tvcostitem2)
        totalTextView = findViewById(R.id.tvtotalriew)

        val submitButton: Button = findViewById(R.id.butcode)
        submitButton.setOnClickListener {
            handleButtonClick()
            handleButtonClick2()
            calculateTotal()

            showCustomToast("Feedback sent to the manager")

             }
    }

    private fun handleButtonClick() {
        val userInput = itemCodeEditText.text.toString().trim()
        val userInput2 = itemCodeEditText2.text.toString().trim()
        var itemName = ""
        var itemCost = ""

        // Check user input against specified conditions
        when (userInput) {
            "10" -> {
                itemName = "Ice Lotus"
                itemCost = "15"
            }
            "20" -> {
                itemName = "Milk"
                itemCost = "20"
            }
            "30" -> {
                itemName = "Mulukhiya"
                itemCost = "30"
            }
            "40" -> {
                itemName = "sugar"
                itemCost = "50"
            }
            "50" -> {
                itemName = "veta pastirma flaver"
                itemCost = "38"
            }
            else -> {
                itemName = "Unknown Item"
                itemCost = "0"
            }
        }

        // Display the result in the corresponding TextViews
        itemNameTextView.text = itemName
        itemCostTextView.text = itemCost
    }

    private fun handleButtonClick2() {
        val userInput = itemCodeEditText.text.toString().trim()
        val userInput2 = itemCodeEditText2.text.toString().trim()
        var itemName = ""
        var itemCost = ""

        // Check user input against specified conditions
        when (userInput2) {
            "10" -> {
                itemName = "Ice Lotus"
                itemCost = "15"
            }
            "20" -> {
                itemName = "Milk"
                itemCost = "20"
            }
            "30" -> {
                itemName = "Mulukhiya"
                itemCost = "30"
            }
            "40" -> {
                itemName = "sugar"
                itemCost = "50"
            }
            "50" -> {
                itemName = "veta pastirma flaver"
                itemCost = "38"
            }
            else -> {
                itemName = "Unknown Item"
                itemCost = "0"
            }
        }

        // Display the result in the corresponding TextViews
        itemNameTextView1.text = itemName
        itemCostTextView1.text = itemCost
    }




    fun showCustomToast(message: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.customtiast, findViewById(R.id.toast))

        val text = layout.findViewById<TextView>(R.id.customtexttost)
        text.text = message

        val toast = Toast(applicationContext)
        toast.setGravity(Gravity.TOP or Gravity.CENTER, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }

    private fun calculateTotal() {
        // Get the item codes, names, and costs from EditTexts and TextViews
        val itemCode1 = itemCodeEditText.text.toString().trim()
        val itemName1 = itemNameTextView.text.toString()
        val itemCost1 = itemCostTextView.text.toString().toIntOrNull() ?: 0

        val itemCode2 = itemCodeEditText2.text.toString().trim()
        val itemName2 = itemNameTextView1.text.toString()
        val itemCost2 = itemCostTextView1.text.toString().toIntOrNull() ?: 0

        // Calculate the total cost
        val totalCost = itemCost1 + itemCost2

        // Set the total cost in totalTextView
        totalTextView.text = totalCost.toString()

        // Save all information to Firestore
        val salesData = hashMapOf(
            "item1" to hashMapOf(
                "code" to itemCode1,
                "name" to itemName1,
                "cost" to itemCost1
            ),
            "item2" to hashMapOf(
                "code" to itemCode2,
                "name" to itemName2,
                "cost" to itemCost2
            ),
            "totalCost" to totalCost
        )

        db.collection("sales").document("salesInfo").set(salesData)
            .addOnSuccessListener {
                // Data saved successfully
            }
            .addOnFailureListener { e ->
                // Handle errors
                Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show()
            }
    }

}
