package ng.hotels.booking.app.adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class TabAdapter(val fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    var mFragmentList = ArrayList<Fragment>()
    var mFragmentTitleList = ArrayList<String>()

    override fun getItem(p0: Int): Fragment {
        return mFragmentList[p0]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }


    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position];
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }
    
}