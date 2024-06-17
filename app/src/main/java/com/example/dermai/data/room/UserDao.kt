package com.example.dermai.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dermai.data.model.Product
import com.example.dermai.data.model.ResultResponse

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWishlist(product: Product)

    @Query("SELECT * FROM Product")
    fun getAllWishlist(): LiveData<List<Product>>

    @Query("SELECT count(*) FROM Product WHERE url = :url")
    suspend fun checkWishlist(url: String): Int

    @Query("DELETE FROM Product WHERE url = :url")
    suspend fun deleteWishlist(url: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertResult(result: ResultResponse)

    @Query("SELECT * FROM ResultResponse")
    fun getResult(): LiveData<ResultResponse>

    @Query("DELETE FROM ResultResponse")
    suspend fun deleteResult(): Int

}