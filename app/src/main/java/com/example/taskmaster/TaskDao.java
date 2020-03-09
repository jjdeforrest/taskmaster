package com.example.taskmaster;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task WHERE id = :id")
    public Task getOneTask(long id);

    @Query("SELECT * FROM task")
    public List<Task> getTasks();

    @Insert
    public void addTask(Task task);



}
