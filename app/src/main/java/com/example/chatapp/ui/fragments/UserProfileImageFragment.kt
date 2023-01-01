package com.example.chatapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentUserProfileImageBinding
import com.example.chatapp.models.Account
import com.squareup.picasso.Picasso


private const val ARG_PARAM1 = "param1"

class UserProfileImageFragment : Fragment() {
    private var result: Account? = null
    private var _binding: FragmentUserProfileImageBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            result = it.getSerializable("image") as Account?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserProfileImageBinding.inflate(inflater, container, false)

        binding.nameField.text = result?.displayName
        binding.statusField.text = "yaqinda onlayn edi"
        Picasso.get().load(result?.photoUrl).into(binding.image)

        binding.image.setOnClickListener {

            val currentState = binding.telegramMotion.currentState

            val bundle = Bundle()
            bundle.putSerializable("all", result)

            when(currentState) {
                R.id.finishCollapse -> findNavController().navigate(R.id.userImageFragment, bundle)
                R.id.middleCollapse -> findNavController().navigate(R.id.userImageFragment, bundle)
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            UserProfileImageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}