package com.example.chatapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chatapp.adapters.PagerDataAdapter
import com.example.chatapp.databinding.FragmentHomeBinding
import com.example.chatapp.databinding.ItemTabBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var pagerDataAdapter: PagerDataAdapter
    private lateinit var titleList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        loadTitles()
        pagerDataAdapter = PagerDataAdapter(this, titleList)
        binding.viewPager.adapter = pagerDataAdapter

        TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab, position ->
            tab.text = titleList[position]
            val itemTabBinding = ItemTabBinding.inflate(layoutInflater)
            itemTabBinding.tv.text = tab.text
            if (position == 0) {
                itemTabBinding.tv.setTextColor(Color.WHITE)
            } else {
                itemTabBinding.tv.setTextColor(Color.parseColor("#CEEFFD"))
            }
            tab.customView = itemTabBinding.root
        }.attach()

//        setTabs()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val itemTabBinding = ItemTabBinding.bind(tab?.customView!!)
                itemTabBinding.tv.setTextColor(Color.WHITE)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val itemTabBinding = ItemTabBinding.bind(tab?.customView!!)
                itemTabBinding.tv.setTextColor(Color.parseColor("#CEEFFD"))
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        return binding.root
    }

    private fun loadTitles() {
        titleList = ArrayList()

        titleList.add("Barcha chatlar")
        titleList.add("Shaxsiy")
        titleList.add("Guruhlar")
        titleList.add("Kanallar")
        titleList.add("O'qilmagan")
        titleList.add("Sun'iy Intellekt")
    }

//    private fun setTabs() {
//        val tabCount = binding.tabLayout.tabCount
//        for(i in 0 until tabCount) {
//            val tab: TabLayout.Tab? = binding.tabLayout.getTabAt(i)
//            val itemTabBinding = ItemTabBinding.inflate(layoutInflater)
//            when(i) {
//                0 -> itemTabBinding.tv.text = "Barcha chatlar"
//                1 -> itemTabBinding.tv.text = "Shaxsiy"
//                2 -> itemTabBinding.tv.text = "Guruhlar"
//                3 -> itemTabBinding.tv.text = "Kanallar"
//                4 -> itemTabBinding.tv.text = "O'qilmagan"
//            }
//            if(i == 0) {
//                itemTabBinding.tv.setTextColor(Color.WHITE)
//            } else {
//                itemTabBinding.tv.setTextColor(Color.parseColor("#CEEFFD"))
//            }
//            tab?.customView = itemTabBinding.root
//        }
//    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}