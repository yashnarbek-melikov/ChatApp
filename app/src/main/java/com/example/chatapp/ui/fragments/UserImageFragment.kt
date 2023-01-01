package com.example.chatapp.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentUserImageBinding
import com.example.chatapp.models.Account
import com.squareup.picasso.Picasso

private const val ARG_PARAM1 = "param1"

class UserImageFragment : Fragment() {
    private var result: Account? = null
    private var _binding: FragmentUserImageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            result = it.getSerializable("all") as Account?
        }

        activity?.window?.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.black)
        activity?.window?.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.black)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUserImageBinding.inflate(inflater, container, false)

        val display: Display = activity?.windowManager!!.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x

        binding.image.layoutParams.height = width
        binding.image.layoutParams.width = width

        Picasso.get().load(result?.displayName).into(binding.image)

        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            UserImageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    @SuppressLint("ResourceType")
    override fun onDestroy() {
        _binding = null
        activity?.window?.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.telegram_status_color)
        
        super.onDestroy()
    }
}