package com.example.dermai.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dermai.data.model.Product
import com.example.dermai.data.model.ResultResponse
import com.example.dermai.utils.Converters

@Database(
    entities = [Product::class , ResultResponse::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class UserDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): UserDatabase {
            if(INSTANCE == null) {
                synchronized(UserDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        UserDatabase::class.java, "dermai_database")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE as UserDatabase
        }
    }
}