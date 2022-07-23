package com.github.captainayan.locationshare.android;

import android.content.Intent
import android.os.Bundle;
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChooseFriendActivity: AppCompatActivity() {

    private lateinit var friendDao: DB.FriendDao
    private lateinit var friendList: ArrayList<DB.Friend>
    private lateinit var adapter: FriendsList.FriendAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_friend);

        friendDao = DB.FriendDatabase.getDatabase(this).friendDao()
        friendList = friendDao.getAll() as ArrayList<DB.Friend>

        recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = FriendsList.FriendAdapter(friendList,
            {position -> onTrackButtonClick(position)},
            {friend -> onDeleteButtonClick(friend)}
        )

        recyclerView.adapter = adapter
    }


    private fun onTrackButtonClick(position: Int) {
        friendList[position].uuid
        val intent: Intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("uuid", friendList[position].uuid)
        startActivity(intent)
    }

    private fun onDeleteButtonClick(friend: DB.Friend) {
        val index: Int = friendList.indexOf(friend)
        friendDao.delete(friend)
        adapter.mList.removeAt(index)
        adapter.notifyItemRemoved(index)
        adapter.notifyItemRangeChanged(index, adapter.mList.size)
    }

}