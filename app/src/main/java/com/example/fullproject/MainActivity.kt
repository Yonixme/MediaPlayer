package com.example.fullproject


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fullproject.databinding.ActivityMainScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater).also { setContentView(it.root) }
    }
}