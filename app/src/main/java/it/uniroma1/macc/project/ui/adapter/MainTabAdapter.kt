package it.uniroma1.macc.project.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MainTabAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val fragmentList: MutableList<Fragment> = mutableListOf()

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }
}