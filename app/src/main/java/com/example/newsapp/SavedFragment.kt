package com.example.newsapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SavedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SavedFragment : Fragment(),java.io.Serializable {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var this_fragment=this

    var db = Firebase.database
    var auth = Firebase.auth

    private lateinit var newsRecyclerView: RecyclerView //Recycler bileşenini temsil eder.
    private lateinit var newsAdapter: NewsAdapter// Haberlerin listelendiği Adapteri temsil eder.

    private var savedNewsList = mutableListOf<Haber>() // Kaydedilen haberleri tutacak liste


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
        val view = inflater.inflate(R.layout.fragment_saved, container, false)

        // View bileşenlerine erişim sağla
        val notificationButton = view.findViewById<Button>(R.id.notificationButton)
        val savedButton = view.findViewById<Button>(R.id.savedButton)
        newsRecyclerView = view.findViewById(R.id.newsRecyclerView)


        notificationButton.setOnClickListener {
            savedNewsList.clear()


            // Adapter güncellemesini tetikle
            newsAdapter.notifyDataSetChanged()

            // TODO: Bildirimleri göster
            Toast.makeText(requireContext(), "Bildirimler", Toast.LENGTH_SHORT).show()
        }


        savedButton.setOnClickListener {
            savedNewsList.clear()

            val userId = auth.currentUser?.uid.toString() // Kullanıcının UID'sini belirtin
            var savedNewsKeys = listOf<String>()
            var haberler = ArrayList<Haber>()

            val userReference = db.getReference("users").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val user = dataSnapshot.getValue(User::class.java)
                        if(user?.saved_news != null){
                            savedNewsKeys = user?.saved_news!!

                        }
                        // saved_news array'ini alın
                        // İşlemlerinizi burada yapabilirsiniz
                        for (s in savedNewsKeys){

                            db.getReference("haberler").child(s).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(newsSnapshot: DataSnapshot) {
                                    if (newsSnapshot.exists()) {
                                        var new = newsSnapshot.getValue(Haber::class.java)
                                        new!!.haber_name= newsSnapshot.key
                                        savedNewsList.add(new!!)
                                        newsAdapter.notifyDataSetChanged()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // Hata durumunda yapılacaklar
                                }
                            })
                        }




                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Hata durumunda yapılacaklar
                }
            })

            newsAdapter.notifyDataSetChanged()
            // TODO: Kaydedilen haberleri göster
            Toast.makeText(requireContext(), "Kaydedilenler", Toast.LENGTH_SHORT).show()
        }

        // RecyclerView için layoutManager ve adapter tanımla
        newsRecyclerView.layoutManager = LinearLayoutManager(requireContext()) //RecyclerView'in kullanacağı bir düzen yöneticisi (layout manager) atıyoruz.
        newsAdapter = NewsAdapter(savedNewsList)//RecyclerView için bir adapter oluşturuyoruz ve savedNewsList listesini adapter'a veriyoruz.

        newsAdapter.setItemClickListener(object : NewsAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {
                // Tıklanan öğeyle ilgili işlemleri gerçekleştir
                val haber=savedNewsList[position]
                val fragment = WebviewFragment.newInstance(haber,this_fragment)

                MainActivity.replaceFragment(fragment)
            }
        })
        newsRecyclerView.adapter = newsAdapter//RecyclerView'e adapter atıyoruz.



        return view
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SavedFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SavedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}