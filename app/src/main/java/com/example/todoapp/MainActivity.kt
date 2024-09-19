package com.example.todoapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userData =  findViewById<EditText>(R.id.userData)
        val createButton = findViewById<Button>(R.id.buttonCreate)
        val recyclerView = findViewById<RecyclerView>(R.id.listOfItems)

        val allTasks = mutableListOf<Item>()
        var adapter: ItemAdapter? = null

        adapter = ItemAdapter(allTasks) { position ->
            allTasks.removeAt(position)
            adapter?.notifyItemRemoved(position)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        createButton.setOnClickListener {
            val text = userData.text.toString().trim()
            if (text.isEmpty()){
                Toast.makeText(this, "You have not entered any text!", Toast.LENGTH_SHORT).show()
            }
            else{
                val newItem = Item(isChecked = false, taskText = text, id = allTasks.size)
                allTasks.add(newItem)
                adapter.notifyItemRemoved(allTasks.size - 1)
                userData.text.clear()
            }
        }

        fun importFromJsonFile(filePath: String): List<Item>{
            val file = File(filePath)
            if (!file.exists()) return emptyList()

            val jsonString = file.readText()
            val gson = Gson()

            val itemType = object: TypeToken<List<Item>>() {}.type
            return gson.fromJson<List<Item>>(jsonString, itemType)
        }

        val loadButton = findViewById<TextView>(R.id.textImport)
        loadButton.setOnClickListener{
            val filePath = "${filesDir}/tasks.json"
            val loadedTasks = importFromJsonFile(filePath)

            if (loadedTasks != null){
                allTasks.clear()
                allTasks.addAll(loadedTasks)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Tasks loaded from JSON file", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "No file found to load", Toast.LENGTH_SHORT).show()
            }
        }

        fun exportToJsonFile(filePath: String, items: List<Item>){
            val gson = Gson()
            val jsonString = gson.toJson(items)

            val file = File(filePath)
            file.writeText(jsonString)
        }

        val saveButton = findViewById<TextView>(R.id.textSave)
        saveButton.setOnClickListener {
            val filePath = "${filesDir}/tasks.json"
            exportToJsonFile(filePath, allTasks)
            Toast.makeText(this, "Tasks saved to JSON file", Toast.LENGTH_SHORT).show()
        }
    }
}