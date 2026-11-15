
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

    private val blue = Color.rgb(40, 115, 235)
    private val darkBlue = Color.rgb(35, 65, 125)
    private val pageColor = Color.rgb(235, 247, 255)
    private val green = Color.rgb(65, 185, 85)
    private val orange = Color.rgb(255, 165, 45)
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

    private var currentCategory: Category? = null
    private var currentWordIndex = 0

    private var quizIndex = 0
    private var quizScore = 0
    private var quizQuestions: List<Word> = emptyList()

    private val alphabet = Category(
        "Alphabet", "🔤",
        listOf(
            Word("🅰️", "A - Apple"),
            Word("🅱️", "B - Ball"),
            Word("🌙", "C - Cat"),
            Word("🐶", "D - Dog"),
            Word("🥚", "E - Egg"),
            Word("🐟", "F - Fish"),
            Word("🍇", "G - Grapes"),
            Word("🏠", "H - House"),
            Word("🍦", "I - Ice cream"),
            Word("🧃", "J - Juice"),
            Word("🪁", "K - Kite"),
            Word("🦁", "L - Lion"),
            Word("🌙", "M - Moon"),
            Word("🪺", "N - Nest"),
            Word("🐙", "O - Octopus"),
            Word("✏️", "P - Pencil"),
            Word("👑", "Q - Queen"),
            Word("🌈", "R - Rainbow"),
            Word("☀️", "S - Sun"),
            Word("🐢", "T - Turtle"),
            Word("☂️", "U - Umbrella"),
            Word("🚐", "V - Van"),
            Word("⌚", "W - Watch"),
            Word("❌", "X - X-ray"),
            Word("🪀", "Y - Yo-yo"),
            Word("🦓", "Z - Zebra")
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
            Word("🔟", "Ten"),
            Word("1️⃣1️⃣", "Eleven"),
            Word("1️⃣2️⃣", "Twelve"),
            Word("1️⃣3️⃣", "Thirteen"),
            Word("1️⃣4️⃣", "Fourteen"),
            Word("1️⃣5️⃣", "Fifteen"),
            Word("1️⃣6️⃣", "Sixteen"),
            Word("1️⃣7️⃣", "Seventeen"),
            Word("1️⃣8️⃣", "Eighteen"),
            Word("1️⃣9️⃣", "Nineteen"),
            Word("2️⃣0️⃣", "Twenty")
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
            Word("🐴", "Horse"),
            Word("🐮", "Cow"),
            Word("🐧", "Penguin")
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                ttsReady =
                    result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }

        showHome()
    }

    private fun speak(text: String) {
        if (ttsReady) {
            tts?.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "english_word"
            )
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

    private fun space(height: Int): View {
        val view = View(this)
        view.layoutParams = LinearLayout.LayoutParams(1, dp(height))
        return view
    }

    private fun createPage(
        title: String,
        subtitle: String
    ): LinearLayout {

        root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(pageColor)

        val scroll = ScrollView(this)
        scroll.isFillViewport = true

        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(
            dp(18), dp(18), dp(18), dp(25)
        )

        val header = LinearLayout(this)
        header.orientation = LinearLayout.VERTICAL
        header.gravity = Gravity.CENTER
        header.setPadding(
            dp(18), dp(24), dp(18), dp(24)
        )
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
        content.addView(space(15))

        scroll.addView(content)
        root.addView(scroll)

        setContentView(root)
        return content
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
        view.gravity = Gravity.CENTER

        if (bold) {
            view.typeface = Typeface.DEFAULT_BOLD
        }

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
        button.textSize = 16f
        button.isAllCaps = false
        button.setTextColor(Color.WHITE)
        button.setBackgroundColor(color)
        button.setOnClickListener { action() }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(55)
        )

        params.setMargins(0, dp(5), 0, dp(5))
        parent.addView(button, params)
    }

    private fun showHome() {
        val page = createPage(
            "🌟 EnglishMaster Kids",
            "Learn English. Play. Have fun!"
        )

        addText(
            page,
            "Choose Your Learning Group",
            21f,
            darkBlue,
            true
        )

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
                "${category.emoji}  ${category.title}"
            ) {
                showLesson(category)
            }
        }

        val completed = prefs.getInt("completed_lessons", 0)
        val bestScore = prefs.getInt("quiz_score", 0)

        addText(page, "Your Learning Journey", 20f, darkBlue, true)
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

        val description = when (group) {
            "Little Learners" ->
                "Learn first words, letters, numbers and colors."
            "Junior Learners" ->
                "Build vocabulary and learn useful everyday English."
            else ->
                "Improve vocabulary, reading and everyday communication."
        }

        addText(page, description, 16f)
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
            addButton(page, "${category.emoji} ${category.title}") {
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

    // One learning item per screen

    private fun showLesson(category: Category) {
        currentCategory = category
        currentWordIndex = 0
        showWordPage()
    }

    private fun showWordPage() {
        val category = currentCategory ?: return
        val word = category.words[currentWordIndex]
        val total = category.words.size
        val number = currentWordIndex + 1

        val page = createPage(
            "${category.emoji} ${category.title}",
            "Learn one word at a time!"
        )

        addText(
            page,
            "Word $number of $total",
            18f,
            darkBlue,
            true
        )

        addText(
            page,
            "⭐  ${number * 100 / total}% completed",
            15f,
            green,
            true
        )

        // Large illustration area

        val pictureCard = LinearLayout(this)
        pictureCard.orientation = LinearLayout.VERTICAL
        pictureCard.gravity = Gravity.CENTER
        pictureCard.setPadding(
            dp(12), dp(18), dp(12), dp(18)
        )
        pictureCard.setBackgroundColor(Color.WHITE)

        val picture = TextView(this)
        picture.text = word.emoji
        picture.textSize = 100f
        picture.gravity = Gravity.CENTER

        pictureCard.addView(
            picture,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(210)
            )
        )

        val wordName = TextView(this)
        wordName.text = word.english
        wordName.textSize = 29f
        wordName.setTextColor(darkBlue)
        wordName.typeface = Typeface.DEFAULT_BOLD
        wordName.gravity = Gravity.CENTER
        wordName.setPadding(0, dp(12), 0, dp(12))

        pictureCard.addView(wordName)

        page.addView(
            pictureCard,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        addText(
            page,
            "Listen and repeat!",
            20f,
            darkBlue,
            true
        )

        addButton(page, "🔊 Listen to the Word", blue) {
            speak(word.english)
        }

        addButton(page, "🎤 Listen Again", purple) {
            speak(word.english)
        }

        page.addView(space(12))

        // Navigation

        if (currentWordIndex > 0) {
            addButton(page, "⬅ Previous", blue) {
                currentWordIndex--
                showWordPage()
            }
        }

        if (currentWordIndex < total - 1) {
            addButton(page, "Next ➜", green) {
                currentWordIndex++
                showWordPage()
            }
        } else {
            addButton(page, "🏆 Finish Lesson", green) {
                val count = prefs.getInt("completed_lessons", 0) + 1
                prefs.edit()
                    .putInt("completed_lessons", count)
                    .apply()

                Toast.makeText(
                    this,
                    "Amazing! Lesson completed!",
                    Toast.LENGTH_LONG
                ).show()

                showLessonComplete()
            }
        }

        addButton(page, "🎮 Practice Quiz", orange) {
            startQuiz(category)
        }

        addButton(page, "⬅ Back to Home", darkBlue) {
            showHome()
        }
    }

    private fun showLessonComplete() {
        val page = createPage(
            "🏆 Amazing Work!",
            "You completed this lesson!"
        )

        addText(page, "⭐ ⭐ ⭐", 45f)
        addText(
            page,
            "Great job! Keep learning!",
            22f,
            darkBlue,
            true
        )

        addButton(page, "🎮 Take a Quiz", green) {
            startQuiz(currentCategory)
        }

        addButton(page, "🔄 Learn Again", orange) {
            currentCategory?.let { showLesson(it) }
        }

        addButton(page, "🏠 Home", blue) {
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

        addText(
            page,
            "Which word matches this picture?",
            20f,
            darkBlue,
            true
        )

        addText(page, correctWord.emoji, 85f, darkBlue, true)

        val options = (
            listOf(correctWord) +
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
            prefs.edit()
                .putInt("quiz_score", quizScore)
                .apply()
        }

        val message = when {
            quizScore == quizQuestions.size ->
                "Perfect score! Amazing!"
            quizScore >= 3 ->
                "Well done! Keep learning!"
            else ->
                "Good try! Practice makes progress!"
        }

        addText(page, message, 19f, darkBlue, true)

        addButton(page, "🔄 Try Again", green) {
            startQuiz(currentCategory)
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

        addText(
            page,
            "⭐ Learning Statistics",
            22f,
            darkBlue,
            true
        )

        addText(page, "Completed lessons: $completed", 19f)
        addText(page, "Best quiz score: $bestScore / 5", 19f)

        addText(
            page,
            "Keep practicing every day!",
            18f,
            green,
            true
        )

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
