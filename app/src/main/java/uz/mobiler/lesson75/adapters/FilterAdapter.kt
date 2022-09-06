package uz.mobiler.lesson75.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.mobiler.lesson75.databinding.ItemFilterBinding
import uz.mobiler.lesson75.models.FilterModel

class FilterAdapter(
    val context: Context,
    val list: ArrayList<FilterModel>,
    val listener: OnItemClickListener
) : RecyclerView.Adapter<FilterAdapter.Vh>() {

    inner class Vh(val itemFilterBinding: ItemFilterBinding) :
        RecyclerView.ViewHolder(itemFilterBinding.root) {
        fun onBind(imageModel: FilterModel, position: Int) {
            itemFilterBinding.apply {
                Picasso.get().load(imageModel.imageUrl).into(filterImg)
                filterText.text = imageModel.name

                itemView.setOnClickListener {
                    listener.onItemClickListener(imageModel, position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onItemClickListener(imageModel: FilterModel, position: Int)
    }
}