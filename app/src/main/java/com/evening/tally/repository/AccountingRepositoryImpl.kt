package com.evening.tally.repository

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.evening.tally.data.AppDatabase
import com.evening.tally.data.dao.AccountingDao
import com.evening.tally.data.entity.AccountingItem
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import java.util.Date
import javax.inject.Inject

class AccountingRepositoryImpl @Inject constructor(
    private val dao: AccountingDao,
    private val database: AppDatabase
) : AccountingRepository {
    // 获取所有条目
    override fun getAllItems() = dao.getAllItems().flowOn(Dispatchers.IO)

    // 添加条目
    override suspend fun addItem(item: AccountingItem) = dao.insert(item)
    // 删除单个条目
    override suspend fun delete(item: AccountingItem) = dao.delete(item)

    // 更新条目
    override suspend fun update(item: AccountingItem) = dao.update(item)

    // 批量删除条目
    override suspend fun deleteByIds(ids: List<Long>) = dao.deleteByIds(ids)

    // 按日期范围查询
    override fun getItemsByDateRange(start: Date, end: Date): Flow<List<AccountingItem>> {
        return dao.getItemsByDateRange(start, end).flowOn(Dispatchers.IO)
    }

    override suspend fun exportData(context: Context, uri: Uri) {
        try {
            val items = dao.getAllForExport()
            val json = Gson().toJson(items)
            context.contentResolver.openOutputStream(uri)?.use {
                it.write(json.toByteArray())
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun importData(context: Context, uri: Uri) {
        try {
            val json = context.contentResolver.openInputStream(uri)?.use {
                it.bufferedReader().readText()
            } ?: throw IOException("文件读取失败")

            val items = Gson().fromJson(json, Array<AccountingItem>::class.java).toList()

            // 使用协程事务
            database.withTransaction {
                dao.deleteAll()
                items.forEach { dao.insert(it) }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun searchItems(query: String, isWholeWord: Boolean): List<AccountingItem> {
        return if (isWholeWord) {
            dao.searchExact(query)
        } else {
            dao.searchFuzzy("%$query%") // 添加通配符进行模糊查询
        }
    }
}