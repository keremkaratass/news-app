package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.newsapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(),PopupMenu.OnMenuItemClickListener{
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        mainActivity = this
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.discover -> replaceFragment(DiscoverFragment())
                R.id.saved -> replaceFragment(SavedFragment())

                else->{

                }
            }
            true
        }

    }



    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        TODO("Not yet implemented")
    }

    companion object{

        lateinit var mainActivity:MainActivity
        fun replaceFragment(fragment:Fragment){
            val fragmentManager = mainActivity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frameLayout,fragment)
            fragmentTransaction.commit()
        }

        fun replaceActivity(activity:AppCompatActivity){
            val intent = Intent(mainActivity, activity::class.java)
            mainActivity.startActivity(intent)
            mainActivity.finish()
        }
    }
}