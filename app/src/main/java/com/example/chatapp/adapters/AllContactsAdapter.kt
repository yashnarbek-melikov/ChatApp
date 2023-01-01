package com.example.chatapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.databinding.ItemAllContactsBinding
import com.example.chatapp.models.Account
import com.example.chatapp.models.Message
import com.example.chatapp.utils.Functions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.*

class AllContactsAdapter(
    var allContactsList: ArrayList<Account>,
    var listener: OnAllItemsClickListener,
    var isChat: Boolean
) : RecyclerView.Adapter<AllContactsAdapter.MyViewHolder>() {

    var theLastMessage: String? = null
    var theTime: String? = null

    inner class MyViewHolder(var itemAllContactsBinding: ItemAllContactsBinding) :
        RecyclerView.ViewHolder(itemAllContactsBinding.root) {
        fun onBind(account: Account) {
            itemAllContactsBinding.apply {
                Picasso.get().load(account.photoUrl).into(personImage)
                personName.text = account.displayName

                if(isChat) {
                    lastMessageAndTime(account, personMessage, clockTv)
                    messageUserSeen(account, doneImage, quantityTv, clockCv)
                }


                if (isChat) {
                    if (account.status.equals("online")) {
                        imageOnOff.setBackgroundColor(Color.parseColor("#50C878"))
                    } else {
                        imageOnOff.setBackgroundColor(Color.parseColor("#B2C5C8"))
                    }
                }
            }
            itemView.setOnClickListener {
                listener.onAllItemsClickListener(account)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemAllContactsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(allContactsList[position])
    }

    override fun getItemCount(): Int = allContactsList.size

    interface OnAllItemsClickListener {
        fun onAllItemsClickListener(account: Account)
    }

     private fun messageUserSeen(account: Account, check: ImageView, countText: TextView, countCv: CardView) {

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("chats")
        var seenOrUnseen = ""
        var count = 0

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val value = child.getValue(Message::class.java)
                    if(value != null) {
                        if (value.to.equals(account.uid) && value.from.equals(firebaseUser?.uid)) {
                            if(value.isSeen == "true") {
                                seenOrUnseen = "true"
                            }
                            else {
                                seenOrUnseen = "false"
                            }
                        }
                        else if(value.to.equals(firebaseUser?.uid) && value.from.equals(account.uid)) {
                            if(value.isSeen == "true") {
                                seenOrUnseen = ""
                            }
                            else {
                                count++
                                seenOrUnseen = "$count"
                            }
                        }
                    }
                }

                when(seenOrUnseen) {
                    "true" -> {
                        check.setImageResource(R.drawable.checked_read)
                        countCv.visibility = View.INVISIBLE
                        countCv.setCardBackgroundColor(Color.WHITE)
                        countText.text = ""
                    }
                    "false" -> {
                        check.setImageResource(R.drawable.checked_unread)
                        countCv.visibility = View.INVISIBLE
                        countCv.setCardBackgroundColor(Color.WHITE)
                        countText.text = ""
                    }
                    "" -> {
                        check.visibility = View.INVISIBLE
                        countCv.visibility = View.INVISIBLE
                        countCv.setCardBackgroundColor(Color.WHITE)
                        countText.text = ""
                    }
                    else -> {
                        check.visibility = View.INVISIBLE
                        countCv.visibility = View.VISIBLE
                        countCv.setCardBackgroundColor(Color.parseColor("#9E9C9C"))
                        countText.text = seenOrUnseen
                    }
                }
                seenOrUnseen = ""
                count = 0
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

     private fun lastMessageAndTime(account: Account, lastMessage: TextView, time: TextView) {
        theLastMessage = "default"
        theTime = ""

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("chats")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val value = child.getValue(Message::class.java)
                    if (value != null) {
                        if (value.to.equals(firebaseUser?.uid) && value.from.equals(account.uid)
                            || value.to.equals(account.uid) && value.from.equals(firebaseUser?.uid)
                        ) {
                            theLastMessage = value.message
                            theTime =  Functions.calculateDateMessage(Date(), value)
                        }
                    }
                }
                when (theLastMessage) {
                    "default" -> {
                        lastMessage.setText("${account.displayName} Telegramga qo'shildi!")
                        lastMessage.setTextColor(Color.parseColor("#458DC8"))
                    }
                    else -> lastMessage.setText(theLastMessage)
                }

                when(theTime) {
                    "" -> {
                        time.setText(Functions.calculateDateAccount(Date(), account))
                    }
                    else -> time.setText(theTime)
                }
                theTime = ""
                theLastMessage = "default"
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}