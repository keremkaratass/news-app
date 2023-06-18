package com.example.newsapp

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.newsapp.MainActivity.Companion.replaceFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.Serializable

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WebviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WebviewFragment : Fragment(), Serializable {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var db = Firebase.database
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view =inflater.inflate(R.layout.fragment_webview, container, false)

        auth =Firebase.auth

        val haber:Haber = arguments?.getSerializable("haber") as Haber

        var webView = view.findViewById<WebView>(R.id.webView)

        webView.apply {
            loadUrl(haber.haber_link!!)
            settings.javaScriptEnabled= true
        }
        val webSettings: WebSettings = webView.settings

// Cache'i etkinleştir

        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.userAgentString = "custom-user-agent"

        class CustomWebViewClient : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                // Sayfa yüklemesi tamamlandığında yapılacak işlemler
                view?.settings?.loadsImagesAutomatically = true
            }
        }

// WebView ayarları

// Önizleme modunu etkinleştir
        webSettings.loadsImagesAutomatically = false

// WebViewClient'i ayarla
        webView.webViewClient = CustomWebViewClient()

        var backButton = view.findViewById<ImageButton>(R.id.back_button)

        backButton.setOnClickListener {
            var prev_fragment = arguments?.getSerializable("prev_fragment") as Fragment

            replaceFragment(prev_fragment)
        }

        var likeButton = view.findViewById<ImageButton>(R.id.like_button)
        var saveButton = view.findViewById<ImageButton>(R.id.save_button)


        likeButton.setOnClickListener {
            onLikeButtonClicked(view,haber.haber_name!!,likeButton)
        }

        saveButton.setOnClickListener {
            onSaveButtonClicked(view,haber.haber_name!!,saveButton)
        }

        likedsavedListener(view,haber.haber_name!!,likeButton,saveButton)


        // Inflate the layout for this fragment
        return view
    }



    // Kullanıcının "like" butonuna tıklandığında çalışacak olan fonksiyon
    fun onLikeButtonClicked(view: View,haberKey: String,likeButton: ImageButton) {
        val userId = auth.currentUser?.uid.toString() // Kullanıcının ID'si, burayı kullanıcıya özgü bir değerle güncelleyin

        // Kullanıcının verilerini tuttuğunuz düğüme ulaşın
        val usersRef = db.getReference("users").child(userId)

        // Kullanıcının mevcut "liked_news" dizisini alın
        usersRef.child("liked_news").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val likedNews = mutableListOf<String>()

                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val likedNewsId = childSnapshot.getValue(String::class.java)
                        likedNewsId?.let { likedNews.add(it) }
                    }
                }

                var newDrawable : Drawable? = ContextCompat.getDrawable(view.context, R.drawable.baseline_favorite_border_24)
                // İlgili haberin key değeri zaten var mı diye kontrol edin
                if (likedNews.contains(haberKey)) {
                    likedNews.remove(haberKey) // Key değeri varsa diziden kaldırın
                    // Değiştirmek istediğiniz drawable'ı alın
                    newDrawable = ContextCompat.getDrawable(view.context, R.drawable.baseline_favorite_border_24)

                    // ImageButton'da drawable'ı değiştirin

                } else {
                    likedNews.add(haberKey) // Key değeri yoksa dizie ekleyin
                    // Değiştirmek istediğiniz drawable'ı alın
                    newDrawable = ContextCompat.getDrawable(view.context, R.drawable.baseline_favorite_24)

                }

                likeButton.setImageDrawable(newDrawable)

                // Güncellenmiş "liked_news" dizisini Firebase Realtime Database'e kaydedin
                usersRef.child("liked_news").setValue(likedNews)
            }

            override fun onCancelled(error: DatabaseError) {
                // Hata durumunda yapılacaklar
            }
        })
    }


    // Kullanıcının "like" butonuna tıklandığında çalışacak olan fonksiyon
    fun onSaveButtonClicked(view: View, haberKey: String, saveButton: ImageButton) {
        val userId = auth.currentUser?.uid.toString() // Kullanıcının ID'si, burayı kullanıcıya özgü bir değerle güncelleyin

        // Kullanıcının verilerini tuttuğunuz düğüme ulaşın
        val usersRef = db.getReference("users").child(userId)

        // Kullanıcının mevcut "liked_news" dizisini alın
        usersRef.child("saved_news").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val savedNews = mutableListOf<String>()

                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val savedNewsId = childSnapshot.getValue(String::class.java)
                        savedNewsId?.let { savedNews.add(it) }
                    }
                }

                var newDrawable : Drawable? = ContextCompat.getDrawable(view.context, R.drawable.baseline_download_24)
                // İlgili haberin key değeri zaten var mı diye kontrol edin
                if (savedNews.contains(haberKey)) {
                    savedNews.remove(haberKey) // Key değeri varsa diziden kaldırın
                    // Değiştirmek istediğiniz drawable'ı alın
                    newDrawable = ContextCompat.getDrawable(view.context, R.drawable.baseline_download_24)

                    // ImageButton'da drawable'ı değiştirin

                } else {
                    savedNews.add(haberKey) // Key değeri yoksa dizie ekleyin
                    // Değiştirmek istediğiniz drawable'ı alın
                    newDrawable = ContextCompat.getDrawable(view.context, R.drawable.baseline_file_download_done_24)

                }

                saveButton.setImageDrawable(newDrawable)

                // Güncellenmiş "liked_news" dizisini Firebase Realtime Database'e kaydedin
                usersRef.child("saved_news").setValue(savedNews)
            }

            override fun onCancelled(error: DatabaseError) {
                // Hata durumunda yapılacaklar
            }
        })
    }

    fun likedsavedListener(view: View,haberKey: String,likeButton: ImageButton,saveButton: ImageButton){

        val userId = auth.currentUser?.uid.toString() // Kullanıcının ID'si, burayı kullanıcıya özgü bir değerle güncelleyin



        // Kullanıcının verilerini tuttuğunuz düğüme ulaşın
        val usersRef = db.getReference("users").child(userId)

        // Kullanıcının mevcut "liked_news" dizisini alın
        usersRef.child("liked_news").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val likedNews = mutableListOf<String>()

                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val likedNewsId = childSnapshot.getValue(String::class.java)
                        likedNewsId?.let { likedNews.add(it) }
                    }
                }

                var newDrawable : Drawable? = ContextCompat.getDrawable(view.context, R.drawable.baseline_favorite_border_24)
                // İlgili haberin key değeri zaten var mı diye kontrol edin
                if (likedNews.contains(haberKey)) {

                    // Değiştirmek istediğiniz drawable'ı alın
                    newDrawable = ContextCompat.getDrawable(view.context, R.drawable.baseline_favorite_24)

                    // ImageButton'da drawable'ı değiştirin

                } else {

                    // Değiştirmek istediğiniz drawable'ı alın
                    newDrawable = ContextCompat.getDrawable(view.context, R.drawable.baseline_favorite_border_24)

                }

                likeButton.setImageDrawable(newDrawable)


            }

            override fun onCancelled(error: DatabaseError) {
                // Hata durumunda yapılacaklar
            }
        })

        usersRef.child("saved_news").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val savedNews = mutableListOf<String>()

                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val savedNewsId = childSnapshot.getValue(String::class.java)
                        savedNewsId?.let { savedNews.add(it) }
                    }
                }

                var newDrawable : Drawable? = ContextCompat.getDrawable(view.context, R.drawable.baseline_download_24)
                // İlgili haberin key değeri zaten var mı diye kontrol edin
                if (savedNews.contains(haberKey)) {

                    // Değiştirmek istediğiniz drawable'ı alın
                    newDrawable = ContextCompat.getDrawable(view.context, R.drawable.baseline_file_download_done_24)

                    // ImageButton'da drawable'ı değiştirin

                } else {

                    // Değiştirmek istediğiniz drawable'ı alın
                    newDrawable = ContextCompat.getDrawable(view.context, R.drawable.baseline_download_24)

                }

                saveButton.setImageDrawable(newDrawable)

                // Güncellenmiş "liked_news" dizisini Firebase Realtime Database'e kaydedin

            }

            override fun onCancelled(error: DatabaseError) {
                // Hata durumunda yapılacaklar
            }
        })
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WebviewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun <T>newInstance(haber: Haber,prev_fragment: T): WebviewFragment {

            val fragment = WebviewFragment()
            val args = Bundle()
            args.putSerializable("haber", haber)



            when (prev_fragment) {
                is HomeFragment -> {
                    // HomeFragment ile ilgili işlemler
                    args.putSerializable("prev_fragment",prev_fragment)
                }
                is SavedFragment -> {
                    // SavedFragment ile ilgili işlemler
                    args.putSerializable("prev_fragment",prev_fragment)
                }
                else -> {
                    // Diğer durumlar için işlemler
                    println("Fragment tipiniz yanlış")
                }
            }

            fragment.arguments = args
            return fragment
        }
    }
}