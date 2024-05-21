package com.example.todoapp.model

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg todo: Todo)

//    @Query("SELECT * FROM todo")
//    fun selectAllTodo(): List<Todo>

//    @Query("SELECT * FROM todo ORDER BY priority DESC")
//     fun selectAllTodo(): List<Todo>

    @Query("SELECT * FROM todo WHERE is_done=:is_done ORDER BY priority DESC")
    fun selectAllTodo(is_done: Int): List<Todo>

    @Query("SELECT * FROM todo WHERE uuid= :id")
    fun selectTodo(id: Int): Todo

    @Delete
    fun deleteTodo(todo: Todo)

    @Query("UPDATE todo SET title=:title, notes=:notes, priority=:priority WHERE uuid = :id")
    fun update(title:String, notes:String, priority:Int, id:Int)

    @Query("UPDATE todo SET is_done=:is_done WHERE uuid =:id")
    fun updateDeleteTodo(is_done: Int, id: Int)
}