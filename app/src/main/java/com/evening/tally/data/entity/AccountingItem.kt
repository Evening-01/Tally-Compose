package com.evening.tally.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date



@Entity(tableName = "accounting_items")
data class AccountingItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "date")
    val date: Date,
    val orderNumber: String,
    val diameter: Double,    // 直径
    val thickness: Double,   // 壁厚
    val length: Int,         // 长度
    val bundleWeight: Double,// 单捆重量
    val totalBundles: Int,   // 总捆数
    val unitPrice: Double    // 开票价
)