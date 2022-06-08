package com.android.currencies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.currencies.ui.CurrencyConverterFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(
                R.id.fragment_container,
                CurrencyConverterFragment()
            ).commit()
        }
    }
}