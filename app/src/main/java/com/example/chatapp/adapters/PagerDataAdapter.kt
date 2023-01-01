package com.example.chatapp.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.chatapp.ui.fragments.*

class PagerDataAdapter(fragment: Fragment,private val list: ArrayList<String>) :
    FragmentStateAdapter(fragment) {

//    override fun getCount(): Int {
//        return 5
//    }
//
//    override fun getItem(position: Int): Fragment {
//
//        var fragment: Fragment? = null
//
//        when(position) {
//            0 -> fragment = AllContactsFragment()
//            1 -> fragment = PrivateMessagesFragment()
//            2 -> fragment = SupergroupsFragment()
//            3 -> fragment = ChannelsFragment()
//            4 -> fragment = NotReadFragment()
//        }
//
//        return fragment!!
//    }

    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null

        when(position) {
            0 -> fragment = AllContactsFragment()
            1 -> fragment = PrivateMessagesFragment()
            2 -> fragment = SupergroupsFragment()
            3 -> fragment = ChannelsFragment()
            4 -> fragment = NotReadFragment()
        }

        return fragment?: Fragment()
    }
}