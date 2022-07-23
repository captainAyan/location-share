package com.github.captainayan.locationshare.android

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

class DB {
    @Entity
    data class Friend(
        @ColumnInfo(name = "name") val name: String?,
        @ColumnInfo(name = "uuid") val uuid: String?
    ) {
        @PrimaryKey(autoGenerate = true)
        var id: Int? = null
    }

    @Dao
    interface FriendDao {
        @Query("SELECT * FROM friend")
        fun getAll(): List<Friend>

        @Insert
        fun insert(friend: Friend)

        @Delete
        fun delete(friend: Friend)
    }

    @Database(entities = [Friend::class], version = 1)
    abstract class FriendDatabase : RoomDatabase() {
        abstract fun friendDao(): FriendDao

        companion object {
            private var INSTANCE: FriendDatabase? = null
            fun getDatabase(context: Context): FriendDatabase {
                if (INSTANCE == null) {
                    synchronized(this) {
                        INSTANCE =
                            Room.databaseBuilder(context, FriendDatabase::class.java, "friend_database")
                                .allowMainThreadQueries()
                                .build()
                    }
                }
                return INSTANCE!!
            }
        }
    }
}