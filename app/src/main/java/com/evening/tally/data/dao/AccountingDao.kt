package com.evening.tally.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.evening.tally.data.entity.AccountingItem
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface AccountingDao {
    @Insert
    suspend fun insert(item: AccountingItem)

    @Query("SELECT * FROM accounting_items ORDER BY date DESC")
    fun getAllItems(): Flow<List<AccountingItem>>  // 确保返回Flow类型

    @Query("DELETE FROM accounting_items")
    suspend fun deleteAll()

    @Query("SELECT * FROM accounting_items")
    suspend fun getAllForExport(): List<AccountingItem>

    @Delete
    suspend fun delete(item: AccountingItem)

    @Update
    suspend fun update(item: AccountingItem)

    @Query("DELETE FROM accounting_items WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("SELECT * FROM accounting_items WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getItemsByDateRange(start: Date, end: Date): Flow<List<AccountingItem>>
}