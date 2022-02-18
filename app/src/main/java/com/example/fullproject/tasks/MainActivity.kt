package com.example.fullproject.tasks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fullproject.R
import com.example.fullproject.databinding.ActivityMainScreenBinding



class MainActivity :AppCompatActivity(){
    lateinit var binding: ActivityMainScreenBinding

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


