package uz.mobiler.lesson75.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import uz.mobiler.lesson75.MainActivity
import uz.mobiler.lesson75.R
import uz.mobiler.lesson75.adapters.ImageAdapter
import uz.mobiler.lesson75.database.AppDatabase
import uz.mobiler.lesson75.database.entity.HitEntity
import uz.mobiler.lesson75.databinding.FragmentHistoryBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HistoryFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    val appDatabase: AppDatabase by lazy {
        AppDatabase.getInstance(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var imageList: List<HitEntity>
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        binding.apply {
            imageList = appDatabase.hitDao().getAllHits()
            if (imageList.isNotEmpty()) {
                info.visibility = View.INVISIBLE
            } else {
                info.visibility = View.VISIBLE
            }
            imageAdapter = ImageAdapter(
                binding,
                requireContext(),
                imageList as ArrayList<HitEntity>,
                object : ImageAdapter.OnItemClickListener {
                    override fun onItemClickListener(imageModel: HitEntity, position: Int) {
                        val bundle = Bundle()
                        bundle.putSerializable("img", imageModel)
                        Navigation.findNavController(root).navigate(R.id.imageInfoFragment, bundle)
                    }
                })
            rv.adapter = imageAdapter
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).hideCard()
    }
}