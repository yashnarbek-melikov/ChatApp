package com.example.chatapp.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.databinding.ReceiveMsgItemBinding
import com.example.chatapp.databinding.SendMsgItemBinding
import com.example.chatapp.models.Message
import com.example.chatapp.utils.Functions
import com.example.chatapp.utils.Functions.Companion.time
import com.example.chatapp.utils.Functions.Companion.today
import java.util.Date

class MessageAdapter(
    var currentUid: String,
    var list: List<Message>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    inner class SendMsgHolder(var sendMsgItemBinding: SendMsgItemBinding) :
        RecyclerView.ViewHolder(sendMsgItemBinding.root) {
        fun onBind(message: Message, position: Int) {
            sendMsgItemBinding.apply {

                var previousDay = 0
                if(position > 0) {
                    val message1 = list[position - 1]
                    previousDay = message1.date?.date?:0
                }
                val today = list[position].date?.date?:0

                setTimeTextVisibility(today, previousDay, dateLayout)

                if (list[position].isSeen == "true") {
                    sendTrueImage.setImageResource(R.drawable.ic_baseline_done_all_24)
                } else {
                    sendTrueImage.setImageResource(R.drawable.ic_baseline_done_24)
                }

                sendTv.text = message.message
                timeTv.text = message.date?.let { time(it) }
                dateTv.text = message.date?.let { today(it) }
            }
        }
    }

    inner class ReceiveMsgHolder(var receiveMsgItemBinding: ReceiveMsgItemBinding) :
        RecyclerView.ViewHolder(receiveMsgItemBinding.root) {
        fun onBind(message: Message, position: Int) {
            receiveMsgItemBinding.apply {
                var previousDay = 0
                if(position > 0) {
                    val message1 = list[position - 1]
                    previousDay = message1.date?.date?:0
                }
                val today = list[position].date?.date?:0
                setTimeTextVisibility(today, previousDay, dateLayout)

                receiveTv.text = message.message

                timeTv.text = message.date?.let { time(it) }
                dateTv.text = message.date?.let { today(it) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        if (list[position].from == currentUid) {
            return 1
        }
        return 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            return SendMsgHolder(
                SendMsgItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return ReceiveMsgHolder(
                ReceiveMsgItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is SendMsgHolder) {
            holder.onBind(list[position], position)
        } else {
            val toVh = holder as ReceiveMsgHolder
            toVh.onBind(list[position], position)
        }
    }

    override fun getItemCount(): Int = list.size

    private fun setTimeTextVisibility(today: Int, previousDay: Int, cardView: CardView) {

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        if(previousDay == 0) {
            cardView.visibility = View.VISIBLE
            params.topMargin = 25
            params.bottomMargin = 25
            params.gravity = Gravity.CENTER_HORIZONTAL
            cardView.layoutParams = params

        } else {
            val sameDay = today == previousDay
            if(sameDay) {
                cardView.visibility = View.GONE
                params.topMargin = 0
                params.bottomMargin = 0
                cardView.layoutParams = params
            }
            else {
                cardView.visibility = View.VISIBLE
                params.topMargin = 25
                params.bottomMargin = 25
                params.gravity = Gravity.CENTER_HORIZONTAL
                cardView.layoutParams = params
            }
        }
    }
}