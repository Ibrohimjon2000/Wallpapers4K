package uz.mobiler.lesson75.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import uz.mobiler.lesson75.R
import uz.mobiler.lesson75.database.AppDatabase
import uz.mobiler.lesson75.database.entity.HitEntity
import uz.mobiler.lesson75.databinding.FragmentHistoryBinding
import uz.mobiler.lesson75.databinding.ItemImageDataBinding

class ImageAdapter(
    val binding: FragmentHistoryBinding,
    val context: Context,
    val imageList: ArrayList<HitEntity>,
    val listener: OnItemClickListener
) : RecyclerView.Adapter<ImageAdapter.Vh>() {
    val appDatabase: AppDatabase by lazy {
        AppDatabase.getInstance(context)
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
                itemView.setOnLongClickListener(View.OnLongClickListener {

                    var delete = arrayOf<CharSequence>("Delete")

                    val builder = AlertDialog.Builder(context)
                    builder.setItems(
                        delete
                    ) { p0, p1 ->
                        if (p1 == 0) {
                            imageList.remove(imageModel)
                            appDatabase.hitDao().deleteHitEntity(imageModel)
                            if (imageList.isNotEmpty()) {
                                binding.info.visibility = View.INVISIBLE
                            } else {
                                binding.info.visibility = View.VISIBLE
                            }
                            notifyDataSetChanged()
                        }
                    }
                    builder.create().show()
                    return@OnLongClickListener false
                })
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