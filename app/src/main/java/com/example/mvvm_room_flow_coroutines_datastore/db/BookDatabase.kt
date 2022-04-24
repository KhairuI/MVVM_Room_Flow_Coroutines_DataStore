package com.example.mvvm_room_flow_coroutines_datastore.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mvvm_room_flow_coroutines_datastore.di.ApplicationScope
import com.example.mvvm_room_flow_coroutines_datastore.model.ModelBook
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [ModelBook::class],version = 1)
abstract class BookDatabase:RoomDatabase() {

    abstract fun bookDao():BookDAO

    class Callback @Inject constructor(
        private val bookDatabase: Provider<BookDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
        ): RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val dao= bookDatabase.get().bookDao()
            applicationScope.launch {
                dao.insert(ModelBook("The Great Gatsby"))
                dao.insert(ModelBook("Ulysses"))
                dao.insert(ModelBook("War and Peace",important = true))
                dao.insert(ModelBook("The Great Gatsby"))
                dao.insert(ModelBook("Ulysses", completed = true))
                dao.insert(ModelBook("The Great Gatsby"))
            }
        }
    }
}