
package com.englishmaster.app

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
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

    private val blue = Color.rgb(20, 115, 245)
    private val darkBlue = Color.rgb(20, 48, 125)
    private val green = Color.rgb(35, 190, 35)
    private val orange = Color.rgb(255, 155, 15)
    private val purple = Color.rgb(155, 40, 230)
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

    private fun roundedBackground(
        colors: IntArray,
        radius: Int,
        strokeColor: Int = Color.TRANSPARENT,
        strokeWidth: Int = 0
    ): GradientDrawable {

        return GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            colors
        ).apply {
            cornerRadius = dp(radius).toFloat()

            if (strokeWidth > 0) {
                setStroke(dp(strokeWidth), strokeColor)
            }
        }
    }

    // A simple smiling yellow star mascot drawn directly in Android.
    private class StarMascotView(context: Context) : View(context) {

        private val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(255, 205, 20)
            style = Paint.Style.FILL
            setShadowLayer(7f, 0f, 4f, 0x66000000)
        }

        private val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(235, 145, 0)
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }

        private val facePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(35, 40, 65)
            style = Paint.Style.FILL
        }

        private val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val cx = width / 2f
            val cy = height / 2f
            val outer = minOf(width, height) * 0.46f
            val inner = outer * 0.48f

            val path = Path()

            for (i in 0 until 10) {
                val angle = Math.PI * i / 5.0 - Math.PI / 2.0
                val radius = if (i % 2 == 0) outer else inner

                val x = cx + (radius * Math.cos(angle)).toFloat()
                val y = cy + (radius * Math.sin(angle)).toFloat()

                if (i == 0) path.moveTo(x, y)
                else path.lineTo(x, y)
            }

            path.close()

            canvas.drawPath(path, starPaint)
            canvas.drawPath(path, edgePaint)

            canvas.drawCircle(
                cx - outer * 0.25f,
                cy - outer * 0.05f,
                outer * 0.09f,
                whitePaint
            )

            canvas.drawCircle(
                cx + outer * 0.25f,
                cy - outer * 0.05f,
                outer * 0.09f,
                whitePaint
            )

            canvas.drawCircle(
                cx - outer * 0.25f,
                cy - outer * 0.04f,
                outer * 0.045f,
                facePaint
            )

            canvas.drawCircle(
                cx + outer * 0.25f,
                cy - outer * 0.04f,
                outer * 0.045f,
                facePaint
            )

            val smile = Path()
            smile.moveTo(cx - outer * 0.23f, cy + outer * 0.20f)
            smile.quadTo(
                cx,
                cy + outer * 0.48f,
                cx + outer * 0.23f,
                cy + outer * 0.20f
            )

            val smilePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(145, 35, 20)
                style = Paint.Style.STROKE
                strokeWidth = outer * 0.075f
                strokeCap = Paint.Cap.ROUND
            }

            canvas.drawPath(smile, smilePaint)
        }
    }

    private fun createPage(
        title: String,
        subtitle: String
    ): LinearLayout {

        root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL

        // Programmatic sky-to-garden background.
        root.background = roundedBackground(
            intArrayOf(
                Color.rgb(35, 160, 255),
                Color.rgb(145, 220, 255),
                Color.rgb(205, 245, 180)
            ),
            0
        )

        val scroll = ScrollView(this)
        scroll.isFillViewport = true
        scroll.overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS

        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.gravity = Gravity.CENTER_HORIZONTAL
        content.setPadding(
            dp(14), dp(12), dp(14), dp(24)
        )

        // Brand header.
        val header = LinearLayout(this)
        header.orientation = LinearLayout.HORIZONTAL
        header.gravity = Gravity.CENTER_VERTICAL
        header.setPadding(
            dp(10), dp(8), dp(10), dp(8)
        )

        header.background = roundedBackground(
            intArrayOf(
                Color.rgb(180, 105, 35),
                Color.rgb(120, 65, 25)
            ),
            23,
            Color.rgb(255, 205, 100),
            3
        )

        header.elevation = dp(7).toFloat()

        val mascot = StarMascotView(this)
        header.addView(
            mascot,
            LinearLayout.LayoutParams(dp(67), dp(67))
        )

        val brandColumn = LinearLayout(this)
        brandColumn.orientation = LinearLayout.VERTICAL
        brandColumn.gravity = Gravity.CENTER
        brandColumn.setPadding(dp(5), 0, dp(5), 0)

        val brand = TextView(this)
        brand.text = "EnglishMaster"
        brand.textSize = 23f
        brand.setTextColor(Color.WHITE)
        brand.typeface = Typeface.DEFAULT_BOLD
        brand.gravity = Gravity.CENTER
        brand.setShadowLayer(3f, 2f, 3f, Color.BLACK)

        val kids = TextView(this)
        kids.text = "K I D S"
        kids.textSize = 20f
        kids.setTextColor(Color.YELLOW)
        kids.typeface = Typeface.DEFAULT_BOLD
        kids.gravity = Gravity.CENTER
        kids.setShadowLayer(3f, 2f, 3f, Color.BLACK)

        brandColumn.addView(brand)
        brandColumn.addView(kids)

        header.addView(
            brandColumn,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        if (title != "Home") {
            val home = Button(this)
            home.text = "⌂"
            home.textSize = 27f
            home.setTextColor(Color.WHITE)
            home.background = roundedBackground(
                intArrayOf(
                    Color.rgb(65, 180, 255),
                    Color.rgb(0, 85, 220)
                ),
                35,
                Color.WHITE,
                2
            )
            home.setOnClickListener {
                showHome()
            }

            header.addView(
                home,
                LinearLayout.LayoutParams(dp(52), dp(52))
            )
        }

        content.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(space(12))

        val subtitleView = TextView(this)
        subtitleView.text = subtitle
        subtitleView.textSize = 18f
        subtitleView.setTextColor(darkBlue)
        subtitleView.typeface = Typeface.DEFAULT_BOLD
        subtitleView.gravity = Gravity.CENTER
        subtitleView.setShadowLayer(1f, 1f, 1f, Color.WHITE)

        content.addView(subtitleView)
        content.addView(space(10))

        scroll.addView(content)
        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )

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

        view.setPadding(dp(4), dp(7), dp(4), dp(7))
        parent.addView(view)
    }

    private fun makeButton(
        text: String,
        color: Int,
        action: () -> Unit
    ): Button {

        val button = Button(this)
        button.text = text
        button.textSize = 17f
        button.isAllCaps = false
        button.minHeight = 0
        button.minWidth = 0
        button.setTextColor(Color.WHITE)
        button.typeface = Typeface.DEFAULT_BOLD
        button.gravity = Gravity.CENTER
        button.setPadding(
            dp(5), dp(5), dp(5), dp(5)
        )

        val colors = when (color) {
            green -> intArrayOf(
                Color.rgb(145, 255, 55),
                Color.rgb(35, 195, 25),
                Color.rgb(0, 125, 15)
            )

            orange -> intArrayOf(
                Color.rgb(255, 220, 65),
                Color.rgb(255, 150, 10),
                Color.rgb(220, 75, 0)
            )

            purple -> intArrayOf(
                Color.rgb(225, 130, 255),
                Color.rgb(160, 45, 235),
                Color.rgb(100, 15, 165)
            )

            else -> intArrayOf(
                Color.rgb(100, 215, 255),
                Color.rgb(20, 120, 250),
                Color.rgb(0, 55, 180)
            )
        }

        button.background = roundedBackground(
            colors,
            30,
            Color.WHITE,
            2
        )

        button.elevation = dp(6).toFloat()

        button.setOnClickListener {
            action()
        }

        return button
    }

    private fun addButton(
        parent: LinearLayout,
        text: String,
        color: Int = blue,
        action: () -> Unit
    ) {
        val button = makeButton(text, color, action)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(58)
        )

        params.setMargins(0, dp(5), 0, dp(5))
        parent.addView(button, params)
    }

    private fun addButtonRow(
        parent: LinearLayout,
        firstText: String,
        firstColor: Int,
        firstAction: () -> Unit,
        secondText: String,
        secondColor: Int,
        secondAction: () -> Unit
    ) {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER
        row.weightSum = 2f

        val first = makeButton(
            firstText,
            firstColor,
            firstAction
        )

        val second = makeButton(
            secondText,
            secondColor,
            secondAction
        )

        val firstParams = LinearLayout.LayoutParams(
            0,
            dp(60),
            1f
        )
        firstParams.setMargins(dp(3), dp(5), dp(5), dp(5))

        val secondParams = LinearLayout.LayoutParams(
            0,
            dp(60),
            1f
        )
        secondParams.setMargins(dp(5), dp(5), dp(3), dp(5))

        row.addView(first, firstParams)
        row.addView(second, secondParams)

        parent.addView(
            row,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun showHome() {
        val page = createPage(
            "Home",
            "Learn English. Play. Have fun!"
        )

        addText(
            page,
            "Welcome, Little Learner!",
            24f,
            darkBlue,
            true
        )

        addText(
            page,
            "Choose your adventure",
            20f,
            darkBlue,
            true
        )

        addButton(page, "🍎  Fruits", green) {
            currentIndex = 0
            showFruitPage()
        }

        addButton(page, "⭐  Practice Fruit Quiz", orange) {
            startFruitQuiz()
        }

        val completed = prefs.getInt("fruit_completed", 0)
        val best = prefs.getInt("fruit_best_score", 0)

        addText(
            page,
            "Your Learning Journey",
            22f,
            darkBlue,
            true
        )

        addText(page, "Completed fruit lessons: $completed")
        addText(page, "Best fruit quiz score: $best / 5")

        addButton(page, "My Progress", purple) {
            showProgress()
        }
    }

    private fun addFruitProgress(
        page: LinearLayout,
        number: Int,
        total: Int
    ) {
        val panel = LinearLayout(this)
        panel.orientation = LinearLayout.VERTICAL
        panel.gravity = Gravity.CENTER
        panel.setPadding(
            dp(12), dp(7), dp(12), dp(7)
        )

        panel.background = roundedBackground(
            intArrayOf(
                Color.rgb(255, 250, 225),
                Color.rgb(255, 230, 180)
            ),
            24,
            Color.rgb(255, 180, 70),
            2
        )

        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL

        val label = TextView(this)
        label.text = "Word $number of $total"
        label.textSize = 20f
        label.setTextColor(darkBlue)
        label.typeface = Typeface.DEFAULT_BOLD
        label.gravity = Gravity.CENTER

        row.addView(
            label,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        val stars = TextView(this)
        stars.text = "★ ★ ★"
        stars.textSize = 20f
        stars.setTextColor(Color.rgb(255, 165, 0))
        stars.typeface = Typeface.DEFAULT_BOLD
        row.addView(stars)

        panel.addView(row)

        val progress = ProgressBar(
            this,
            null,
            android.R.attr.progressBarStyleHorizontal
        )

        progress.max = total
        progress.progress = number
        progress.progressTintList = ColorStateList.valueOf(
            Color.rgb(45, 205, 35)
        )
        progress.progressBackgroundTintList =
            ColorStateList.valueOf(
                Color.rgb(185, 200, 230)
            )

        val progressParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(17)
        )
        progressParams.setMargins(0, dp(7), 0, dp(5))

        panel.addView(progress, progressParams)

        page.addView(
            panel,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun showFruitPage() {
        if (currentIndex !in fruits.indices) return

        val word = fruits[currentIndex]
        val number = currentIndex + 1
        val total = fruits.size

        val page = createPage(
            "Fruits",
            "Let's discover a delicious fruit!"
        )

        addFruitProgress(page, number, total)
        page.addView(space(12))

        // Main fruit card.
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.gravity = Gravity.CENTER
        card.setPadding(
            dp(10), dp(12), dp(10), dp(12)
        )

        card.background = roundedBackground(
            intArrayOf(
                Color.WHITE,
                Color.rgb(250, 253, 255)
            ),
            32,
            Color.rgb(20, 130, 255),
            4
        )

        card.elevation = dp(9).toFloat()

        val picture = ImageView(this)
        picture.setImageResource(word.imageRes)

        // Preserve the complete image without stretching or cropping.
        picture.scaleType = ImageView.ScaleType.FIT_CENTER
        picture.adjustViewBounds = true
        picture.contentDescription = word.english
        picture.setPadding(
            dp(5), dp(5), dp(5), dp(5)
        )

        card.addView(
            picture,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(275)
            )
        )

        val name = TextView(this)
        name.text = word.english
        name.textSize = 38f
        name.setTextColor(darkBlue)
        name.typeface = Typeface.create(
            "sans-serif-rounded",
            Typeface.BOLD
        )
        name.gravity = Gravity.CENTER
        name.setPadding(
            dp(3), dp(7), dp(3), dp(12)
        )

        card.addView(name)

        page.addView(
            card,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        page.addView(space(10))

        addText(
            page,
            "Listen and repeat!",
            20f,
            darkBlue,
            true
        )

        addButtonRow(
            page,
            "🔊 Listen",
            blue,
            { speak(word.english) },
            "↻ Listen Again",
            purple,
            { speak(word.english) }
        )

        val nextText =
            if (currentIndex == total - 1) {
                "Finish Lesson  ✓"
            } else {
                "Next  ➜"
            }

        addButtonRow(
            page,
            "⬅ Previous",
            blue,
            {
                if (currentIndex > 0) {
                    currentIndex--
                    showFruitPage()
                } else {
                    Toast.makeText(
                        this,
                        "This is the first fruit!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            nextText,
            green,
            {
                if (currentIndex < total - 1) {
                    currentIndex++
                    showFruitPage()
                } else {
                    val count = prefs.getInt(
                        "fruit_completed",
                        0
                    ) + 1

                    prefs.edit()
                        .putInt("fruit_completed", count)
                        .apply()

                    showLessonComplete()
                }
            }
        )

        addButton(page, "Practice Quiz", orange) {
            startFruitQuiz()
        }

        addButton(page, "Back to Home", darkBlue) {
            showHome()
        }
    }

    private fun showLessonComplete() {
        val page = createPage(
            "Complete",
            "You completed the fruit lesson!"
        )

        addText(
            page,
            "★  Amazing Work!  ★",
            29f,
            green,
            true
        )

        addText(
            page,
            "You learned ${fruits.size} fruits!",
            22f,
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
        picture.adjustViewBounds = true
        picture.contentDescription = correct.english

        page.addView(
            picture,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(250)
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

        addButton(page, "🔊 Listen", green) {
            speak(correct.english)
        }

        addButton(page, "Home", darkBlue) {
            showHome()
        }
    }

    private fun showQuizResult() {
        val page = createPage(
            "Quiz Complete",
            "You did a great job!"
        )

        addText(
            page,
            "$quizScore / ${quizQuestions.size}",
            48f,
            green,
            true
        )

        val best = prefs.getInt(
            "fruit_best_score",
            0
        )

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

        addText(
            page,
            message,
            21f,
            darkBlue,
            true
        )

        addButton(page, "Try Again", green) {
            startFruitQuiz()
        }

        addButton(page, "Home", blue) {
            showHome()
        }
    }

    private fun showProgress() {
        val page = createPage(
            "Progress",
            "Every step makes you better!"
        )

        val completed = prefs.getInt(
            "fruit_completed",
            0
        )

        val best = prefs.getInt(
            "fruit_best_score",
            0
        )

        addText(
            page,
            "Fruit Learning Statistics",
            22f,
            darkBlue,
            true
        )

        addText(
            page,
            "Completed lessons: $completed"
        )

        addText(
            page,
            "Best quiz score: $best / 5"
        )

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
