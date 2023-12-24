import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshop.R
import com.example.onlineshop.Upload
import com.squareup.picasso.Picasso

class ImageAdapter(
    private val mContext: Context,
    private val mUploads: List<Upload>
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val v: View = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false)
        return ImageViewHolder(v)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val uploadCurrent: Upload = mUploads[position]
        holder.textViewName.text = uploadCurrent.name
        holder.priceTextView.text = "Price: $${String.format("%.2f", uploadCurrent.price)}"

        // Load image using Picasso library
        Picasso.get()
            .load(uploadCurrent.imageUrl)
           .placeholder(R.mipmap.ic_launcher)
            //.error(R.drawable.ic_launcher_background)
            .fit()
            .centerCrop()
            .into(holder.imageView)

        // Set a click listener for the order button if needed
        holder.orderButton.setOnClickListener {
            // Handle order button click if necessary
            // Example: Open a new activity or perform an action
        }
    }

    override fun getItemCount(): Int {
        return mUploads.size
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.text_view_name)
        val imageView: ImageView = itemView.findViewById(R.id.image_view_upload)
        val priceTextView: TextView = itemView.findViewById(R.id.price_item)
        val orderButton: Button = itemView.findViewById(R.id.order_button)
    }

}