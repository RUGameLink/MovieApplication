package com.example.movieapplication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class History : AppCompatActivity() {
    private val dbManager = DbManager(this) //Инициализация бд-менеджера

    private lateinit var deleteButton: Button
    private lateinit var searchResButton: ImageButton
    private lateinit var searchResText: EditText
    private lateinit var resultText: TextView
    private lateinit var refreshButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        deleteButton = findViewById(R.id.deleteButton)
        searchResButton = findViewById(R.id.searchResButton)
        searchResText = findViewById(R.id.searchResText)
        resultText = findViewById(R.id.resultText)
        refreshButton = findViewById(R.id.refreshButton)

        readText()

        searchResButton.setOnClickListener {
            findText()
        }

        deleteButton.setOnClickListener {
            deleteFromDb()
            readText()
        }

        refreshButton.setOnClickListener {
            readText()
        }
    }

    private fun findText(){ //СЛУШАТЕЛЬ КНОПКИ ПОИСКА
        if (searchResText.text.isEmpty()){ //ПРОВЕРКА НА ПУСТОТУ ПОЛЯ ВВОДА
            Toast.makeText(this, "Введите текст для поиска", Toast.LENGTH_SHORT).show()
            return
        }
        else{
            dbManager.openDb()
            val resList = dbManager.searchInDb(searchResText.text.toString()) //ПОИСК В БД
            dbManager.closeDb()
            setAdapter(resList) //ОБНОВЛЕНИЕ РЕСАЙКЛЕР ВЬЮ
            if (resList.isEmpty()){ //ПРОВЕРКА РЕЗУЛЬТАТА ПОИСКА
                resultText.text = ""
                resultText.text = "По вашему запросу ничего не найдено"
            }
            else{
                resultText.text = ""
            }
        }
    }

    private fun deleteFromDb(){ //ФУНКЦИЯ ОЧИСТКИ БД
        dbManager.openDb()
        println("check del ${dbManager.deleteFromDb()}")
        dbManager.closeDb()
    }

    private fun readText(){
        dbManager.openDb() //Открытие бд
        val historySongData = dbManager.readDbDataTitles() //Считывание из бд колонки имен в лист
        dbManager.closeDb() //Закрытие бд
        setAdapter(historySongData)
    }

    private fun setAdapter(historySongData: ArrayList<String>){ //МЕТОД ОБНОВЛЕНИЯ АДАПТЕРА РЕСАЙКЛЕРА
        val recyclerView: RecyclerView = findViewById(R.id.itemView) //Подвязка ресайклера к объекту
        val linearLayoutManager = LinearLayoutManager(applicationContext) //Подготовка лайаут менеджера
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager //Инициализация лайаут менеджера
        recyclerView.adapter = Adapter(historySongData!!) //внесение данных из листа в адаптер (заполнение данными)
    }
}