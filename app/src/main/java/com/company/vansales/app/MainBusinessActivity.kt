package com.company.vansales.app

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.company.vansales.databinding.ActivityMainBusinessBinding
import com.company.vansales.R
import com.company.vansales.app.activity.LoginActivity


class MainBusinessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBusinessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBusinessBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    private fun startEntitySetListActivity() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        })
    }

    override fun onResume() {
        super.onResume()
        startEntitySetListActivity()
    }

    companion object {
    }
}
