package com.example.mvvm_room_flow_coroutines_datastore.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.DateFormat

@Entity(tableName = "book_table")
data class ModelBook(val name:String,
                     val important:Boolean=false,
                     val completed:Boolean=false,
                     val time:Long= System.currentTimeMillis(),
                     @PrimaryKey(autoGenerate = true) val id:Int = 0
):Serializable {
    val createFormatted:String
    get() = DateFormat.getDateInstance().format(time)
}