package com.github.captainayan.locationshare.android;

import android.content.Intent
import android.os.Bundle;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class ChooseFriendActivity: AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar

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

        toolbar = findViewById<View>(R.id.topAppBar) as MaterialToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Choose A Friend"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { this@ChooseFriendActivity.finish() }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.choose_friend_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        if (item.itemId == R.id.add_friend) {
            startActivity(Intent(this@ChooseFriendActivity, AddFriendActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}