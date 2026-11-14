
package com.englishmaster.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

class MainActivity : Activity() {

    private val blue = Color.rgb(35, 91, 160)
    private val dark = Color.rgb(30, 45, 65)
    private val pageBackgroundColor = Color.rgb(245, 248, 253)
    private val green = Color.rgb(35, 145, 105)
    private val orange = Color.rgb(230, 145, 45)

    private var tts: TextToSpeech? = null
    private var ttsReady = false

    private var currentLevel = "A1"
    private var quizIndex = 0
    private var quizScore = 0
    private var quizAnswered = false

    private val preferences by lazy {
        getSharedPreferences("english_master_data", MODE_PRIVATE)
    }

    private data class LessonExample(
        val english: String,
        val arabic: String
    )

    private data class Question(
        val question: String,
        val answers: List<String>,
        val correct: Int
    )

    private val questions = listOf(
        Question("What does Apple mean?",
            listOf("كتاب", "تفاحة", "ماء"), 1),
        Question("What does Book mean?",
            listOf("كتاب", "قلم", "باب"), 0),
        Question("How are you?",
            listOf("إلى اللقاء", "كم عمرك؟", "كيف حالك؟"), 2),
        Question("What does Beautiful mean?",
            listOf("سريع", "جميل", "صغير"), 1),
        Question("What does Water mean?",
            listOf("ماء", "طعام", "منزل"), 0)
    )

    private val levelLessons = mapOf(
        "A1" to listOf(
            "التحيات والتعارف",
            "الحروف والأرقام",
            "الأسرة والأصدقاء",
            "الألوان والأشياء",
            "الأيام والوقت"
        ),
        "A2" to listOf(
            "الحياة اليومية",
            "الطعام والشراب",
            "التسوق",
            "وصف الأماكن",
            "الماضي البسيط"
        ),
        "B1" to listOf(
            "التعبير عن الرأي",
            "السفر والعمل",
            "الأزمنة الإنجليزية",
            "المواقف اليومية",
            "كتابة الفقرات"
        ),
        "B2" to listOf(
            "المحادثات المتقدمة",
            "القواعد المتقدمة",
            "التعبير عن الأفكار",
            "القراءة والفهم",
            "الكتابة المتقدمة"
        )
    )

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
            tts?.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "english_master"
            )
        } else {
            Toast.makeText(
                this,
                "خدمة النطق غير جاهزة. تحقق من تثبيت محرك تحويل النص إلى كلام.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun page(title: String, subtitle: String): LinearLayout {
        val scroll = ScrollView(this)

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(22), dp(18), dp(30))
            setBackgroundColor(pageBackgroundColor)
        }

        scroll.addView(content)
        setContentView(scroll)

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(16), dp(18), dp(16))
            background = rounded(blue, 20)
        }

        val titleView = TextView(this).apply {
            text = title
            textSize = 25f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.RIGHT
        }

        val subtitleView = TextView(this).apply {
            text = subtitle
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.RIGHT
            setPadding(0, dp(7), 0, 0)
        }

        header.addView(titleView)
        header.addView(subtitleView)
        content.addView(header)

        return content
    }

    private fun rounded(
        color: Int,
        radius: Int = 15
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun addText(
        parent: LinearLayout,
        value: String,
        size: Int = 16,
        color: Int = dark,
        bold: Boolean = false
    ) {
        val view = TextView(this).apply {
            text = value
            textSize = size.toFloat()
            setTextColor(color)
            gravity = Gravity.RIGHT
            textDirection = View.TEXT_DIRECTION_FIRST_STRONG
            typeface = if (bold) {
                Typeface.DEFAULT_BOLD
            } else {
                Typeface.DEFAULT
            }
            setPadding(dp(2), dp(9), dp(2), dp(9))
        }

        parent.addView(view)
    }

    private fun addCard(
        parent: LinearLayout,
        title: String,
        description: String,
        action: () -> Unit
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(10), dp(16), dp(10))
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = dp(16).toFloat()
                setStroke(dp(1), Color.rgb(225, 232, 243))
            }
            elevation = dp(2).toFloat()
        }

        addText(card, title, 18, blue, true)
        addText(card, description, 14, dark)

        card.setOnClickListener { action() }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(0, dp(6), 0, dp(6))
        parent.addView(card, params)
    }

    // صيغة الزر الافتراضية: اللون الأزرق
    // هذا الإصلاح يعالج استدعاءات الأزرار باستخدام trailing lambda.
    private fun addButton(
        parent: LinearLayout,
        label: String,
        action: () -> Unit
    ) {
        addButton(parent, label, action, blue)
    }

    // صيغة الزر مع لون مخصص
    private fun addButton(
        parent: LinearLayout,
        label: String,
        action: () -> Unit,
        color: Int
    ) {
        val button = Button(this).apply {
            text = label
            isAllCaps = false
            textSize = 15f
            setTextColor(Color.WHITE)
            background = rounded(color, 13)
            setOnClickListener { action() }
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(53)
        )

        params.setMargins(0, dp(5), 0, dp(5))
        parent.addView(button, params)
    }

    private fun addSpeakCard(
        parent: LinearLayout,
        english: String,
        arabic: String
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(15), dp(10), dp(15), dp(10))
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = dp(14).toFloat()
                setStroke(dp(1), Color.rgb(225, 232, 243))
            }
        }

        addText(card, english, 19, blue, true)
        addText(card, arabic, 15, dark)

        addButton(card, "🔊 استمع إلى النطق", {
            speak(english)
        }, green)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(0, dp(6), 0, dp(6))
        parent.addView(card, params)
    }

    private fun showHome() {
        val p = page(
            "EnglishMaster",
            "تعلم الإنجليزية من الصفر حتى الاحتراف"
        )

        addText(p, "مرحباً بك! 👋", 23, dark, true)

        addText(
            p,
            "ابدأ رحلتك اليوم، وتعلم الإنجليزية خطوة بخطوة.",
            16
        )

        val completed = preferences.getInt("completed_lessons", 0)
        val score = preferences.getInt("quiz_score", 0)

        addCard(
            p,
            "إنجازاتك الحالية",
            "الدروس المكتملة: $completed | نقاط الاختبارات: $score"
        ) {
            showProgress()
        }

        addText(p, "اختر مستواك التعليمي", 21, blue, true)

        val levels = listOf(
            Triple("A1", "مبتدئ", "ابدأ من الأساسيات"),
            Triple("A2", "مبتدئ متقدم", "طور مهاراتك اليومية"),
            Triple("B1", "متوسط", "تحدث بثقة أكبر"),
            Triple("B2", "متوسط متقدم", "انتقل إلى الإنجليزية المتقدمة")
        )

        levels.forEach { (level, name, description) ->
            addCard(
                p,
                "$level - $name",
                description
            ) {
                currentLevel = level
                showLevel(level)
            }
        }

        addText(p, "أقسام التعلم", 21, blue, true)

        val sections = listOf(
            "المفردات" to "تعلم كلمات جديدة",
            "المحادثات" to "تدرب على الحوار",
            "القواعد" to "تعلم قواعد الإنجليزية",
            "القصص" to "اقرأ قصصاً قصيرة",
            "الاستماع" to "استمع إلى الجمل الإنجليزية",
            "الاختبارات" to "اختبر معلوماتك",
            "تقدمي" to "تابع إنجازاتك"
        )

        sections.forEach { (name, description) ->
            addCard(p, name, description) {
                showSection(name)
            }
        }

        addText(
            p,
            "كل يوم خطوة جديدة نحو إتقان الإنجليزية ❤️",
            15,
            blue,
            true
        )
    }

    private fun showLevel(level: String) {
        val p = page(
            "المستوى $level",
            "اختر الدرس الذي تريد تعلمه"
        )

        val lessons = levelLessons[level] ?: emptyList()

        lessons.forEachIndexed { index, lesson ->
            addCard(
                p,
                "الدرس ${index + 1}: $lesson",
                "افتح الدرس وتعلم كلمات وجملاً جديدة"
            ) {
                showLesson(level, lesson)
            }
        }

        addButton(p, "العودة للرئيسية") {
            showHome()
        }
    }

    private fun showLesson(level: String, lesson: String) {
        val p = page(
            lesson,
            "المستوى $level | تعلم واستمع وكرر"
        )

        val examples = listOf(
            LessonExample("Hello", "مرحباً"),
            LessonExample("How are you?", "كيف حالك؟"),
            LessonExample("I am fine.", "أنا بخير"),
            LessonExample("Thank you.", "شكراً لك"),
            LessonExample("Nice to meet you.", "سعيد بلقائك"),
            LessonExample("Goodbye.", "إلى اللقاء")
        )

        addText(p, "الكلمات والجمل التعليمية", 20, blue, true)

        examples.forEach {
            addSpeakCard(p, it.english, it.arabic)
        }

        addButton(p, "تحديد الدرس كمكتمل", {
            val oldCount = preferences.getInt("completed_lessons", 0)
            preferences.edit()
                .putInt("completed_lessons", oldCount + 1)
                .apply()

            Toast.makeText(
                this,
                "أحسنت! تم حفظ إنجازك 🎉",
                Toast.LENGTH_SHORT
            ).show()

            showLevel(level)
        }, green)

        addButton(p, "العودة للدروس") {
            showLevel(level)
        }

        addButton(p, "الرئيسية") {
            showHome()
        }
    }

    private fun showSection(name: String) {
        when (name) {
            "الاختبارات" -> {
                showQuiz()
                return
            }

            "تقدمي" -> {
                showProgress()
                return
            }
        }

        val p = page(
            name,
            "تعلم الإنجليزية بطريقة عملية"
        )

        when (name) {
            "المفردات" -> {
                addText(p, "كلمات إنجليزية أساسية", 20, blue, true)

                val words = listOf(
                    "Hello" to "مرحباً",
                    "Book" to "كتاب",
                    "Water" to "ماء",
                    "Friend" to "صديق",
                    "Beautiful" to "جميل",
                    "House" to "منزل",
                    "School" to "مدرسة",
                    "Family" to "عائلة",
                    "Teacher" to "معلم",
                    "Happy" to "سعيد",
                    "Learn" to "يتعلم",
                    "Important" to "مهم"
                )

                words.forEach { (english, arabic) ->
                    addSpeakCard(p, english, arabic)
                }
            }

            "المحادثات" -> {
                addText(p, "محادثة التعارف", 20, blue, true)

                val conversation = listOf(
                    LessonExample("Hello! How are you?", "مرحباً! كيف حالك؟"),
                    LessonExample("I am fine, thank you.", "أنا بخير، شكراً لك."),
                    LessonExample("What is your name?", "ما اسمك؟"),
                    LessonExample("My name is Ahmed.", "اسمي أحمد."),
                    LessonExample("Nice to meet you.", "سعيد بلقائك.")
                )

                conversation.forEach {
                    addSpeakCard(p, it.english, it.arabic)
                }
            }

            "القواعد" -> {
                addText(p, "قاعدة Verb To Be", 20, blue, true)

                val rules = listOf(
                    LessonExample("I am a student.", "أنا طالب."),
                    LessonExample("You are happy.", "أنت سعيد."),
                    LessonExample("He is a teacher.", "هو معلم."),
                    LessonExample("She is beautiful.", "هي جميلة."),
                    LessonExample("We are friends.", "نحن أصدقاء."),
                    LessonExample("They are students.", "هم طلاب.")
                )

                rules.forEach {
                    addSpeakCard(p, it.english, it.arabic)
                }

                addText(
                    p,
                    "تذكر: نستخدم am مع I، وis مع He وShe، وare مع You وWe وThey.",
                    15,
                    dark
                )
            }

            "القصص" -> {
                addText(p, "قصة: My Daily Life", 20, blue, true)

                val story = listOf(
                    LessonExample("My name is Adam.", "اسمي آدم."),
                    LessonExample("I am a student.", "أنا طالب."),
                    LessonExample("I wake up early.", "أستيقظ مبكراً."),
                    LessonExample("I go to school.", "أذهب إلى المدرسة."),
                    LessonExample("I love learning English.", "أحب تعلم الإنجليزية.")
                )

                story.forEach {
                    addSpeakCard(p, it.english, it.arabic)
                }
            }

            "الاستماع" -> {
                addText(p, "تمارين الاستماع والنطق", 20, blue, true)

                addText(
                    p,
                    "اضغط على زر الاستماع لسماع الجملة، ثم حاول تكرارها بصوتك.",
                    15
                )

                val listening = listOf(
                    LessonExample("Good morning!", "صباح الخير!"),
                    LessonExample("Where are you from?", "من أين أنت؟"),
                    LessonExample("I speak English.", "أنا أتحدث الإنجليزية."),
                    LessonExample("I want to learn.", "أريد أن أتعلم."),
                    LessonExample("Practice makes perfect.", "الممارسة تؤدي إلى الإتقان.")
                )

                listening.forEach {
                    addSpeakCard(p, it.english, it.arabic)
                }
            }
        }

        addButton(p, "العودة للرئيسية") {
            showHome()
        }
    }

    private fun showQuiz() {
        quizIndex = 0
        quizScore = 0
        quizAnswered = false
        showQuizQuestion()
    }

    private fun showQuizQuestion() {
        val p = page(
            "اختبار الإنجليزية",
            "السؤال ${quizIndex + 1} من ${questions.size}"
        )

        if (quizIndex >= questions.size) {
            showQuizResult()
            return
        }

        val question = questions[quizIndex]

        addText(p, "اختر الإجابة الصحيحة:", 19, blue, true)
        addText(p, question.question, 23, dark, true)

        question.answers.forEachIndexed { index, answer ->
            addButton(p, answer, {
                if (!quizAnswered) {
                    quizAnswered = true

                    if (index == question.correct) {
                        quizScore++
                        addText(p, "إجابة صحيحة! أحسنت 🎉", 18, green, true)
                    } else {
                        addText(
                            p,
                            "إجابة غير صحيحة. الجواب: ${question.answers[question.correct]}",
                            16,
                            orange,
                            true
                        )
                    }

                    addButton(p, "السؤال التالي", {
                        quizIndex++
                        quizAnswered = false
                        showQuizQuestion()
                    }, green)
                }
            })
        }

        addButton(p, "إنهاء الاختبار", {
            showQuizResult()
        }, orange)

        addButton(p, "الرئيسية") {
            showHome()
        }
    }

    private fun showQuizResult() {
        val oldScore = preferences.getInt("quiz_score", 0)
        preferences.edit()
            .putInt("quiz_score", oldScore + quizScore)
            .apply()

        val p = page(
            "نتيجة الاختبار",
            "انتهيت من جميع الأسئلة"
        )

        addText(p, "أحسنت! 🎉", 25, green, true)

        addText(
            p,
            "إجاباتك الصحيحة: $quizScore من ${questions.size}",
            21,
            dark,
            true
        )

        addText(
            p,
            "نسبتك: ${quizScore * 100 / questions.size}%",
            20,
            blue,
            true
        )

        addButton(p, "إعادة الاختبار") {
            showQuiz()
        }

        addButton(p, "الرئيسية") {
            showHome()
        }
    }

    private fun showProgress() {
        val p = page(
            "تقدمي في التعلم",
            "إنجازاتك محفوظة على هاتفك"
        )

        val completed = preferences.getInt("completed_lessons", 0)
        val score = preferences.getInt("quiz_score", 0)

        addCard(
            p,
            "الدروس المكتملة",
            "$completed درس"
        ) {}

        addCard(
            p,
            "مجموع نقاط الاختبارات",
            "$score نقطة"
        ) {}

        addText(p, "نصيحة اليوم", 20, blue, true)

        addText(
            p,
            "خصص وقتاً يومياً لتعلم خمس كلمات جديدة والاستماع إلى جمل إنجليزية.",
            16,
            dark
        )

        addButton(p, "العودة للرئيسية") {
            showHome()
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        super.onDestroy()
    }
}
