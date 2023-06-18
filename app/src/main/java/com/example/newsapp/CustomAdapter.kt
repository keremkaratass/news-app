import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.Haber
import com.example.newsapp.R
import com.example.newsapp.RecyclerViewInterface
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class CustomAdapter(private val mList: List<Haber>,private val listener: RecyclerViewInterface) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_recyclerview_item, parent, false)

        return ViewHolder(view,listener)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class


        // sets the text to the textview from our itemHolder class
        holder.haber_source.text = ItemsViewModel.haber_source
        holder.haber_title.text = ItemsViewModel.haber_title
        holder.haber_date.text = ItemsViewModel.haber_date



        try {
            Picasso.get().load(ItemsViewModel.haber_img).fit().config(Bitmap.Config.RGB_565)
                .placeholder(R.drawable.baseline_downloading_24).into(holder.haber_image);
        }catch(e:java.lang.IllegalArgumentException){
            Picasso.get().load(R.drawable.baseline_home_24)
                .placeholder(R.drawable.baseline_downloading_24).into(holder.haber_image);

        }catch (re:java.lang.RuntimeException){
            Picasso.get().load(R.drawable.baseline_home_24)
                .placeholder(R.drawable.baseline_downloading_24).into(holder.haber_image);
        }


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View,listener: RecyclerViewInterface) : RecyclerView.ViewHolder(ItemView) {

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

        val haber_image: ImageView = itemView.findViewById(R.id.haber_image)
        val haber_source: TextView = itemView.findViewById(R.id.haber_source)
        val haber_title: TextView = itemView.findViewById(R.id.haber_title)
        val haber_date: TextView = itemView.findViewById(R.id.haber_date)
    }
}
