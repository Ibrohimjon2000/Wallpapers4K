package uz.mobiler.lesson75.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.mobiler.lesson75.ui.home.HomeViewPagerFragment

class ViewPager2FragmentAdapter(
    fragment: Fragment,
    private val list: List<String>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return HomeViewPagerFragment.newInstance(list[position])
    }
}