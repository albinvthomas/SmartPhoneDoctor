package com.smartphonedoctor.di

import android.content.Context
import com.smartphonedoctor.data.DataCollector
import com.smartphonedoctor.domain.HealthScoreCalculator
import com.smartphonedoctor.domain.PhoneHealthAnalyzer
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
    fun provideDataCollector(@ApplicationContext context: Context): DataCollector {
        return DataCollector(context)
    }

    @Provides
    @Singleton
    fun provideHealthScoreCalculator(): HealthScoreCalculator {
        return HealthScoreCalculator()
    }

    @Provides
    @Singleton
    fun providePhoneHealthAnalyzer(calculator: HealthScoreCalculator): PhoneHealthAnalyzer {
        return PhoneHealthAnalyzer(calculator)
    }
}
