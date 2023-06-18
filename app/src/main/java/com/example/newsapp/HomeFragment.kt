package com.example.newsapp

import CustomAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.MainActivity.Companion.replaceActivity
import com.example.newsapp.MainActivity.Companion.replaceFragment
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.tasks.Tasks.await
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment(),RecyclerViewInterface,java.io.Serializable {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var this_fragment: HomeFragment = this

    val database = Firebase.database
    val myRef = database.getReference("haberler")
    lateinit var user:User

    var news:ArrayList<Haber> = ArrayList()
    var lastNewsDate=""
    var typeOrNewspaper = "type"
    var type = ""
    var newspaper = ""
    lateinit var recyclerViewAdapter:CustomAdapter
    var countAddedNews =0
    var searchText = ""
    var scoreTypes = HashMap<String,Int>()

    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        auth = Firebase.auth

        database.getReference("users").child(auth.currentUser!!.uid).get().addOnSuccessListener {
            user = it.getValue(User::class.java)!!

            getLikeWeights(user.liked_news)

        }

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment







        var view = inflater.inflate(R.layout.fragment_home, container, false)



        val navigationView: NavigationView = view.findViewById(R.id.navigation_view)
        val drawerLayout: DrawerLayout = view.findViewById(R.id.drawer_layout)
        var home_search_button = view.findViewById<ImageButton>(R.id.home_search_button)
        var home_search_text = view.findViewById<EditText>(R.id.home_search_text)



        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Seçilen menü öğesinin ID'sini al
            val itemId = menuItem.itemId

            home_search_text.setText("")
            searchText=""
            lastNewsDate=""
            news.clear()
            recyclerViewAdapter.notifyDataSetChanged()



            if(menuItem.groupId==R.id.group_type) {
                typeOrNewspaper = "type"

            }else if (menuItem.groupId==R.id.group_newspaper){
                typeOrNewspaper="newspaper"
                newspaper = menuItem.title.toString()

            }

            // İlgili işlemleri yapmak için switch case veya if-else kullanabilirsin
            when (itemId) {

                R.id.menu_logout -> {
                    // Logout öğesi seçildiğinde yapılacak işlemler
                    auth.signOut()
                    replaceActivity(LoginActivity())
                }
                R.id.menu_home -> {
                    // Home öğesi seçildiğinde yapılacak işlemler
                    type =""
                }
                R.id.menu_siyaset -> {
                    // Siyaset öğesi seçildiğinde yapılacak işlemler
                    type ="siyaset"
                }
                R.id.menu_ekonomi -> {
                    // Ekonomi öğesi seçildiğinde yapılacak işlemler
                    type ="ekonomi"
                }
                R.id.menu_spor -> {
                    // Spor öğesi seçildiğinde yapılacak işlemler
                    type ="spor"

                }
                R.id.menu_bilim_teknoloji -> {
                    // Bilim ve Teknoloji öğesi seçildiğinde yapılacak işlemler
                    type ="bilim ve teknoloji"
                }
                R.id.menu_yasam -> {
                    // Yaşam öğesi seçildiğinde yapılacak işlemler
                    type ="yaşam"
                }
                R.id.menu_gundem -> {
                    // Gündem öğesi seçildiğinde yapılacak işlemler
                    type ="gündem"
                }

            }

            // Seçili öğeyi işaretleyerek görsel geri bildirimi sağla
            menuItem.isChecked = true

            getLatestNews()
            countAddedNews=0

            // Drawer'ı kapat
            drawerLayout.closeDrawer(GravityCompat.START)

            true
        }


        var options_button = view.findViewById<ImageButton>(R.id.home_options_button)

        options_button.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            /*
            var popupMenu = PopupMenu(it.context, it)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                Toast.makeText(context, "sa", Toast.LENGTH_SHORT).show()
                true

            })
            popupMenu.inflate(R.menu.options_menu)
            popupMenu.show()

             */
        }


        //Picasso.get().load("https://i.imgur.com/DvpvklR.png").placeholder(R.drawable.baseline_home_24).into(imageView);
        //var news:ArrayList<Haber> = getLatestNews()


        var recyclerView = view.findViewById<RecyclerView>(R.id.home_recyclerview)
        /*var recyclerViewAdapter2 = HomeRecyclerViewAdapter2(news,this.requireContext())
        recyclerView.adapter=recyclerViewAdapter2
        recyclerView.layoutManager=LinearLayoutManager(context)
        recyclerViewAdapter2.notifyDataSetChanged()*/
        recyclerViewAdapter = CustomAdapter(news,object:RecyclerViewInterface{
            override fun onItemClick(position: Int) {


                val haber = news[position]
                val fragment = WebviewFragment.newInstance(haber,this_fragment)

                replaceFragment(fragment)
            }
        })
        recyclerView.adapter=recyclerViewAdapter
        recyclerView.layoutManager=LinearLayoutManager(context)

        //getLatestNews()
        //countAddedNews=0

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    getLatestNews()
                    countAddedNews=0
                }
            }
        })




        home_search_button.setOnClickListener {
            searchText = home_search_text.text.toString()
            searchText = searchText.replace(" ","").lowercase()
            lastNewsDate=""
            news.clear()
            recyclerViewAdapter.notifyDataSetChanged()
            getLatestNews()
            countAddedNews=0
        }




        return view
    }

    fun getLatestNews() {

        var tempnews = ArrayList<Haber>()


        val query = if (lastNewsDate.equals("")) {
            myRef.orderByChild("haber_date")
                .limitToLast(1000)
        } else {
            myRef.orderByChild("haber_date")
                .endAt(lastNewsDate)
                .limitToLast(1000)
        }

        query.get().addOnSuccessListener {
            var hashMap: HashMap<String,HashMap<String,String>> = it.getValue() as HashMap<String, HashMap<String, String>>
            var newsOldSize = news.size

            for(hash in hashMap){
                var new = Haber(hash.value["haber_date"], hash.value["haber_desc"],hash.value["haber_img"],hash.value["haber_title"],hash.value["haber_source"],hash.value["haber_link"],hash.value["haber_type"],hash.key)
                var newText = new.haber_title + new.haber_desc
                newText = newText.lowercase()
                newText= newText.replace(" ","")

                if(!searchText.equals("")){
                    if(news.find { it.haber_name==new.haber_name }==null && newText.contains(searchText)){
                        tempnews.add(new)
                        countAddedNews++
                    }
                }else{
                    if(news.find { it.haber_name==new.haber_name }==null ){
                        if(typeOrNewspaper.equals("type")) {
                            if(type.equals("")){
                                tempnews.add(new)
                                countAddedNews++
                            }else{
                                if (new.haber_type.equals(type)) {
                                    tempnews.add(new)
                                    countAddedNews++
                                }
                            }

                        }else if(typeOrNewspaper.equals("newspaper")){
                            if (new.haber_source.equals(newspaper)) {
                                tempnews.add(new)
                                countAddedNews++
                            }
                        }

                    }
                }


            }

            val randomizedScores = tempnews.map { Pair(it, randomizeScores(getNewsScore(it))) }
                .shuffled()
                .sortedByDescending { it.second }
                .map { it.first }

            /*
            tempnews.sortByDescending { randomizeScores(getNewsScore(it)) }
*/
            for(temp in randomizedScores){
                println(temp.haber_title+"---"+getNewsScore(temp))
            }
            news.addAll(randomizedScores)

            val lastLoadedHaber = it.children.firstOrNull()?.getValue(Haber::class.java)
            lastNewsDate = lastLoadedHaber?.haber_date ?: lastNewsDate

            var newsNewSize = news.size
            recyclerViewAdapter.notifyItemRangeInserted(newsOldSize,newsNewSize-newsOldSize)
            println(countAddedNews)
            if(countAddedNews<20){
                getLatestNews()

            }
        }

    }

    fun getNewsScore(new :Haber):Int{
        var score=0

        //haberin kategorisi kullanıcının favori kategorilerinden biri ise haber ekstra 3 puan kazanır
        if(user.fav_types.contains(new.haber_type)){
                score+=3
        }

        //haberin kaynağı kullanıcının favori kaynaklarından biri ise haber ekstra 3 puan kazanır
        if(user.fav_sources.contains(new.haber_source)){
                score+=3
        }

        //scoreTypes değişkenindeki ağırlıkları kullanarak puan eklemelerini yapıyoruz
        score += scoreTypes[new.haber_type]!!

        //println(new.haber_title+"---"+score)

        return score

    }

    fun getLikeWeights(liked_news : List<String>) {


        val tasks = mutableListOf<Task<DataSnapshot>>()

        for (newKey in liked_news) {
            val task = database.getReference("haberler").child(newKey).get()
            tasks.add(task)
        }

        Tasks.whenAllComplete(tasks)
            .addOnSuccessListener {
                var countTypes = HashMap<String,Int>()
                countTypes.put("spor",0)
                countTypes.put("siyaset",0)
                countTypes.put("ekonomi",0)
                countTypes.put("yaşam",0)
                countTypes.put("bilim ve teknoloji",0)
                countTypes.put("gündem",0)

                for (task in tasks) {
                    if (task.isSuccessful) {
                        val snapshot = task.result
                        val new = snapshot.getValue(Haber::class.java)!!
                        if (!new.haber_type.isNullOrEmpty()) {
                            countTypes[new.haber_type!!] = countTypes.getOrDefault(new.haber_type!!, 0) + 1
                        }
                    }
                }

                var percentTypes = HashMap<String,Float>()
                percentTypes.put("spor",0f)
                percentTypes.put("siyaset",0f)
                percentTypes.put("ekonomi",0f)
                percentTypes.put("yaşam",0f)
                percentTypes.put("bilim ve teknoloji",0f)
                percentTypes.put("gündem",0f)

                var count = 0

                for (types in countTypes) {
                    count += types.value
                }

                //var scoreTypes = HashMap<String,Int>()
                scoreTypes.put("",0)
                scoreTypes.put("spor",0)
                scoreTypes.put("siyaset",0)
                scoreTypes.put("ekonomi",0)
                scoreTypes.put("yaşam",0)
                scoreTypes.put("bilim ve teknoloji",0)
                scoreTypes.put("gündem",0)

                for (types in countTypes) {
                    percentTypes[types.key] = types.value*100f/count.toFloat()
                    //println(types.key+"-"+percentTypes[types.key])
                    //yüzdelik değerleri skorlara dönüştürme
                    if(percentTypes[types.key]!!>0f && percentTypes[types.key]!!<20f){
                        scoreTypes[types.key]=1
                    }
                    else if(percentTypes[types.key]!!>=20f && percentTypes[types.key]!!<40f){
                        scoreTypes[types.key]=2
                    }
                    else if(percentTypes[types.key]!!>=40f){
                        scoreTypes[types.key]=3
                    }

                }
                getLatestNews()
                countAddedNews=0
            }




    }




    fun randomizeScores(score:Int):Int{
        val random = Random.Default
        val randomScore = (random.nextFloat()*score).toInt()

        return randomScore
    }





    companion object {


        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemClick(position: Int) {

    }
}