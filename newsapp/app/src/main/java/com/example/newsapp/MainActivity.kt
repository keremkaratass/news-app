package com.example.newsapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = Firebase.database
        val myRef = database.getReference("haberler")
        val users = database.getReference("users")

        val text : TextView = findViewById(R.id.text)

        var getuser=object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Toast.makeText(applicationContext, "başarılı", Toast.LENGTH_SHORT).show()

                var data:MutableMap<String,String> = HashMap()
                data= snapshot.getValue() as MutableMap<String, String>
                text.setText(data.get("ad"))

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "hata", Toast.LENGTH_SHORT).show()
            }

        }

        var gethaber= object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {


                    var haber=snapshot.getValue(Haber::class.java)

                    //text.setText(haber!!.haber_desc)
            }

            override fun onCancelled(error: DatabaseError) {

            }


        }

        var haber1=myRef.child("https:shiftdeletenetamazon-web-tarayicisi").addValueEventListener(gethaber)
        //val userInfo: MutableMap<String, String> = HashMap()
        //userInfo.put("ad","fatih")
        //users.child("deneme").updateChildren(userInfo as Map<String, Any>)
        //users.child("deneme").addValueEventListener(getuser)
        var asd=5
    }
}