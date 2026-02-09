package com.sibsutis.astronomyhandbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibsutis.astronomyhandbook.model.News
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class NewsViewModel : ViewModel() {
    private val _newsList = MutableStateFlow<List<News>>(emptyList())
    val newsList: StateFlow<List<News>> = _newsList.asStateFlow()

    private val _displayedNews = MutableStateFlow<List<News>>(emptyList())
    val displayedNews: StateFlow<List<News>> = _displayedNews.asStateFlow()

    init {
        initializeNews()
        startNewsRotation()
    }

    private fun initializeNews() {
        val allNews = listOf(
            News(1, "Новость 1", "Открыта новая экзопланета в созвездии Лебедя..."),
            News(2, "Новость 2", "Марсоход Perseverance обнаружил следы древней воды..."),
            News(3, "Новость 3", "Телескоп Джеймс Уэбб сделал новые снимки Юпитера..."),
            News(4, "Новость 4", "Ученые обнаружили воду на экзопланете в зоне обитаемости..."),
            News(5, "Новость 5", "Запуск новой лунной миссии запланирован на 2024 год..."),
            News(6, "Новость 6", "Астрономы нашли самую далекую галактику..."),
            News(7, "Новость 7", "Солнечная активность достигла пика..."),
            News(8, "Новость 8", "Новая теория о темной материи..."),
            News(9, "Новость 9", "Обнаружен астероид, приближающийся к Земле..."),
            News(10, "Новость 10", "Международная космическая станция отметила юбилей...")
        )
        _newsList.value = allNews
        _displayedNews.value = allNews.take(4)
    }

    fun likeNews(id: Int) {
        _displayedNews.value = _displayedNews.value.map { news ->
            if (news.id == id) news.copy(likes = news.likes + 1) else news
        }
    }

    private fun startNewsRotation() {
        viewModelScope.launch {
            while (true) {
                delay(5000L)
                rotateNews()
            }
        }
    }

    private fun rotateNews() {
        val current = _displayedNews.value.toMutableList()
        val all = _newsList.value

        // Исправлено: проверка размера current
        if (current.isNotEmpty()) {
            val randomIndex = Random.nextInt(0, current.size)
            val availableNews = all.filter { it !in current }

            if (availableNews.isNotEmpty()) {
                current[randomIndex] = availableNews.random()
                _displayedNews.value = current
            }
        }
    }
}