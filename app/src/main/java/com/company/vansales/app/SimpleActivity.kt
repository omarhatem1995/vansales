package com.company.vansales.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.company.vansales.R

class SimpleActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.simple)
    }
}