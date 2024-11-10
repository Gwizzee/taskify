package com.taskify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.lifecycle.lifecycleScope
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var editTextTask: EditText
    private lateinit var buttonAdd: Button
    private lateinit var listViewTasks: ListView

    private val tasks = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTask = findViewById(R.id.editTextTask)
        buttonAdd = findViewById(R.id.buttonAdd)
        listViewTasks = findViewById(R.id.listViewTasks)

        @Database(entities = [Task::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,   

                    "task_database"
                ).build()
                INSTANCE = instance
                instance   

            }
        }
    }
}

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tasks)
        listViewTasks.adapter = adapter

        buttonAdd.setOnClickListener {
            val task = editTextTask.text.toString()
            if (task.isNotEmpty()) {
                tasks.add(task)
                adapter.notifyDataSetChanged()
                editTextTask.text.clear()
            }
        }
    }
}
database = AppDatabase.getDatabase(this)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tasks)
        listViewTasks.adapter = adapter

        lifecycleScope.launch {
            val taskList = database.taskDao().getAllTasks()
            tasks.addAll(taskList.map { it.task }) // Add existing tasks from database
            adapter.notifyDataSetChanged()
        }

        buttonAdd.setOnClickListener {
            val taskText = editTextTask.text.toString()
            if (taskText.isNotEmpty()) {
                val task = Task(task = taskText)
                lifecycleScope.launch {
                    database.taskDao().insert(task)
                    tasks.add(taskText)
                    adapter.notifyDataSetChanged()
                }
                editTextTask.text.clear()
            }
        }

        listViewTasks.setOnItemLongClickListener { _, _, position, _ ->
            val taskToDelete = tasks[position]
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes")   
 { _, _ ->
                    lifecycleScope.launch {
                        val taskEntity = database.taskDao().getAllTasks().find { it.task == taskToDelete }
                        if (taskEntity != null) {
                            database.taskDao().delete(taskEntity)
                        }
                        tasks.removeAt(position)
                        adapter.notifyDataSetChanged()
                    }
                }
                .setNegativeButton("No", null)
                .create()
            alertDialog.show()
            true
        }
    }
}
listViewTasks.setOnItemLongClickListener { _, _, position, _ ->
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes")   
 { _, _ ->
                    tasks.removeAt(position)
                    adapter.notifyDataSetChanged()
                }
                .setNegativeButton("No", null)
                .create()
            alertDialog.show()
            true
        }
    }
}
