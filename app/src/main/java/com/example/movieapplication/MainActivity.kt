package com.example.movieapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    //Инициализация переменных
    private lateinit var reqText: EditText
    private lateinit var buttonSearch: Button
    private lateinit var buttonHistory: Button
    private lateinit var resultView: TextView

    private val dbManager = DbManager(this) //Инициализация бд-менеджера

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Привязка переменных к объектам активити
        reqText = findViewById(R.id.reqText)
        buttonSearch = findViewById(R.id.buttonSearch)
        buttonHistory = findViewById(R.id.buttonHistory)
        resultView = findViewById(R.id.resultView)

        buttonSearch.setOnClickListener { //Слушатель кнопки поиска
            if (reqText.text.isEmpty()){ //Если текстовое поле пустое, то закончить выполнение функции
                Toast.makeText(this, "Поле пустое", Toast.LENGTH_SHORT).show() //Тост о пустом полу
                return@setOnClickListener //Конец выполнения слушателя
            }
            else{
                Toast.makeText(this, "Поиск", Toast.LENGTH_SHORT).show() //Тост о поиске
                var title = reqText.text.toString() //Запись текста из textview в переменную

                title = title.replace(" ", "%20", false) //Замена пробелов в тексте на символ "%20" для корректной работы api
                reqText.setText("") //Очистка поля ввода
                val thread = Thread{  //Открытие потока
                    try {
                        //Работа с api
                        val client = OkHttpClient()

                        val request = Request.Builder()
                            .url("https://imdb-data-searching.p.rapidapi.com/om?t=${title}") //Запрос с фильмом
                            .get()
                            .addHeader("X-RapidAPI-Host", "imdb-data-searching.p.rapidapi.com")
                            .addHeader("X-RapidAPI-Key", "8fac8d93edmshc4380d7d88505cdp17d5dfjsndf4c3b2501a4")
                            .build()

                        val response = client.newCall(request).execute() //Отправка запроса
                        val result = response.body()?.string() //Получение результатов в видо json файла
                        var error = JSONObject(result).getString("Response").toBoolean() //Считывания объекта Response

                        if(!error){ //Если error = false
                            resultView.text = "Ошибка поиска!"
                        }
                        else{//Если error = true
                            //Считывание по заголовкам данных из результата запроса
                            var title = JSONObject(result).getString("Title")
                            var year = JSONObject(result).getString("Year")
                            var plot = JSONObject(result).getString("Plot")
                            var language = JSONObject(result).getString("Language")
                            var imdbRating = JSONObject(result).getString("imdbRating")
                            var totalSeasons = JSONObject(result).getString("totalSeasons")

                            //Формирование строки итогового результата
                            var res = "Title: $title" +
                                    "\nYear: $year" +
                                    "\nPlot: $plot" +
                                    "\nLanguage: $language" +
                                    "\nimdbRating: $imdbRating" +
                                    "\ntotalSeasons: $totalSeasons"

                            runOnUiThread { //Возврат в основной поток
                                resultView.text = res //Запись в текстовое поле результата
                            }

                            dbManager.openDb() //Открытие бд
                            dbManager.insertToDb(res) //Запись
                            dbManager.closeDb() //Закрытие бд
                        }
                    }
                    catch (e: Exception){
                        e.printStackTrace()
                    }
                }
                thread.start() //Открытие потока
            }
        }

        buttonHistory.setOnClickListener {//Слушатель кнопки истории
            val i = Intent(this, History::class.java) //Инициализация интента для открытия новой активити
            startActivity(i) //Старт активити
        }
    }
}