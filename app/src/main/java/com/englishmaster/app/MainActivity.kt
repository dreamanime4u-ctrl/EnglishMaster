
package com.englishmaster.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private val blue = Color.rgb(35, 91, 160)
    private val dark = Color.rgb(30, 45, 65)
    private val background = Color.rgb(245, 248, 253)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
    }

    private fun page(title: String, subtitle: String): LinearLayout {
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(30))
            setBackgroundColor(background)
        }

        scroll.addView(content)
        setContentView(scroll)

        addText(content, title, 27, blue, true)
        addText(content, subtitle, 15, dark, false)
        return content
    }

    private fun addText(
        parent: LinearLayout,
        value: String,
        size: Int,
        color: Int,
        bold: Boolean
    ) {
        val view = TextView(this).apply {
            text = value
            textSize = size.toFloat()
            setTextColor(color)
            gravity = Gravity.RIGHT
            textDirection = View.TEXT_DIRECTION_FIRST_STRONG
            typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            setPadding(0, dp(10), 0, dp(10))
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
        }

        addText(card, title, 19, blue, true)
        addText(card, description, 14, dark, false)

        card.setOnClickListener { action() }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, dp(7), 0, dp(7))
        parent.addView(card, params)
    }

    private fun addButton(
        parent: LinearLayout,
        label: String,
        action: () -> Unit
    ) {
        val button = Button(this).apply {
            text = label
            isAllCaps = false
            setTextColor(Color.WHITE)
            textSize = 16f
            background = GradientDrawable().apply {
                setColor(blue)
                cornerRadius = dp(12).toFloat()
            }
            setOnClickListener { action() }
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(55)
        )
        params.setMargins(0, dp(6), 0, dp(6))
        parent.addView(button, params)
    }

    private fun showHome() {
        val p = page(
            "English Master",
            "تعلم الإنجليزية من الصفر حتى الاحتراف"
        )

        addText(p, "مرحباً بك! 👋", 23, dark, true)
        addText(
            p,
            "ابدأ رحلتك في تعلم اللغة الإنجليزية خطوة بخطوة.",
            16, dark, false
        )

        addText(p, "اختر مستواك", 21, blue, true)

        listOf("A1" to "مبتدئ",
            "A2" to "مبتدئ متقدم",
            "B1" to "متوسط",
            "B2" to "متوسط متقدم"
        ).forEach { (level, name) ->
            addCard(p, level, name) {
                showLevel(level)
            }
        }

        addText(p, "أقسام التعلم", 21, blue, true)

        val sections = listOf(
            "المفردات" to "تعلم كلمات إنجليزية ومعانيها",
            "المحادثات" to "تعلم الحوارات اليومية",
            "القواعد" to "افهم قواعد اللغة الإنجليزية",
            "القصص" to "اقرأ قصصاً باللغة الإنجليزية",
            "الاستماع" to "تدرب على فهم الجمل الإنجليزية",
            "الاختبارات" to "اختبر معلوماتك",
            "تقدمي" to "تابع تقدمك في التعلم"
        )

        sections.forEach { (name, description) ->
            addCard(p, name, description) {
                showSection(name)
            }
        }

        addText(
            p,
            "كل يوم خطوة جديدة نحو إتقان الإنجليزية ❤️",
            15, blue, true
        )
    }

    private fun showLevel(level: String) {
        val p = page(
            "المستوى $level",
            "اختر درساً وابدأ التعلم"
        )

        val lessons = when (level) {
            "A1" -> listOf(
                "التحيات والتعارف",
                "الحروف والأرقام",
                "الأسرة والأصدقاء",
                "الألوان والأشياء",
                "الأيام والوقت"
            )
            "A2" -> listOf(
                "الحياة اليومية",
                "الطعام والشراب",
                "التسوق",
                "وصف الأماكن",
                "الماضي البسيط"
            )
            "B1" -> listOf(
                "التعبير عن الرأي",
                "السفر والعمل",
                "الأزمنة الإنجليزية",
                "المواقف اليومية",
                "كتابة الفقرات"
            )
            else -> listOf(
                "المحادثات المتقدمة",
                "القواعد المتقدمة",
                "التعبير عن الأفكار",
                "القراءة والفهم",
                "الكتابة المتقدمة"
            )
        }

        lessons.forEachIndexed { index, lesson ->
            addCard(p, "الدرس ${index + 1}: $lesson",
                "اضغط لفتح الدرس") {
                showLesson(lesson)
            }
        }

        addButton(p, "العودة للرئيسية") { showHome() }
    }

    private fun showSection(name: String) {
        if (name == "الاختبارات") {
            showQuiz()
            return
        }

        val p = page(name, "تعلم وتدرب على اللغة الإنجليزية")

        val content = when (name) {
            "المفردات" -> listOf(
                "Hello = مرحباً",
                "Good morning = صباح الخير",
                "Book = كتاب",
                "Water = ماء",
                "Friend = صديق",
                "Beautiful = جميل"
            )
            "المحادثات" -> listOf(
                "Hello! How are you? = مرحباً، كيف حالك؟",
                "I am fine, thank you. = أنا بخير، شكراً.",
                "What is your name? = ما اسمك؟",
                "My name is Ahmed. = اسمي أحمد."
            )
            "القواعد" -> listOf(
                "I am = أنا أكون",
                "You are = أنت تكون",
                "He is = هو يكون",
                "She is = هي تكون",
                "I have = أنا أملك"
            )
            "القصص" -> listOf(
                "My name is Adam.",
                "I am a student.",
                "I live with my family.",
                "I love learning English."
            )
            "الاستماع" -> listOf(
                "مرحباً بك في قسم الاستماع.",
                "تدرب على قراءة الجمل الإنجليزية.",
                "سيتم تطوير تمارين الصوت في مرحلة لاحقة."
            )
            else -> emptyList()
        }

        content.forEachIndexed { index, item ->
            addCard(p, "تمرين ${index + 1}", item) {
                Toast.makeText(this, item, Toast.LENGTH_SHORT).show()
            }
        }

        addButton(p, "العودة للرئيسية") { showHome() }
    }

    private fun showLesson(lesson: String) {
        val p = page(lesson, "درس تعليمي للمبتدئين")

        val examples = listOf(
            "Hello = مرحباً",
            "How are you? = كيف حالك؟",
            "I am fine = أنا بخير",
            "Thank you = شكراً لك",
            "Goodbye = إلى اللقاء"
        )

        examples.forEachIndexed { index, example ->
            addCard(p, "مثال ${index + 1}", example) {
                Toast.makeText(this, example, Toast.LENGTH_SHORT).show()
            }
        }

        addButton(p, "العودة للدروس") {
            showLevel("A1")
        }
        addButton(p, "الرئيسية") { showHome() }
    }

    private fun showQuiz() {
        val p = page(
            "اختبار الإنجليزية",
            "اختر الإجابة الصحيحة"
        )

        addText(p, "ما معنى كلمة Apple؟", 21, dark, true)

        val answers = listOf(
            "كتاب" to false,
            "تفاحة" to true,
            "ماء" to false
        )

        answers.forEach { (answer, correct) ->
            addButton(p, answer) {
                val message = if (correct) {
                    "إجابة صحيحة! أحسنت 🎉"
                } else {
                    "إجابة غير صحيحة، حاول مرة أخرى."
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        addButton(p, "العودة للرئيسية") { showHome() }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
