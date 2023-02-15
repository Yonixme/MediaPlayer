package com.example.fullproject

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fullproject.businesslogic.SongMusic
import com.example.fullproject.databinding.ActivityMainScreenBinding
import com.example.fullproject.view.MusicListFragment
import com.example.fullproject.view.MusicPlayerFragment

class MainActivity :AppCompatActivity(), Navigator{
    private lateinit var binding: ActivityMainScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater).also { setContentView(it.root) }

        Log.d("Activity", "onCreate()")

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, MusicListFragment())
                .commit()
        }
        var id = (applicationContext as App).id
        Log.d("idApp", "$id")
        (applicationContext as App).upValue()
        id = (applicationContext as App).id
        Log.d("idApp", "$id")
    }

    override fun goBack() {
        onBackPressed()
    }


    override fun onMusicPlaylist(currentTime: Long, song: SongMusic) {
        launchFragment(MusicPlayerFragment.newInstance(currentTime, song))
    }

    private fun launchFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}