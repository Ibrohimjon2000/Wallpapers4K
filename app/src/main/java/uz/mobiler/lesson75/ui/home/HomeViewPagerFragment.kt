package uz.mobiler.lesson75.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.mobiler.lesson75.R
import uz.mobiler.lesson75.adapters.ImageRvAdapter
import uz.mobiler.lesson75.database.entity.HitEntity
import uz.mobiler.lesson75.databinding.FragmentHomeViewPagerBinding
import uz.mobiler.lesson75.vm.MyViewModel
import kotlin.coroutines.CoroutineContext

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeViewPagerFragment : Fragment(), CoroutineScope {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var binding: FragmentHomeViewPagerBinding
    private lateinit var imageRvAdapter: ImageRvAdapter
    lateinit var myViewModel: MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeViewPagerBinding.inflate(inflater, container, false)
        binding.apply {
            progress.visibility = View.VISIBLE
            myViewModel = MyViewModel(param1.toString(), false)
            imageRvAdapter =
                ImageRvAdapter(requireContext(), object : ImageRvAdapter.OnItemClickListener {
                    override fun onItemClickListener(imageModel: HitEntity, position: Int) {
                        val bundle = Bundle()
                        bundle.putSerializable("img", imageModel)
                        Navigation.findNavController(root).navigate(R.id.imageInfoFragment, bundle)
                    }
                })
            rv.adapter = imageRvAdapter
            launch {
                myViewModel.flow.collect {
                    progress.visibility = View.GONE
                    imageRvAdapter.submitData(it)
                }
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            HomeViewPagerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}