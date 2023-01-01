package com.example.chatapp.ui.fragments

import android.os.*
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.chatapp.R
import com.example.chatapp.adapters.MessageAdapter
import com.example.chatapp.databinding.FragmentMessageBinding
import com.example.chatapp.models.Account
import com.example.chatapp.models.Message
import com.example.chatapp.ui.activities.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.util.*


class MessageFragment : Fragment(R.layout.fragment_message) {
    private var result: Account? = null
    private var _binding: FragmentMessageBinding? = null
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var list: ArrayList<Message>

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private val TAG = "MessageFragment"
    private lateinit var seenListener: ValueEventListener
    private lateinit var loadListener: ValueEventListener
    private lateinit var mLinearLayout: LinearLayoutManager

    private val TOTAL_ITEMS_TO_LOAD = 10
    private var mCurrentPage = 1

    // new solution
    private var itemPos = 0
    private var mLastKey = ""
    private var mPrevKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as MainActivity?)!!.hide()

        arguments?.let {
            result = it.getSerializable("all") as Account?
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentMessageBinding.bind(view)
        _binding = binding

        mLinearLayout = LinearLayoutManager(requireContext())
        mLinearLayout.stackFromEnd = true
        binding.recyclerChat.setHasFixedSize(true)
        binding.recyclerChat.layoutManager = mLinearLayout

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("chats")
//        reference.keepSynced(true)
        list = ArrayList()
        messageAdapter = MessageAdapter(firebaseAuth.uid ?: "", list)
        binding.recyclerChat.adapter = messageAdapter


        binding.nameTv.text = result?.displayName
        Picasso.get().load(result?.photoUrl).into(_binding?.userImage)

        binding.topAppBar.setNavigationOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }
        binding.et.requestFocus()
        appBarClickMenus(binding)


        binding.sendBtn.setOnClickListener {

            if (binding.et.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Xabar yozing.", Toast.LENGTH_SHORT).show()
            } else {
                var isHave = false
                binding.et.text.toString().all {
                    isHave = it.isWhitespace()
                    isHave
                }
                if (!isHave) {
                    val messageText = binding.et.text.toString()
                    val date = Calendar.getInstance().time

                    val key = reference.push().key

                    val message = Message(
                        firebaseAuth.uid,
                        result?.uid,
                        messageText,
                        "false",
                        date
                    )
                    val a = Date().time
                    reference
                        .child(key ?: "")
                        .setValue(message).addOnSuccessListener {
                            val b = Date().time
                            Log.d(TAG, "onViewCreated: a ni o'zgarishi: $a")
                            Log.d(TAG, "onViewCreated: b ni o'zgarishi: $b")
                        }

//                    val chatRef = FirebaseDatabase.getInstance().getReference("chatList")
//                        .child(firebaseAuth.uid?:"").child(result?.uid?:"")
//
//                    chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            if(!snapshot.exists()) {
//                                chatRef.child("id").setValue(result?.uid)
//                            }
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//
//                        }
//
//                    })

                    binding.et.setText("")
                    binding.recyclerChat.scrollToPosition(list.size - 1)
                    messageAdapter.notifyItemChanged(list.size - 1)
                } else {
                    Toast.makeText(requireContext(), "Xabar yozing.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                mCurrentPage++
                itemPos = 0
                loadMoreMessages()
//                reference.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD).addValueEventListener(seenListener)
            }

        })

        loadListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val value = child.getValue(Message::class.java)
                    if (value != null) {
                        if (value.to.equals(firebaseAuth.uid) && value.from.equals(result?.uid)) {
                            if(value.isSeen.equals("false")) {
                                val hashMap: HashMap<String, Any> = HashMap()
                                hashMap.put("seen", "true")
                                reference
                                    .child(child.key ?: "").ref.updateChildren(hashMap)
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        seenListener = reference.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                list.clear()
//                var count = 0
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(Message::class.java)
                    if (value != null) {
                        if ((value.to.equals(result?.uid) && value.from.equals(firebaseAuth.uid)) || (value.to.equals(
                                firebaseAuth.uid
                            ) && value.from.equals(result?.uid))
                        ) {
//                            if (value.isSeen == "false") {
//                                count++
//                            }
                            itemPos++
                            if(itemPos == 1) {
                                val messageKey = child.key
                                if (messageKey != null) {
                                    mLastKey = messageKey
                                    mPrevKey = messageKey
                                }
                            }
                            list.add(value)
                        }
                    }
                }
                binding.recyclerChat.scrollToPosition(list.size - 1)
                messageAdapter.notifyDataSetChanged()
                binding.swipeRefreshLayout.isRefreshing = false
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        binding.linearLayout.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("all", result)
            findNavController().navigate(R.id.userProfileFragment, bundle)
        }

        binding.userImage.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("image", result)
            findNavController().navigate(R.id.userProfileImageFragment, bundle)
        }

        binding.smileCv.setOnClickListener {
            Toast.makeText(requireContext(), "Smile bosildi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadMoreMessages() {

        val query = reference.orderByKey().endAt(mLastKey).limitToLast(10)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                list.clear()
//                var count = 0
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(Message::class.java)
                    if (value != null) {
                        if ((value.to.equals(result?.uid) && value.from.equals(firebaseAuth.uid)) || (value.to.equals(
                                firebaseAuth.uid
                            ) && value.from.equals(result?.uid))
                        ) {
//                            if (value.isSeen == "false") {
//                                count++
//                            }
                            val messageKey = child.key
                            if(mPrevKey != messageKey){
                                list.add(itemPos++, value)
                            } else {
                                mPrevKey = mLastKey
                            }
                            if(itemPos == 1) {

                                if (messageKey != null) {
                                    mLastKey = messageKey
                                }
                            }
                        }
                    }
                }
                mLinearLayout.scrollToPositionWithOffset(10, 0)
                messageAdapter.notifyDataSetChanged()
                _binding?.swipeRefreshLayout?.isRefreshing = false
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    private fun appBarClickMenus(binding: FragmentMessageBinding) {

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.mute_id -> {
                    Toast.makeText(requireContext(), "Mute Clicked", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.search_id -> {
                    Toast.makeText(requireContext(), "Search Clicked", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.clear_id -> {
                    Toast.makeText(requireContext(), "Clear History Clicked", Toast.LENGTH_SHORT)
                        .show()
                    true
                }

                R.id.change_colors_id -> {
                    Toast.makeText(requireContext(), "Change Colors Clicked", Toast.LENGTH_SHORT)
                        .show()
                    true
                }

                R.id.delete_id -> {
                    Toast.makeText(requireContext(), "Delete chat Clicked", Toast.LENGTH_SHORT)
                        .show()
                    true
                }
                else -> false

            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Account) =
            MessageFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("all", param1)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        (activity as MainActivity?)!!.show()
    }

    override fun onPause() {
        super.onPause()
        reference.removeEventListener(seenListener)
        reference.removeEventListener(loadListener)
    }

    override fun onStop() {
        super.onStop()
        reference.removeEventListener(seenListener)
        reference.removeEventListener(loadListener)
    }

    override fun onResume() {
        super.onResume()
//        reference.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD).addValueEventListener(seenListener)
        reference.addValueEventListener(loadListener)
    }
}