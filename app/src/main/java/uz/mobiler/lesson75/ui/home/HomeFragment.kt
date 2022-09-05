package uz.mobiler.lesson75.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import uz.mobiler.lesson75.adapters.ViewPager2FragmentAdapter
import uz.mobiler.lesson75.databinding.FragmentHomeBinding
import uz.mobiler.lesson75.databinding.ItemTabBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewPager2FragmentAdapter: ViewPager2FragmentAdapter
    val list = arrayOf(
        "All",
        "Nature",
        "Fashion",
        "Education",
        "Animals",
        "Computer",
        "Food",
        "Sports",
        "Travel",
        "Music"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.apply {

            viewPager2FragmentAdapter =
                ViewPager2FragmentAdapter(this@HomeFragment, list.toList())
            binding.viewPager2.adapter = viewPager2FragmentAdapter

            val tabLayoutMediator = TabLayoutMediator(
                binding.tabLayout, binding.viewPager2
            ) { tab, position ->
                val itemTabBinding: ItemTabBinding = ItemTabBinding.inflate(layoutInflater)
                itemTabBinding.titleTab.text = list[position]
                if (position == 0) {
                    itemTabBinding.titleTab.setTextColor(Color.WHITE)
                    itemTabBinding.circle.visibility = View.VISIBLE
                } else {
                    itemTabBinding.titleTab.setTextColor(Color.parseColor("#80FFFFFF"))
                    itemTabBinding.circle.visibility = View.INVISIBLE
                }
                tab.customView = itemTabBinding.root
            }
            tabLayoutMediator.attach()
            binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val itemTabBinding: ItemTabBinding =
                        ItemTabBinding.bind(tab.customView!!)
                    itemTabBinding.titleTab.setTextColor(Color.WHITE)
                    itemTabBinding.circle.visibility = View.VISIBLE
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    val itemTabBinding: ItemTabBinding =
                        ItemTabBinding.bind(tab.customView!!)
                    itemTabBinding.titleTab.setTextColor(Color.parseColor("#80FFFFFF"))
                    itemTabBinding.circle.visibility = View.INVISIBLE
                }

                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }
        return binding.root
    }
}