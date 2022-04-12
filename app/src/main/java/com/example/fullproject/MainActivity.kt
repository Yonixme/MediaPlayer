package com.example.fullproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fullproject.databinding.ActivityMainScreenBinding
import com.example.fullproject.screens.MusicPlayerFragment


class MainActivity :AppCompatActivity(){
    private lateinit var binding: ActivityMainScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater).also { setContentView(it.root) }

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, MusicPlayerFragment())
                .commit()
        }
    }
}


