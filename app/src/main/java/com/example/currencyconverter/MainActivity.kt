package com.example.currencyconverter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val currency: Map<String, String> = hashMapOf(
        "United States - Dollar" to "USD",
        "European Union - Euro" to "EUR",
        "United Kingdom - Pound" to "GBP",
        "Japan - Yen" to "JPY",
        "Vietnam - Dong" to "VND",
        "South Korea - Won" to "KRW")

    private val rate: Map<String, Double> = hashMapOf(
        "USD" to 1.0,
        "EUR" to 0.93,
        "GBP" to 0.77,
        "JPY" to 152.23,
        "VND" to 25427.5,
        "KRW" to 1380.22
    )

    private val symbol: Map<String, String> = hashMapOf(
        "USD" to "$",
        "EUR" to "€",
        "GBP" to "£",
        "JPY" to "¥",
        "VND" to "₫",
        "KRW" to "₩"
    )

    private lateinit var optView1: AutoCompleteTextView
    private lateinit var optView2: AutoCompleteTextView

    private lateinit var amt1: EditText
    private lateinit var amt2: EditText

    private lateinit var adapter: ArrayAdapter<String>

    private val pair: Array<String> = Array(2) { "" }

    private var isUpdating: Boolean = false

    private fun convertCurrency(amount: Double, state: Boolean): Double = if (state) amount * rate[pair[1]]!! / rate[pair[0]]!! else amount * rate[pair[0]]!! / rate[pair[1]]!!

    private fun updateAmount1() {
        val amount = amt1.text.toString().toDoubleOrNull() ?: 0.0
        isUpdating = true
        if (amount == 0.0) {
            amt2.setText(R.string.empty)
        } else if (pair[0].isNotEmpty() && pair[1].isNotEmpty()) {
           val convertedAmount = convertCurrency(amount, true)
           amt2.setText(String.format(Locale.US, "%.4f", convertedAmount))
        }
        isUpdating = false
    }

    private fun updateAmount2() {
        val amount = amt2.text.toString().toDoubleOrNull() ?: 0.0
        isUpdating = true
        if (amount == 0.0) {
            amt1.setText(R.string.empty)
        } else if (pair[0].isNotEmpty() && pair[1].isNotEmpty()) {
            val convertedAmount = convertCurrency(amount, false)
            amt1.setText(String.format(Locale.US, "%.4f", convertedAmount))
        }
        isUpdating = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_linear)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = ArrayAdapter(this, R.layout.list_item, currency.keys.toTypedArray())

        optView1 = findViewById(R.id.currency_1)
        optView2 = findViewById(R.id.currency_2)

        amt1 = findViewById(R.id.amt_1)
        amt2 = findViewById(R.id.amt_2)

        optView1.setAdapter(adapter)
        optView2.setAdapter(adapter)

        optView1.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            println(selectedItem)
            pair[0] = currency[selectedItem] ?: ""
            val unit: TextView = findViewById(R.id.unit_1)
            unit.text = pair[0]
            val sym: TextView = findViewById(R.id.sym_1)
            sym.text = symbol[pair[0]]
            if (pair[1].isNotEmpty() && amt2.text.toString().isNotEmpty()) {
                updateAmount2()
            }
        }
        optView2.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            println(selectedItem)
            pair[1] = currency[selectedItem] ?: ""
            val unit: TextView = findViewById(R.id.unit_2)
            unit.text = pair[1]
            val sym: TextView = findViewById(R.id.sym_2)
            sym.text = symbol[pair[1]]
            if (pair[0].isNotEmpty() && amt1.text.toString().isNotEmpty()) {
                updateAmount1()
            }
        }

        amt1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (pair[0].isNotEmpty() && pair[1].isNotEmpty()) {
                    if (!isUpdating) updateAmount1()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        amt2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (pair[0].isNotEmpty() && pair[1].isNotEmpty()) {
                    if (!isUpdating) updateAmount2()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}