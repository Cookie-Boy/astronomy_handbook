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
            News(1, "Сигнал пришельцев или космическая случайность?", "Загадочный сигнал из Проксимы Центавра не удается объяснить естественными причинами..."),
            News(2, "На Марсе нашли органику", "Марсоход Curiosity обнаружил сложные органические молекулы в древних породах..."),
            News(3, "Юпитер преподнес сюрприз", "На снимках Уэбба видны неизвестные ранее структуры в атмосфере газового гиганта..."),
            News(4, "Вода есть везде!", "Новое исследование показывает, что вода может быть распространена во Вселенной гораздо шире, чем считалось..."),
            News(5, "SpaceX теряет корабли", "Два прототипа Starship взорвались во время испытаний на этой неделе..."),
            News(6, "Самая старая галактика", "Уэбб побил рекорд, обнаружив галактику, существовавшую через 300 млн лет после Большого взрыва..."),
            News(7, "Магнитная буря надвигается", "Корональный выброс массы достигнет Земли завтра, ожидаются перебои со связью..."),
            News(8, "Астероид пролетит мимо Земли", "2023 XL размером с автобус пройдет ближе Луны, но угрозы нет..."),
            News(9, "Россия запустит новую станцию", "РОС придет на смену МКС после 2028 года..."),
            News(10, "Телескоп Уэбба сломался?", "Один из режимов работы вышел из строя, но инженеры ищут решение...")
        )
        _newsList.value = allNews
        _displayedNews.value = allNews.take(6)
    }

    fun likeNews(id: Int) {
        _displayedNews.value = _displayedNews.value.map { news ->
            if (news.id == id) news.copy(likes = news.likes + 1) else news
        }

        _newsList.value = _newsList.value.map { news ->
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