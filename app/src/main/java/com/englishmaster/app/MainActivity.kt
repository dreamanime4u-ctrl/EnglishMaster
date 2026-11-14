
package com.englishmaster.app

import android.app.Activity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var root: LinearLayout
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    private val prefs by lazy {
        getSharedPreferences("english_master_data", MODE_PRIVATE)
    }

    private val blue = Color.rgb(55, 115, 230)
    private val darkBlue = Color.rgb(35, 65, 125)
    private val pageColor = Color.rgb(244, 248, 255)
    private val green = Color.rgb(65, 180, 120)
    private val orange = Color.rgb(255, 165, 65)
    private val purple = Color.rgb(145, 105, 220)
    private val textColor = Color.rgb(45, 55, 75)

    private data class Word(
        val emoji: String,
        val english: String
    )

    private data class Category(
        val title: String,
        val emoji: String,
        val words: List<Word>
    )

    private val alphabet = Category(
        "Alphabet", "🔤",
        listOf(
            Word("🍎", "A - Apple"),
            Word("⚽", "B - Ball"),
            Word("🐱", "C - Cat"),
            Word("🐶", "D - Dog"),
            Word("🥚", "E - Egg"),
            Word("🐟", "F - Fish"),
            Word("🍇", "G - Grapes"),
            Word("🏠", "H - House"),
            Word("🍦", "I - Ice cream"),
            Word("🧃", "J - Juice")
        )
    )

    private val numbers = Category(
        "Numbers", "🔢",
        listOf(
            Word("1️⃣", "One"),
            Word("2️⃣", "Two"),
            Word("3️⃣", "Three"),
            Word("4️⃣", "Four"),
            Word("5️⃣", "Five"),
            Word("6️⃣", "Six"),
            Word("7️⃣", "Seven"),
            Word("8️⃣", "Eight"),
            Word("9️⃣", "Nine"),
            Word("🔟", "Ten")
        )
    )

    private val colors = Category(
        "Colors", "🎨",
        listOf(
            Word("🔴", "Red"),
            Word("🔵", "Blue"),
            Word("🟡", "Yellow"),
            Word("🟢", "Green"),
            Word("🟠", "Orange"),
            Word("🟣", "Purple"),
            Word("⚫", "Black"),
            Word("⚪", "White"),
            Word("🟤", "Brown"),
            Word("🩷", "Pink")
        )
    )

    private val shapes = Category(
        "Shapes", "🔷",
        listOf(
            Word("⚪", "Circle"),
            Word("🟥", "Square"),
            Word("🔺", "Triangle"),
            Word("▭", "Rectangle"),
            Word("⭐", "Star"),
            Word("❤️", "Heart"),
            Word("⬡", "Hexagon"),
            Word("🔶", "Diamond")
        )
    )

    private val animals = Category(
        "Animals", "🐾",
        listOf(
            Word("🐶", "Dog"),
            Word("🐱", "Cat"),
            Word("🐰", "Rabbit"),
            Word("🦁", "Lion"),
            Word("🐘", "Elephant"),
            Word("🐒", "Monkey"),
            Word("🦒", "Giraffe"),
            Word("🐼", "Panda"),
            Word("🐯", "Tiger"),
            Word("🐻", "Bear"),
            Word("🦊", "Fox"),
            Word("🐸", "Frog"),
            Word("🐵", "Chimpanzee"),
            Word("🐴", "Horse"),
            Word("🐮", "Cow")
        )
    )

    private val fruits = Category(
        "Fruits", "🍓",
        listOf(
            Word("🍎", "Apple"),
            Word("🍌", "Banana"),
            Word("🍊", "Orange"),
            Word("🍇", "Grapes"),
            Word("🍓", "Strawberry"),
            Word("🍉", "Watermelon"),
            Word("🍍", "Pineapple"),
            Word("🥭", "Mango"),
            Word("🍐", "Pear"),
            Word("🍑", "Peach"),
            Word("🍒", "Cherry"),
            Word("🥝", "Kiwi"),
            Word("🍋", "Lemon"),
            Word("🥥", "Coconut"),
            Word("🫐", "Blueberry")
        )
    )

    private val vegetables = Category(
        "Vegetables", "🥕",
        listOf(
            Word("🥕", "Carrot"),
            Word("🥔", "Potato"),
            Word("🍅", "Tomato"),
            Word("🥒", "Cucumber"),
            Word("🥬", "Lettuce"),
            Word("🌽", "Corn"),
            Word("🧅", "Onion"),
            Word("🧄", "Garlic"),
            Word("🥦", "Broccoli"),
            Word("🌶️", "Pepper"),
            Word("🍆", "Eggplant"),
            Word("🫛", "Peas"),
            Word("🥑", "Avocado"),
            Word("🍠", "Sweet potato"),
            Word("🥗", "Spinach")
        )
    )

    private val food = Category(
        "Food", "🍽️",
        listOf(
            Word("🍞", "Bread"),
            Word("🧀", "Cheese"),
            Word("🥚", "Egg"),
            Word("🍚", "Rice"),
            Word("🍗", "Chicken"),
            Word("🍕", "Pizza"),
            Word("🍔", "Burger"),
            Word("🥛", "Milk")
        )
    )

    private val body = Category(
        "Body Parts", "👦",
        listOf(
            Word("👁️", "Eye"),
            Word("👂", "Ear"),
            Word("👃", "Nose"),
            Word("👄", "Mouth"),
            Word("✋", "Hand"),
            Word("🦶", "Foot"),
            Word("🦵", "Leg"),
            Word("💪", "Arm")
        )
    )

    private val allCategories = listOf(
        alphabet, numbers, colors, shapes,
        animals, fruits, vegetables, food, body
    )

    private var selectedCategory: Category? = null
    private var quizIndex = 0
    private var quizScore = 0
    private var quizQuestions: List<Word> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                ttsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }

        showHome()
    }

    private fun speak(text: String) {
        if (ttsReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "english_word")
        } else {
            Toast.makeText(
                this,
                "Speech is not ready yet.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun createPage(title: String, subtitle: String): LinearLayout {
        root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(pageColor)

        val scroll = ScrollView(this)
        scroll.isFillViewport = true

        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(18), dp(18), dp(18), dp(24))

        val header = LinearLayout(this)
        header.orientation = LinearLayout.VERTICAL
        header.gravity = Gravity.CENTER
        header.setPadding(dp(18), dp(24), dp(18), dp(24))
        header.setBackgroundColor(blue)

        val heading = TextView(this)
        heading.text = title
        heading.textSize = 27f
        heading.setTextColor(Color.WHITE)
        heading.typeface = Typeface.DEFAULT_BOLD
        heading.gravity = Gravity.CENTER

        val description = TextView(this)
        description.text = subtitle
        description.textSize = 15f
        description.setTextColor(Color.WHITE)
        description.gravity = Gravity.CENTER
        description.setPadding(0, dp(8), 0, 0)

        header.addView(heading)
        header.addView(description)

        content.addView(header)
        content.addView(space(12))

        scroll.addView(content)
        root.addView(scroll)

        setContentView(root)
        return content
    }

    private fun space(height: Int): View {
        val view = View(this)
        view.layoutParams = LinearLayout.LayoutParams(
            1,
            dp(height)
        )
        return view
    }

    private fun addText(
        parent: LinearLayout,
        text: String,
        size: Float = 18f,
        color: Int = textColor,
        bold: Boolean = false
    ) {
        val view = TextView(this)
        view.text = text
        view.textSize = size
        view.setTextColor(color)
        view.gravity = Gravity.CENTER_VERTICAL
        if (bold) view.typeface = Typeface.DEFAULT_BOLD

        view.setPadding(dp(4), dp(8), dp(4), dp(8))
        parent.addView(view)
    }

    private fun addButton(
        parent: LinearLayout,
        text: String,
        color: Int = blue,
        action: () -> Unit
    ) {
        val button = Button(this)
        button.text = text
        button.textSize = 15f
        button.isAllCaps = false
        button.setTextColor(Color.WHITE)
        button.setBackgroundColor(color)
        button.setOnClickListener { action() }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(54)
        )
        params.setMargins(0, dp(5), 0, dp(5))
        parent.addView(button, params)
    }

    private fun addWordCard(parent: LinearLayout, word: Word) {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.HORIZONTAL
        card.gravity = Gravity.CENTER_VERTICAL
        card.setPadding(dp(14), dp(10), dp(14), dp(10))
        card.setBackgroundColor(Color.WHITE)

        val emoji = TextView(this)
        emoji.text = word.emoji
        emoji.textSize = 32f
        emoji.gravity = Gravity.CENTER

        card.addView(
            emoji,
            LinearLayout.LayoutParams(dp(65), dp(60))
        )

        val name = TextView(this)
        name.text = word.english
        name.textSize = 19f
        name.typeface = Typeface.DEFAULT_BOLD
        name.setTextColor(darkBlue)

        card.addView(
            name,
            LinearLayout.LayoutParams(0, dp(60), 1f)
        )

        val sound = Button(this)
        sound.text = "🔊"
        sound.textSize = 18f
        sound.isAllCaps = false
        sound.setOnClickListener { speak(word.english) }

        card.addView(
            sound,
            LinearLayout.LayoutParams(dp(65), dp(55))
        )

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, dp(5), 0, dp(5))
        parent.addView(card, params)
    }

    private fun showHome() {
        val page = createPage(
            "🌟 EnglishMaster Kids",
            "Learn English. Play. Have fun!"
        )

        addText(page, "Choose Your Learning Group", 21f, darkBlue, true)

        addButton(page, "🐣 Little Learners (Ages 4–6)", green) {
            showGroup("Little Learners", "Ages 4–6")
        }

        addButton(page, "🚀 Junior Learners (Ages 7–9)", orange) {
            showGroup("Junior Learners", "Ages 7–9")
        }

        addButton(page, "🏆 Young Learners (Ages 10–12)", purple) {
            showGroup("Young Learners", "Ages 10–12")
        }

        addText(page, "Explore English", 21f, darkBlue, true)

        for (category in allCategories) {
            addButton(
                page,
                "${category.emoji}  ${category.title}",
                blue
            ) {
                showLesson(category)
            }
        }

        addText(page, "Your Learning Journey", 20f, darkBlue, true)

        val completed = prefs.getInt("completed_lessons", 0)
        val bestScore = prefs.getInt("quiz_score", 0)

        addText(page, "Completed lessons: $completed")
        addText(page, "Best quiz score: $bestScore")

        addButton(page, "🎮 Start a Quiz", green) {
            startQuiz()
        }

        addButton(page, "📊 My Progress", purple) {
            showProgress()
        }
    }

    private fun showGroup(group: String, age: String) {
        val page = createPage(
            group,
            "Your English adventure starts here!"
        )

        addText(page, age, 19f, darkBlue, true)

        val groupDescription = when (group) {
            "Little Learners" ->
                "Learn first words, letters, numbers and colors."
            "Junior Learners" ->
                "Build vocabulary and learn useful everyday English."
            else ->
                "Improve vocabulary, reading and everyday communication."
        }

        addText(page, groupDescription, 16f)

        addText(page, "Choose a lesson", 21f, darkBlue, true)

        val categories = when (group) {
            "Little Learners" ->
                listOf(alphabet, numbers, colors, shapes, animals, fruits)
            "Junior Learners" ->
                listOf(animals, fruits, vegetables, food, body, colors, numbers)
            else ->
                listOf(food, body, animals, fruits, vegetables, alphabet, shapes)
        }

        for (category in categories) {
            addButton(page, "${category.emoji}  ${category.title}") {
                showLesson(category)
            }
        }

        addButton(page, "🎮 Practice Quiz", green) {
            startQuiz()
        }

        addButton(page, "⬅ Home", darkBlue) {
            showHome()
        }
    }

    private fun showLesson(category: Category) {
        selectedCategory = category

        val page = createPage(
            "${category.emoji} ${category.title}",
            "Look, listen and repeat!"
        )

        addText(page, "Listen to each word and say it aloud.", 16f)

        for (word in category.words) {
            addWordCard(page, word)
        }

        addButton(page, "🔊 Listen to All Words", green) {
            speak(category.words.joinToString(", ") { it.english })
        }

        addButton(page, "🎮 Practice This Topic", orange) {
            startQuiz(category)
        }

        addButton(page, "✓ Complete Lesson", purple) {
            val count = prefs.getInt("completed_lessons", 0) + 1
            prefs.edit().putInt("completed_lessons", count).apply()
            Toast.makeText(
                this,
                "Lesson completed!",
                Toast.LENGTH_SHORT
            ).show()
            showHome()
        }

        addButton(page, "⬅ Home", darkBlue) {
            showHome()
        }
    }

    private fun startQuiz(category: Category? = null) {
        val source = category ?: allCategories.random()
        quizQuestions = source.words.shuffled().take(5)
        quizIndex = 0
        quizScore = 0
        showQuizQuestion()
    }

    private fun showQuizQuestion() {
        if (quizIndex >= quizQuestions.size) {
            showQuizResult()
            return
        }

        val correctWord = quizQuestions[quizIndex]
        val page = createPage(
            "🎮 Word Challenge",
            "Question ${quizIndex + 1} of ${quizQuestions.size}"
        )

        addText(page, "Which word matches this picture?", 20f, darkBlue, true)

        addText(page, correctWord.emoji, 65f, darkBlue, true)

        val options = (listOf(correctWord) +
                allCategories.flatMap { it.words }
                    .filter { it.english != correctWord.english }
                    .shuffled()
                    .take(3)
                ).shuffled()

        for (option in options) {
            addButton(page, option.english, blue) {
                if (option.english == correctWord.english) {
                    quizScore++
                    Toast.makeText(
                        this,
                        "Great job!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Keep practicing!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                quizIndex++
                showQuizQuestion()
            }
        }

        addButton(page, "🔊 Listen", green) {
            speak(correctWord.english)
        }

        addButton(page, "⬅ Home", darkBlue) {
            showHome()
        }
    }

    private fun showQuizResult() {
        val page = createPage(
            "🏆 Quiz Complete!",
            "You did a great job!"
        )

        addText(
            page,
            "$quizScore / ${quizQuestions.size}",
            42f,
            green,
            true
        )

        val best = prefs.getInt("quiz_score", 0)
        if (quizScore > best) {
            prefs.edit().putInt("quiz_score", quizScore).apply()
        }

        val message = when {
            quizScore == quizQuestions.size -> "Perfect score! Amazing!"
            quizScore >= 3 -> "Well done! Keep learning!"
            else -> "Good try! Practice makes progress!"
        }

        addText(page, message, 19f, darkBlue, true)

        addButton(page, "🔄 Try Again", green) {
            startQuiz(selectedCategory)
        }

        addButton(page, "🏠 Home", blue) {
            showHome()
        }
    }

    private fun showProgress() {
        val page = createPage(
            "📊 My Progress",
            "Every step makes you better!"
        )

        val completed = prefs.getInt("completed_lessons", 0)
        val bestScore = prefs.getInt("quiz_score", 0)

        addText(page, "⭐ Learning Statistics", 22f, darkBlue, true)
        addText(page, "Completed lessons: $completed", 19f)
        addText(page, "Best quiz score: $bestScore / 5", 19f)

        addText(page, "Keep practicing every day!", 18f, green, true)

        addButton(page, "🎮 Play a Quiz", orange) {
            startQuiz()
        }

        addButton(page, "⬅ Home", blue) {
            showHome()
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
