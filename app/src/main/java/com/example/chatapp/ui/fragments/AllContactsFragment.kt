package com.example.chatapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.adapters.AllContactsAdapter
import com.example.chatapp.databinding.FragmentAllContactsBinding
import com.example.chatapp.models.Account
import com.example.chatapp.models.ChatList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"

class AllContactsFragment : Fragment(R.layout.fragment_all_contacts) {
    private var param1: String? = null
    private var _binding: FragmentAllContactsBinding? = null
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var list: ArrayList<Account>
    private lateinit var accountAdapter: AllContactsAdapter
    private lateinit var readListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAllContactsBinding.bind(view)
        _binding = binding

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")

        list = ArrayList()

        accountAdapter = AllContactsAdapter(list, object : AllContactsAdapter.OnAllItemsClickListener {
            override fun onAllItemsClickListener(account: Account) {
                val bundle = Bundle()
                bundle.putSerializable("all", account)
                findNavController().navigate(R.id.messageFragment, bundle)
            }
        }, true)

        binding.allContactsRv.adapter = accountAdapter

//        reference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                userList.clear()
//                for (child in snapshot.children) {
//                    val chatList = child.getValue(ChatList::class.java)
//                    if (chatList != null) {
//                        userList.add(chatList)
//                    }
//                }
//                chatListMethod()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })

        readListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(Account::class.java)
                    if (value != null && value.uid != firebaseAuth.uid) {
                        list.add(value)
                    }
                }
                accountAdapter.notifyItemRangeChanged(0, list.size)
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })


    }

//    private fun chatListMethod() {
//        val reference = FirebaseDatabase.getInstance().getReference("users")
//        reference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                list.clear()
//                for (child in snapshot.children) {
//                    val user = child.getValue(Account::class.java)
//                    for (chatList in userList) {
//                        if (user != null) {
//                            if(user.uid.equals(chatList.id)) {
//                                list.add(user)
//                            }
//                        }
//                    }
//                }
//                accountAdapter.notifyItemRangeChanged(0, list.size)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })
//    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            AllContactsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}