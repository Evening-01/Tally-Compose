package com.evening.tally.repository

import android.content.Context
import android.net.Uri
import com.evening.tally.data.entity.AccountingItem
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface AccountingRepository {
    fun getAllItems(): Flow<List<AccountingItem>>
    suspend fun addItem(item: AccountingItem)
    suspend fun delete(item: AccountingItem)
    suspend fun update(item: AccountingItem)
    suspend fun deleteByIds(ids: List<Long>)
    suspend fun exportData(context: Context, uri: Uri)
    suspend fun importData(context: Context, uri: Uri)
    fun getItemsByDateRange(start: Date, end: Date): Flow<List<AccountingItem>>
}