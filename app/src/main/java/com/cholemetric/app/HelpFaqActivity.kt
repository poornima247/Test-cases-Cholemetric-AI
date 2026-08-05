package com.cholemetric.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HelpFaqActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_faq)

        val llBack = findViewById<LinearLayout>(R.id.ll_faq_back)
        val etSearch = findViewById<EditText>(R.id.et_faq_search)
        val btnContact = findViewById<Button>(R.id.btn_faq_contact_support)

        // Find FAQ Card layout views
        val faqContainer = etSearch.parent as LinearLayout
        val faqCards = ArrayList<LinearLayout>()

        // Gather all cards dynamically
        for (i in 0 until faqContainer.childCount) {
            val child = faqContainer.getChildAt(i)
            // The cards are the LinearLayouts (excluding the search EditText and the bottom still need help box)
            if (child is LinearLayout && child.id != R.id.ll_faq_back && child.childCount >= 3) {
                faqCards.add(child)
            }
        }

        // Search dynamic filtering
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase().trim()
                for (card in faqCards) {
                    var matches = false
                    // Read text content in card to filter
                    for (j in 0 until card.childCount) {
                        val view = card.getChildAt(j)
                        if (view is android.widget.TextView) {
                            if (view.text.toString().lowercase().contains(query)) {
                                matches = true
                                break
                            }
                        }
                    }
                    card.visibility = if (matches || query.isEmpty()) View.VISIBLE else View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Bind actions
        llBack.setOnClickListener {
            finish()
        }

        btnContact.setOnClickListener {
            contactSupport()
        }
    }

    private fun contactSupport() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("support@choliscan.com"))
            putExtra(Intent.EXTRA_SUBJECT, "CholeMetric App FAQ Inquiry")
        }
        try {
            startActivity(Intent.createChooser(intent, "Send Email..."))
        } catch (e: Exception) {
            Toast.makeText(this, "No email app installed", Toast.LENGTH_SHORT).show()
        }
    }
}
