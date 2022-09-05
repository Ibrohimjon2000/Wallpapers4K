package uz.mobiler.lesson75.ui.liked

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.gson.reflect.TypeToken
import uz.mobiler.lesson75.R
import uz.mobiler.lesson75.adapters.ImageLikeAdapter
import uz.mobiler.lesson75.database.entity.HitEntity
import uz.mobiler.lesson75.databinding.FragmentLikedBinding
import uz.mobiler.lesson75.singleton.MyGson

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class LikedFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var binding: FragmentLikedBinding
    private lateinit var list: ArrayList<HitEntity>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var imageLikeAdapter: ImageLikeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikedBinding.inflate(inflater, container, false)
        binding.apply {
            sharedPreferences = requireContext().getSharedPreferences("Like", Context.MODE_PRIVATE)
            editor = sharedPreferences.edit()
            val likeJsonString = sharedPreferences.getString("like", "")
            if (likeJsonString == "") {
                list = ArrayList()
            } else {
                val type = object : TypeToken<List<HitEntity?>?>() {}.type
                list = MyGson.getInstance().gson.fromJson(likeJsonString, type)
            }
            if (list.isNotEmpty()) {
                likeInfo.visibility = View.INVISIBLE
            } else {
                likeInfo.visibility = View.VISIBLE
            }
            imageLikeAdapter = ImageLikeAdapter(
                requireContext(),
                list,
                object : ImageLikeAdapter.OnItemClickListener {
                    override fun onItemClickListener(imageModel: HitEntity, position: Int) {
                        val bundle = Bundle()
                        bundle.putSerializable("img", imageModel)
                        Navigation.findNavController(root).navigate(R.id.imageInfoFragment, bundle)
                    }
                })
            rv.adapter = imageLikeAdapter
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LikedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}