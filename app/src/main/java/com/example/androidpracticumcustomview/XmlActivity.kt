package com.example.androidpracticumcustomview

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.view.setPadding
import com.example.androidpracticumcustomview.ui.theme.CustomContainer


class XmlActivity : ComponentActivity() {
    private val customContainer by lazy { CustomContainer(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startXmlPracticum()
    }

    private fun startXmlPracticum() {
        setContentView(customContainer)
        customContainer.setOnClickListener { finish() }
        repeat(COUNT_OF_CHILD) {
            runCatching { addElement(it.toString()) }
                .onFailure { it.printStackTrace() }
        }
    }

    private fun addElement(label: String) {
        TextView(this).apply {
            text = label
            textSize = 30f
            setBackgroundColor(Color.RED)
            setPadding(10)
        }.let { customContainer.addView(it) }
    }

    companion object {
        /**
         * Количество дочерних элементов, которые будут добавлены к пользовательскому представлению
         */
        private const val COUNT_OF_CHILD = 2
    }
}