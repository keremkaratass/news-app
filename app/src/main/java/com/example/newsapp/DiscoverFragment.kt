package com.example.newsapp

import android.graphics.Color
import android.os.Bundle
import android.util.SparseBooleanArray
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DiscoverFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiscoverFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val db = Firebase.database
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    private lateinit var recyclerView1: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var adapter1: MyAdapter
    private lateinit var adapter2: MyAdapter
    private val selectedItems1: SparseBooleanArray = SparseBooleanArray()
    private val selectedItems2: SparseBooleanArray = SparseBooleanArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_discover, container, false)

        recyclerView1 = view.findViewById(R.id.recyclerView1)
        recyclerView1.layoutManager = LinearLayoutManager(context)
        recyclerView2 = view.findViewById(R.id.recyclerView2)
        recyclerView2.layoutManager = LinearLayoutManager(context)

        val data1 = mutableListOf("spor", "siyaset", "ekonomi", "yaşam", "bilim ve teknoloji", "gündem")
        val data2 = mutableListOf("CNN TÜRK", "Milliyet", "AHABER", "TRTHaber", "Sabah", "NTV", "Takvim", "Yeni Şafak", "Sözcü", "Habertürk", "Fotomaç", "Yeni Akit", "Cumhuriyet ","ShiftDelete", "Akşam", "Hürriyet", "DonanımHaber", "Mynet", "Star", "Ekonomim")

        adapter1 = MyAdapter(data1, selectedItems1) { position ->
            toggleSelection(selectedItems1, position)
            adapter1.notifyItemChanged(position)
        }

        adapter2 = MyAdapter(data2, selectedItems2) { position ->
            toggleSelection(selectedItems2, position)
            adapter2.notifyItemChanged(position)
        }

        recyclerView1.adapter = adapter1
        recyclerView2.adapter = adapter2

        var homeButton=view.findViewById<Button>(R.id.homeButton)
        homeButton.setOnClickListener{
            Toast.makeText(context, "Favorileriniz kaydedildi!", Toast.LENGTH_SHORT).show()
            goToHome(view)
        }
        return view
    }


    private fun toggleSelection(selectedItems: SparseBooleanArray, position: Int) {
        val isSelected = selectedItems.get(position, false)
        selectedItems.put(position, !isSelected)
    }

    fun goToHome(view : View) {

        var selectedPositions=getSelectedItems(selectedItems1)
        var selectedPositions2=getSelectedItems(selectedItems2)



        val selectedItemsText: ArrayList<String> = ArrayList() // Seçili itemlerin text değerlerini tutacak ArrayList
        val selectedItemsText2: ArrayList<String> = ArrayList()

        for (position in selectedPositions) {
            val viewHolder = recyclerView1.findViewHolderForAdapterPosition(position) as MyAdapter.MyViewHolder
            val itemText = viewHolder?.itemView?.findViewById<TextView>(R.id.discover_textView)?.text?.toString()
            if (itemText != null) {
                selectedItemsText.add(itemText)

            }
        }

        for (position in selectedPositions2) {

            val viewHolder = recyclerView2.findViewHolderForAdapterPosition(position) as MyAdapter.MyViewHolder
            val itemText = viewHolder?.itemView?.findViewById<TextView>(R.id.discover_textView)?.text?.toString()

            if (itemText != null) {
                selectedItemsText2.add(itemText)
            }
        }

// Seçili itemlerin text değerlerine sahip olan listede istediğiniz işlemleri yapabilirsiniz
// Örneğin, listeyi yazdırabilirsiniz


        db.getReference("users").child(currentUser!!.uid).child("fav_types").setValue(selectedItemsText)
        db.getReference("users").child(currentUser!!.uid).child("fav_sources").setValue(selectedItemsText2)


    }

    private fun getSelectedItems(selectedItems: SparseBooleanArray): ArrayList<Int> {
        val selectedPositions = ArrayList<Int>()

        for (i in 0 until selectedItems.size()) {
            val position = selectedItems.keyAt(i)
            if (selectedItems.valueAt(i)) {
                selectedPositions.add(position)
            }
        }

        return selectedPositions
    }



    class MyAdapter(
        private val data: List<String>,
        private val selectedItems: SparseBooleanArray,
        private val onItemClick: (Int) -> Unit
    ) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_layout, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val item = data[position]
            holder.bind(item, position)

            // Seçili öğenin arka plan rengini kontrol et

            val isSelected = selectedItems.get(position, false)
            if (isSelected) {
                holder.itemView.setBackgroundColor(Color.RED)

            } else {
                holder.itemView.setBackgroundColor(Color.WHITE)
            }
        }

        override fun getItemCount(): Int = data.size

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bind(item: String, position: Int) {
                itemView.setOnClickListener {
                    onItemClick.invoke(position)
                }

                val textView = itemView.findViewById<TextView>(R.id.discover_textView)
                textView.text = item
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DiscoverFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DiscoverFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}