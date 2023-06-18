package com.example.newsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
//Bu NewsAdapter sınıfı, RecyclerView bileşeniyle birlikte kullanılarak savedNewsList gibi bir veri kaynağını temsil eder ve
// bu verileri görsel olarak listelemek için kullanılır.
//Özel bir RecyclerView adapter sınıfını tanımlıyoruz.Bu adapter, Haber nesnelerinin listesini alır ve RecyclerView'de görüntülemek için kullanılır.
class NewsAdapter(private val newsList: List<Haber>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    private var itemClickListener: ItemClickListener? = null

    // ItemClickListener'ı ayarlamak için bir metod ekle
    fun setItemClickListener(listener: ItemClickListener) {
        this.itemClickListener = listener
    }

    //RecyclerView'de her bir haber öğesi için kullanılan görünüm öğelerini tutar. Bu örnekte, titleTextView ve sourceTextView adında iki TextView öğesi tanımlanır.
    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val sourceTextView: TextView = itemView.findViewById(R.id.sourceTextView)

        init {
            itemView.setOnClickListener {
                // Tıklanan öğenin pozisyonunu al
                val position = adapterPosition
                // ItemClickListener'ı çağır ve tıklanan öğenin pozisyonunu geç
                itemClickListener?.onItemClick(position)
            }
        }
    }

    //bu fonksiyon RecyclerView'nin yeni bir görünüm öğesi oluşturması gerektiğinde çağrılır.
    // Bu fonksiyonda, LayoutInflater sınıfı kullanılarak önceden tanımlanmış bir haber öğesi görünümü (item_news.xml)  (inflate) olur ve
    // bir NewsViewHolder örneği oluşturulur.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(itemView)
    }

    //bu fonksiyon her bir görünüm öğesi için verilerin bağlanmasını yönetir.
    // belirli bir pozisyondaki haber öğesini (newsList[position]) alır ve bu öğenin verilerini ilgili görünüm öğelerine atar.
    // Bu sayede, her bir haber öğesi, doğru başlık (titleTextView) ve kaynak (sourceTextView) bilgileriyle güncellenir.
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = newsList[position]

        holder.titleTextView.text = currentItem.haber_title
        holder.sourceTextView.text = currentItem.haber_source

        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }
}
