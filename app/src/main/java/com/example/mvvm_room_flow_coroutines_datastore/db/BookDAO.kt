package com.example.mvvm_room_flow_coroutines_datastore.db

import androidx.room.*
import com.example.mvvm_room_flow_coroutines_datastore.model.ModelBook
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book:ModelBook)

    @Update
    suspend fun update(book:ModelBook)

    @Delete
    suspend fun delete(book:ModelBook)

    @Query("SELECT * FROM book_table WHERE (completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, name")
    fun getAllBookSortedByName(searchQuery:String,hideCompleted:Boolean): Flow<MutableList<ModelBook>>

    @Query("SELECT * FROM book_table WHERE (completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, time")
    fun getAllBookSortedByDate(searchQuery:String,hideCompleted:Boolean): Flow<MutableList<ModelBook>>

    fun getBook(query:String, sortOrder:SortOrder, hideCompleted:Boolean): Flow<MutableList<ModelBook>> =
        when(sortOrder){
            SortOrder.BY_DATE -> getAllBookSortedByDate(query, hideCompleted)
            SortOrder.BY_NAME -> getAllBookSortedByName(query, hideCompleted)
        }
}

