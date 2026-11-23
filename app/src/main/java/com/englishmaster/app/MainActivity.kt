
package com.englishmaster.app

import android.app.Activity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class MainActivity : Activity() {

    private var tts: TextToSpeech? = null
    private var ttsReady = false

    // One reusable banner for all pages
    private var bannerAd: AdView? = null
    private var adsInitialized = false

    private val prefs by lazy {
        getSharedPreferences("english_master_data", MODE_PRIVATE)
    }

    private val blue = Color.rgb(45, 115, 245)
    private val navy = Color.rgb(35, 55, 115)
    private val green = Color.rgb(40, 190, 105)
    private val orange = Color.rgb(255, 155, 45)
    private val purple = Color.rgb(155, 90, 230)
    private val pink = Color.rgb(245, 105, 155)
    private val ink = Color.rgb(45, 55, 85)

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

        MobileAds.initialize(this) {
            runOnUiThread {
                adsInitialized = true
            }
        }

        window.statusBarColor = Color.rgb(39, 75, 160)
        window.navigationBarColor = Color.rgb(35, 55, 115)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

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

    // Create the banner only once
    private fun createBannerAd(): AdView {
        return AdView(this).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = "ca-app-pub-3940256099942544/6300978111"
            loadAd(AdRequest.Builder().build())
        }
    }

    // Reuse the same banner on every page
    private fun getBannerAd(): AdView {
        if (bannerAd == null) {
            bannerAd = createBannerAd()
        }
        return bannerAd!!
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun speak(word: String) {
        if (ttsReady) {
            tts?.speak(
                word,
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

    private fun gradient(
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

    private fun space(height: Int): View {
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                1,
                dp(height)
            )
        }
    }

    private fun makeText(
        value: String,
        size: Float = 18f,
        color: Int = ink,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            gravity = Gravity.CENTER
            includeFontPadding = true
            if (bold) {
                typeface = Typeface.create(
                    "sans-serif-rounded",
                    Typeface.BOLD
                )
            }
            setPadding(dp(5), dp(7), dp(5), dp(7))
        }
    }

    private fun addText(
        parent: LinearLayout,
        value: String,
        size: Float = 18f,
        color: Int = ink,
        bold: Boolean = false
    ) {
        parent.addView(makeText(value, size, color, bold))
    }

    private fun makeCard(
        radius: Int = 28,
        background: Int = Color.WHITE
    ): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(14), dp(14), dp(14), dp(14))
            this.background = gradient(
                intArrayOf(background, Color.rgb(250, 252, 255)),
                radius,
                Color.WHITE,
                2
            )
            elevation = dp(8).toFloat()
        }
    }

    private fun addCard(
        parent: LinearLayout,
        card: View,
        top: Int = 8,
        bottom: Int = 8
    ) {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, dp(top), 0, dp(bottom))
        parent.addView(card, params)
    }

    private fun makeButton(
        label: String,
        color: Int,
        action: () -> Unit
    ): Button {
        val shades = when (color) {
            green -> intArrayOf(
                Color.rgb(115, 240, 155),
                Color.rgb(35, 185, 105),
                Color.rgb(20, 135, 80)
            )

            orange -> intArrayOf(
                Color.rgb(255, 215, 105),
                Color.rgb(255, 150, 45),
                Color.rgb(225, 100, 20)
            )

            purple -> intArrayOf(
                Color.rgb(205, 155, 255),
                Color.rgb(145, 85, 225),
                Color.rgb(100, 50, 180)
            )

            pink -> intArrayOf(
                Color.rgb(255, 165, 195),
                Color.rgb(240, 100, 155),
                Color.rgb(205, 65, 125)
            )

            else -> intArrayOf(
                Color.rgb(120, 195, 255),
                Color.rgb(55, 125, 245),
                Color.rgb(35, 75, 190)
            )
        }

        return Button(this).apply {
            text = label
            textSize = 16f
            isAllCaps = false
            minHeight = 0
            minWidth = 0
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(5), dp(8), dp(5))
            background = gradient(
                shades,
                22,
                Color.WHITE,
                2
            )
            elevation = dp(5).toFloat()
            setOnClickListener { action() }
        }
    }

    private fun addButton(
        parent: LinearLayout,
        label: String,
        color: Int = blue,
        action: () -> Unit
    ) {
        val button = makeButton(label, color, action)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(57)
        )
        params.setMargins(0, dp(6), 0, dp(6))
        parent.addView(button, params)
    }

    private fun addButtonRow(
        parent: LinearLayout,
        firstLabel: String,
        firstColor: Int,
        firstAction: () -> Unit,
        secondLabel: String,
        secondColor: Int,
        secondAction: () -> Unit
    ) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val first = makeButton(firstLabel, firstColor, firstAction)
        val second = makeButton(secondLabel, secondColor, secondAction)

        val firstParams = LinearLayout.LayoutParams(
            0,
            dp(57),
            1f
        )
        firstParams.setMargins(0, dp(5), dp(5), dp(5))

        val secondParams = LinearLayout.LayoutParams(
            0,
            dp(57),
            1f
        )
        secondParams.setMargins(dp(5), dp(5), 0, dp(5))

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

    private fun createPage(
        title: String,
        subtitle: String
    ): LinearLayout {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            fitsSystemWindows = true
            background = gradient(
                intArrayOf(
                    Color.rgb(120, 205, 255),
                    Color.rgb(205, 235, 255),
                    Color.rgb(235, 250, 225)
                ),
                0
            )
        }

        val scroll = ScrollView(this).apply {
            isFillViewport = true
            clipToPadding = false
            overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(17), dp(14), dp(17), dp(28))
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(13), dp(12), dp(13), dp(12))
            background = gradient(
                intArrayOf(
                    Color.rgb(95, 145, 255),
                    Color.rgb(75, 75, 205),
                    Color.rgb(65, 55, 160)
                ),
                27,
                Color.WHITE,
                2
            )
            elevation = dp(9).toFloat()
        }

        val mascot = TextView(this).apply {
            text = "⭐"
            textSize = 42f
            gravity = Gravity.CENTER
            setShadowLayer(5f, 2f, 4f, Color.rgb(30, 35, 90))
        }

        header.addView(
            mascot,
            LinearLayout.LayoutParams(dp(65), dp(65))
        )

        val brandColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        brandColumn.addView(
            makeText(
                "EnglishMaster",
                22f,
                Color.WHITE,
                true
            )
        )

        brandColumn.addView(
            makeText(
                "LEARN • PLAY • GROW",
                11f,
                Color.rgb(255, 230, 130),
                true
            )
        )

        header.addView(
            brandColumn,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        if (title != "Home") {
            val home = makeButton("⌂", blue) {
                showHome()
            }
            header.addView(
                home,
                LinearLayout.LayoutParams(dp(52), dp(50))
            )
        }

        content.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(space(13))

        val subtitleCard = makeCard(22)
        subtitleCard.addView(
            makeText(
                subtitle,
                17f,
                navy,
                true
            )
        )
        addCard(content, subtitleCard, 2, 10)

        scroll.addView(content)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        // Reuse the same AdView instead of creating a new one
        val ad = getBannerAd()

        (ad.parent as? ViewGroup)?.removeView(ad)

        root.addView(
            ad,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
        )

        setContentView(root)
        return content
    }

    private fun showHome() {
        val page = createPage(
            "Home",
            "Welcome to your learning adventure!"
        )

        val hero = makeCard(30)
        hero.background = gradient(
            intArrayOf(
                Color.rgb(255, 255, 255),
                Color.rgb(225, 245, 255)
            ),
            30,
            Color.WHITE,
            2
        )

        hero.addView(makeText("🌈", 48f))
        hero.addView(
            makeText(
                "Hello, Little Learner!",
                24f,
                navy,
                true
            )
        )
        hero.addView(
            makeText(
                "Let's learn English in a fun way!",
                15f,
                ink
            )
        )
        addCard(page, hero, 8, 12)

        addText(
            page,
            "CHOOSE YOUR ADVENTURE",
            17f,
            navy,
            true
        )

        val fruitCard = makeCard(25)
        fruitCard.background = gradient(
            intArrayOf(
                Color.rgb(150, 245, 175),
                Color.rgb(60, 200, 130)
            ),
            25
        )
        fruitCard.addView(makeText("🍎 🍌 🍇", 35f))
        fruitCard.addView(
            makeText("Fruits Adventure", 21f, Color.WHITE, true)
        )
        fruitCard.addView(
            makeText("Discover 16 delicious fruits", 14f, Color.WHITE)
        )
        fruitCard.setOnClickListener {
            currentIndex = 0
            showFruitPage()
        }
        addCard(page, fruitCard, 8, 8)

        val quizCard = makeCard(25)
        quizCard.background = gradient(
            intArrayOf(
                Color.rgb(255, 205, 115),
                Color.rgb(255, 145, 65)
            ),
            25
        )
        quizCard.addView(makeText("🏆 ⭐ 🎯", 35f))
        quizCard.addView(
            makeText("Fruit Challenge", 21f, Color.WHITE, true)
        )
        quizCard.addView(
            makeText("Test what you have learned", 14f, Color.WHITE)
        )
        quizCard.setOnClickListener {
            startFruitQuiz()
        }
        addCard(page, quizCard, 8, 8)

        val completed = prefs.getInt("fruit_completed", 0)
        val best = prefs.getInt("fruit_best_score", 0)

        val stats = makeCard(25)
        stats.addView(
            makeText("🏅 YOUR LEARNING JOURNEY", 18f, navy, true)
        )
        stats.addView(
            makeText("Completed lessons: $completed", 17f, ink, true)
        )
        stats.addView(
            makeText("Best quiz score: $best / 5", 17f, ink, true)
        )
        addCard(page, stats, 12, 8)

        addButton(page, "📊  My Progress", purple) {
            showProgress()
        }
    }

    private fun addFruitProgress(
        page: LinearLayout,
        number: Int,
        total: Int
    ) {
        val panel = makeCard(22)
        panel.background = gradient(
            intArrayOf(
                Color.rgb(255, 255, 255),
                Color.rgb(225, 255, 230)
            ),
            22,
            Color.rgb(100, 215, 140),
            2
        )

        panel.addView(
            makeText(
                "FRUIT $number / $total     ⭐ ⭐ ⭐",
                18f,
                navy,
                true
            )
        )

        val progress = ProgressBar(
            this,
            null,
            android.R.attr.progressBarStyleHorizontal
        ).apply {
            max = total
            this.progress = number
            progressTintList =
                android.content.res.ColorStateList.valueOf(green)
            progressBackgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    Color.rgb(210, 225, 225)
                )
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(15)
        )
        params.setMargins(dp(5), dp(8), dp(5), dp(5))
        panel.addView(progress, params)

        addCard(page, panel, 4, 8)
    }

    private fun showFruitPage() {
        if (currentIndex !in fruits.indices) return

        val word = fruits[currentIndex]
        val number = currentIndex + 1
        val total = fruits.size

        val page = createPage(
            "Fruits",
            "Explore, learn and say each fruit!"
        )

        addFruitProgress(page, number, total)

        val card = makeCard(32)
        card.background = gradient(
            intArrayOf(
                Color.WHITE,
                Color.rgb(245, 250, 255)
            ),
            32,
            Color.rgb(255, 190, 80),
            3
        )

        card.addView(
            makeText("✨ LOOK AND LEARN ✨", 14f, orange, true)
        )

        val picture = ImageView(this).apply {
            setImageResource(word.imageRes)
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
            contentDescription = word.english
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }

        card.addView(
            picture,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(235)
            )
        )

        card.addView(
            makeText(word.english, 34f, navy, true)
        )

        card.addView(
            makeText("Listen carefully and repeat!", 14f, ink)
        )

        addCard(page, card, 8, 10)

        addButtonRow(
            page,
            "🔊 Listen",
            blue,
            { speak(word.english) },
            "↻ Listen Again",
            purple,
            { speak(word.english) }
        )

        val nextLabel =
            if (currentIndex == total - 1) "Finish ✓" else "Next ➜"

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
            nextLabel,
            green,
            {
                if (currentIndex < total - 1) {
                    currentIndex++
                    showFruitPage()
                } else {
                    val count = prefs.getInt("fruit_completed", 0) + 1

                    prefs.edit()
                        .putInt("fruit_completed", count)
                        .apply()

                    showLessonComplete()
                }
            }
        )

        addButton(page, "🏆 Practice Quiz", orange) {
            startFruitQuiz()
        }

        addButton(page, "⌂ Back to Home", navy) {
            showHome()
        }
    }

    private fun showLessonComplete() {
        val page = createPage(
            "Complete",
            "You completed the fruit lesson!"
        )

        val card = makeCard(30)
        card.background = gradient(
            intArrayOf(
                Color.rgb(180, 255, 195),
                Color.rgb(70, 210, 135)
            ),
            30
        )
        card.addView(makeText("🎉 🏆 🎉", 48f))
        card.addView(
            makeText("Amazing Work!", 28f, Color.WHITE, true)
        )
        card.addView(
            makeText(
                "You learned ${fruits.size} fruits!",
                19f,
                Color.WHITE,
                true
            )
        )
        addCard(page, card, 12, 15)

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
            "Quiz",
            "Question ${quizIndex + 1} of ${quizQuestions.size}"
        )

        val card = makeCard(28)
        card.addView(
            makeText(
                "Which word matches this picture?",
                19f,
                navy,
                true
            )
        )

        val picture = ImageView(this).apply {
            setImageResource(correct.imageRes)
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
            contentDescription = correct.english
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }

        card.addView(
            picture,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(205)
            )
        )

        addCard(page, card, 8, 12)

        val options = (
            listOf(correct) +
            fruits.filter {
                it.english != correct.english
            }.shuffled().take(3)
        ).shuffled()

        options.forEachIndexed { index, option ->
            val colors = listOf(blue, green, orange, purple)
            addButton(
                page,
                "${index + 1}.  ${option.english}",
                colors[index]
            ) {
                if (option.english == correct.english) {
                    quizScore++
                    Toast.makeText(
                        this,
                        "Great job! ⭐",
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

        addButton(page, "🔊 Listen", pink) {
            speak(correct.english)
        }

        addButton(page, "⌂ Home", navy) {
            showHome()
        }
    }

    private fun showQuizResult() {
        val page = createPage(
            "Quiz Result",
            "You did a great job!"
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

        val card = makeCard(30)
        card.background = gradient(
            intArrayOf(
                Color.rgb(255, 230, 145),
                Color.rgb(255, 175, 90)
            ),
            30
        )

        card.addView(makeText("🏆 ⭐ 🎉", 45f))
        card.addView(
            makeText(
                "$quizScore / ${quizQuestions.size}",
                48f,
                Color.WHITE,
                true
            )
        )
        card.addView(
            makeText(message, 19f, Color.WHITE, true)
        )
        addCard(page, card, 12, 15)

        addButton(page, "Try Again", green) {
            startFruitQuiz()
        }

        addButton(page, "My Progress", purple) {
            showProgress()
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

        val completed = prefs.getInt("fruit_completed", 0)
        val best = prefs.getInt("fruit_best_score", 0)

        val card = makeCard(28)
        card.addView(makeText("📊 🏅 ⭐", 43f))
        card.addView(
            makeText("Your Statistics", 24f, navy, true)
        )
        card.addView(
            makeText("Completed lessons: $completed", 19f, ink, true)
        )
        card.addView(
            makeText("Best quiz score: $best / 5", 19f, ink, true)
        )
        addCard(page, card, 12, 12)

        addButton(page, "Practice Quiz", green) {
            startFruitQuiz()
        }

        addButton(page, "Learn Fruits", orange) {
            currentIndex = 0
            showFruitPage()
        }

        addButton(page, "Home", blue) {
            showHome()
        }
    }

    override fun onPause() {
        bannerAd?.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        bannerAd?.resume()
    }

    override fun onDestroy() {
        bannerAd?.destroy()
        bannerAd = null

        tts?.stop()
        tts?.shutdown()
        tts = null

        super.onDestroy()
    }
}
