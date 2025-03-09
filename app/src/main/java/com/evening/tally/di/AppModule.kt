package com.evening.tally.di


import android.content.Context
import androidx.room.Room
import com.evening.tally.data.AppDatabase
import com.evening.tally.data.dao.AccountingDao
import com.evening.tally.repository.AccountingRepository
import com.evening.tally.repository.AccountingRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "accounting_db"
        ).build()
    }

    @Provides
    fun provideAccountingDao(database: AppDatabase): AccountingDao {
        return database.accountingDao()
    }

    @Provides
    @Singleton
    fun provideAccountingRepository(
        dao: AccountingDao,          // 第一个参数
        database: AppDatabase        // 新增的第二个参数
    ): AccountingRepository {
        return AccountingRepositoryImpl(
            dao = dao,               // 注入 DAO
            database = database      // 注入数据库实例
        )
    }
}