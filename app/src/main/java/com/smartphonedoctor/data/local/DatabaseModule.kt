package com.smartphonedoctor.data.local

import android.content.Context
import androidx.room.Room
import com.smartphonedoctor.data.local.dao.ScanResultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "smartphonedoctor_db"
        ).build()
    }

    @Provides
    fun provideScanResultDao(appDatabase: AppDatabase): ScanResultDao {
        return appDatabase.scanResultDao()
    }
}
