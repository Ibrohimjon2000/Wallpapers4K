package uz.mobiler.lesson75.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import uz.mobiler.lesson75.R
import uz.mobiler.lesson75.database.entity.HitEntity
import uz.mobiler.lesson75.databinding.ItemImageDataBinding

class ImageLikeAdapter(
    val context: Context,
    val imageList: ArrayList<HitEntity>,
    val listener: OnItemClickListener
) : RecyclerView.Adapter<ImageLikeAdapter.Vh>() {
    inner class Vh(val itemImageDataBinding: ItemImageDataBinding) :
        RecyclerView.ViewHolder(itemImageDataBinding.root) {
        fun onBind(imageModel: HitEntity, position: Int) {
            itemImageDataBinding.apply {
                Glide.with(context)
                    .load(imageModel.largeImageURL)
                    .apply(
                        RequestOptions().placeholder(
                            R.drawable.plase_img
                        ).centerCrop()
                    )
                    .into(img)

                itemView.setOnClickListener {
                    listener.onItemClickListener(imageModel, position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemImageDataBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(imageList[position], position)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    interface OnItemClickListener {
        fun onItemClickListener(imageModel: HitEntity, position: Int)
    }
}