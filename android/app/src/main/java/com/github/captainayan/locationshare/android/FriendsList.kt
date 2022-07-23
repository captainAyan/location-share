package com.github.captainayan.locationshare.android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendsList {

    class FriendAdapter(val mList:ArrayList<DB.Friend>,
                        private val onTrackButtonClick: (position: Int) -> Unit,
                        private val onDeleteButtonClick: (position: DB.Friend) -> Unit)
        : RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_friend, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.nameTv.text = mList[position].name
            holder.uuidTv.text = mList[position].uuid

            holder.trackBtn.setOnClickListener {_ ->
                run {
                    onTrackButtonClick(position)
                }
            }
            holder.deleteBtn.setOnClickListener {_ ->
                run {
                    onDeleteButtonClick(mList[position])
                }
            }
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameTv: TextView = itemView.findViewById(R.id.name_tv)
            val uuidTv: TextView = itemView.findViewById(R.id.uuid_tv)
            val trackBtn: Button = itemView.findViewById(R.id.track_friend_btn)
            val deleteBtn: Button = itemView.findViewById(R.id.delete_friend_btn)
        }
    }
}