package com.example.newsapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.newsapp.databinding.ActivityLoginBinding
import com.example.newsapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase




class LoginActivity : AppCompatActivity() {

    val db = Firebase.database
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        val currentUser = auth.currentUser

        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    fun signInClicked(view : View) {

        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        if (email == "" || password == "") {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_LONG).show()
        } else {
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@LoginActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }


    }

    fun signUpClicked(view: View) {

        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()


        if (email == "" || password == "") {
            Toast.makeText(this@LoginActivity, "Enter email and password", Toast.LENGTH_LONG).show()
        } else {
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                //success
                val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
                startActivity(intent)
                finish()
                // Create a new user with a first and last name


                //veri ekleme
                var db_users= db.getReference("users")
                val  user=User(email,password)
                db_users.child(auth.currentUser?.uid.toString()).setValue(user).addOnSuccessListener {
                    binding.emailText.text.clear()
                    binding.passwordText.text.clear()

                    Toast.makeText(this,"Successfully Saved ", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText(this,"Failed ", Toast.LENGTH_SHORT).show()
                }



            }.addOnFailureListener {
                Toast.makeText(this@LoginActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
            }


        }

    }

}