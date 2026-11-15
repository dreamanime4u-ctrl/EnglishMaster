
package com.englishmaster.app

import android.app.Activity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
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
    private val green = Color.rgb(65, 185, 85)
    private val orange = Color.rgb(255, 165, 45)
    private val purple = Color.rgb(145, 105, 220)
    private val textColor = Color.rgb(45, 55, 75)

    private data class Word(
        val english: String,
        val imageRes: Int
    )

    private val fruits = listOf(
        Word("Apple", R.drawable.fruit_apple),
        Word("Banana", R.drawable.fruit_banana),
        Word("Orange", R.drawable.fruit_orange),
        Word("Grapes", R.drawable.fruit_grapes),
        Word("Strawberry", R.drawable.fruit_strawberry),
        Word("Watermelon", R.drawable.fruit_watermelon),
        Word("Pineapple", R.drawable.fruit_pineapple),
        Word("Mango", R.drawable.fruit_mango),
        Word("Pear", R.drawable.fruit_pear),
        Word("Peach", R.drawable.fruit_peach),
        Word("Cherry", R.drawable.fruit_cherry),
        Word("Lemon", R.drawable.fruit_lemon),
        Word("Coconut", R.drawable.fruit_coconut),
        Word("Kiwi", R.drawable.fruit_kiwi),
        Word("Blueberry", R.drawable.fruit_blueberry),
        Word("Blackberry", R.drawable.fruit_blackberry)
    )

    private var currentIndex = 0
    private var quizIndex = 0
    private var quizScore = 0
    private var quizQuestions: List<Word> = emptyList()

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

    private fun speak(word: String) {
        if (ttsReady) {
            tts?.speak(
                word,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "fruit_word"
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
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                1,
                dp(height)
            )
        }
    }

    private fun createPage(
        title: String,
        subtitle: String
    ): LinearLayout {

        root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundResource(R.drawable.kids_background)

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
            dp(15), dp(22), dp(15), dp(22)
        )

        val headerBackground = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.rgb(75, 175, 255),
                Color.rgb(25, 90, 210)
            )
        )
        headerBackground.cornerRadius = dp(25).toFloat()
        header.background = headerBackground

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
        button.textSize = 18f
        button.isAllCaps = false
        button.setTextColor(Color.WHITE)
        button.typeface = Typeface.DEFAULT_BOLD
        button.gravity = Gravity.CENTER
        button.setPadding(
            dp(12), dp(8), dp(12), dp(8)
        )

        val buttonColors = when (color) {
            green -> intArrayOf(
                Color.rgb(130, 255, 40),
                Color.rgb(25, 190, 20),
                Color.rgb(0, 125, 15)
            )
            orange -> intArrayOf(
                Color.rgb(255, 220, 35),
                Color.rgb(255, 150, 0),
                Color.rgb(225, 85, 0)
            )
            purple -> intArrayOf(
                Color.rgb(220, 125, 255),
                Color.rgb(155, 45, 230),
                Color.rgb(95, 15, 165)
            )
            else -> intArrayOf(
                Color.rgb(80, 205, 255),
                Color.rgb(20, 115, 250),
                Color.rgb(0, 55, 180)
            )
        }

        val background = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            buttonColors
        )

        background.cornerRadius = dp(30).toFloat()
        background.setStroke(dp(3), Color.WHITE)

        button.background = background
        button.elevation = dp(7).toFloat()

        button.setOnClickListener {
            action()
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(62)
        )
        params.setMargins(0, dp(7), 0, dp(7))
        parent.addView(button, params)
    }

    private fun showHome() {
        val page = createPage(
            "EnglishMaster Kids",
            "Learn English. Play. Have fun!"
        )

        addText(
            page,
            "Welcome, Little Learner!",
            23f,
            darkBlue,
            true
        )

        addText(
            page,
            "Choose your adventure",
            19f,
            darkBlue,
            true
        )

        addButton(page, "Fruits", green) {
            currentIndex = 0
            showFruitPage()
        }

        addButton(page, "Practice Fruit Quiz", orange) {
            startFruitQuiz()
        }

        val completed = prefs.getInt("fruit_completed", 0)
        val best = prefs.getInt("fruit_best_score", 0)

        addText(
            page,
            "Your Learning Journey",
            21f,
            darkBlue,
            true
        )

        addText(page, "Completed fruit lessons: $completed")
        addText(page, "Best fruit quiz score: $best / 5")

        addButton(page, "My Progress", purple) {
            showProgress()
        }
    }

    private fun showFruitPage() {
        if (currentIndex !in fruits.indices) return

        val word = fruits[currentIndex]
        val number = currentIndex + 1
        val total = fruits.size

        val page = createPage(
            "Fruits",
            "Learn one fruit at a time!"
        )

        addText(
            page,
            "Fruit $number of $total",
            19f,
            darkBlue,
            true
        )

        addText(
            page,
            "${number * 100 / total}% completed",
            16f,
            green,
            true
        )

        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.gravity = Gravity.CENTER
        card.setPadding(
            dp(12), dp(12), dp(12), dp(12)
        )

        val cardBackground = GradientDrawable()
        cardBackground.setColor(Color.WHITE)
        cardBackground.cornerRadius = dp(25).toFloat()
        cardBackground.setStroke(dp(3), Color.rgb(100, 190, 255))
        card.background = cardBackground
        card.elevation = dp(5).toFloat()

        val picture = ImageView(this)
        picture.setImageResource(word.imageRes)
        picture.scaleType = ImageView.ScaleType.FIT_CENTER
        picture.adjustViewBounds = true
        picture.contentDescription = word.english

        card.addView(
            picture,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(235)
            )
        )

        val name = TextView(this)
        name.text = word.english
        name.textSize = 31f
        name.setTextColor(darkBlue)
        name.typeface = Typeface.DEFAULT_BOLD
        name.gravity = Gravity.CENTER
        name.setPadding(0, dp(12), 0, dp(12))

        card.addView(name)

        page.addView(
            card,
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

        addButton(page, "Listen to the Word", blue) {
            speak(word.english)
        }

        addButton(page, "Listen Again", purple) {
            speak(word.english)
        }

        if (currentIndex > 0) {
            addButton(page, "Previous", blue) {
                currentIndex--
                showFruitPage()
            }
        }

        if (currentIndex < total - 1) {
            addButton(page, "Next", green) {
                currentIndex++
                showFruitPage()
            }
        } else {
            addButton(page, "Finish Lesson", green) {
                val count = prefs.getInt("fruit_completed", 0) + 1
                prefs.edit()
                    .putInt("fruit_completed", count)
                    .apply()

                showLessonComplete()
            }
        }

        addButton(page, "Practice Quiz", orange) {
            startFruitQuiz()
        }

        addButton(page, "Back to Home", darkBlue) {
            showHome()
        }
    }

    private fun showLessonComplete() {
        val page = createPage(
            "Amazing Work!",
            "You completed the fruit lesson!"
        )

        addText(page, "Great job!", 28f, green, true)
        addText(
            page,
            "You learned ${fruits.size} fruits!",
            21f,
            darkBlue,
            true
        )

        addButton(page, "Take a Quiz", green) {
            startFruitQuiz()
        }

        addButton(page, "Learn Again", orange) {
            currentIndex = 0
            showFruitPage()
        }

        addButton(page, "Home", blue) {
            showHome()
        }
    }

    private fun startFruitQuiz() {
        quizQuestions = fruits.shuffled().take(5)
        quizIndex = 0
        quizScore = 0
        showQuizQuestion()
    }

    private fun showQuizQuestion() {
        if (quizIndex >= quizQuestions.size) {
            showQuizResult()
            return
        }

        val correct = quizQuestions[quizIndex]

        val page = createPage(
            "Fruit Quiz",
            "Question ${quizIndex + 1} of ${quizQuestions.size}"
        )

        addText(
            page,
            "Which word matches this picture?",
            20f,
            darkBlue,
            true
        )

        val picture = ImageView(this)
        picture.setImageResource(correct.imageRes)
        picture.scaleType = ImageView.ScaleType.FIT_CENTER

        page.addView(
            picture,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(210)
            )
        )

        val options = (
            listOf(correct) +
            fruits.filter {
                it.english != correct.english
            }.shuffled().take(3)
        ).shuffled()

        for (option in options) {
            addButton(page, option.english, blue) {
                if (option.english == correct.english) {
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

        addButton(page, "Listen", green) {
            speak(correct.english)
        }

        addButton(page, "Home", darkBlue) {
            showHome()
        }
    }

    private fun showQuizResult() {
        val page = createPage(
            "Quiz Complete!",
            "You did a great job!"
        )

        addText(
            page,
            "$quizScore / ${quizQuestions.size}",
            45f,
            green,
            true
        )

        val best = prefs.getInt("fruit_best_score", 0)

        if (quizScore > best) {
            prefs.edit()
                .putInt("fruit_best_score", quizScore)
                .apply()
        }

        val message = when {
            quizScore == 5 -> "Perfect score! Amazing!"
            quizScore >= 3 -> "Well done! Keep learning!"
            else -> "Good try! Practice makes progress!"
        }

        addText(page, message, 20f, darkBlue, true)

        addButton(page, "Try Again", green) {
            startFruitQuiz()
        }

        addButton(page, "Home", blue) {
            showHome()
        }
    }

    private fun showProgress() {
        val page = createPage(
            "My Progress",
            "Every step makes you better!"
        )

        val completed = prefs.getInt("fruit_completed", 0)
        val best = prefs.getInt("fruit_best_score", 0)

        addText(
            page,
            "Fruit Learning Statistics",
            21f,
            darkBlue,
            true
        )

        addText(page, "Completed lessons: $completed")
        addText(page, "Best quiz score: $best / 5")

        addButton(page, "Practice Quiz", green) {
            startFruitQuiz()
        }

        addButton(page, "Home", blue) {
            showHome()
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
