package com.example.tableapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tableapp.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity(R.layout.activity_main) {
    lateinit var binding: ActivityMainBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
    }
}