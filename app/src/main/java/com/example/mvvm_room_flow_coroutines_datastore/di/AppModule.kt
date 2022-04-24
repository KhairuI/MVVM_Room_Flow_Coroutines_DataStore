package com.example.mvvm_room_flow_coroutines_datastore.di

import android.app.Application
import androidx.room.Room
import com.example.mvvm_room_flow_coroutines_datastore.db.BookDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app:Application,
        callback:BookDatabase.Callback
    ) = Room.databaseBuilder(app,BookDatabase::class.java,"book_database").
    fallbackToDestructiveMigration().addCallback(callback).build()

    @Provides
    fun provideBookDao(db:BookDatabase)= db.bookDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope()= CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope