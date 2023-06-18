package com.example.newsapp

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.HomeRecyclerViewAdapter2.MyViewHolder

internal class HomeRecyclerViewAdapter2(var news: ArrayList<Haber>, var context: Context) :
    RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.home_recyclerview_item, parent, false)
        Toast.makeText(context, "asdasd", Toast.LENGTH_SHORT).show()

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.haber_source.text = news[position].haber_source
        holder.haber_title.text = news[position].haber_title
        var drawable = holder.itemView.resources.getDrawable(R.drawable.baseline_home_24)
        holder.haber_image.setImageDrawable(drawable)
    }

    override fun getItemCount(): Int {
        return 0
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var haber_image: ImageView= itemView.findViewById(R.id.haber_image)
        var haber_title: TextView= itemView.findViewById(R.id.haber_title)
        var haber_source: TextView= itemView.findViewById(R.id.haber_source)



    }
}