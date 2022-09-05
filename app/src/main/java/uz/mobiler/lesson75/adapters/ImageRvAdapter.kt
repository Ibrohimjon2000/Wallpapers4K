package uz.mobiler.lesson75.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import uz.mobiler.lesson75.R
import uz.mobiler.lesson75.database.entity.HitEntity
import uz.mobiler.lesson75.databinding.ItemImageDataBinding

class ImageRvAdapter(
    val context: Context,
    val listener: OnItemClickListener,
) : PagingDataAdapter<HitEntity, ImageRvAdapter.Vh>(MyDiffUtil()) {

    class MyDiffUtil : DiffUtil.ItemCallback<HitEntity>() {
        override fun areItemsTheSame(oldItem: HitEntity, newItem: HitEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HitEntity, newItem: HitEntity): Boolean {
            return oldItem == newItem
        }
    }

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
        val item = getItem(position)
        if (item != null) {
            holder.onBind(item, position)
        }
    }

    interface OnItemClickListener {
        fun onItemClickListener(imageModel: HitEntity, position: Int)
    }
}