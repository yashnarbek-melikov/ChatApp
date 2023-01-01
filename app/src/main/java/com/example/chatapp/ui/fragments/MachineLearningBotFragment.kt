package com.example.chatapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentMachineLearningBotBinding

class MachineLearningBotFragment : Fragment(R.layout.fragment_machine_learning_bot) {

    private var _binding: FragmentMachineLearningBotBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentMachineLearningBotBinding.bind(view)
        _binding = binding


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}