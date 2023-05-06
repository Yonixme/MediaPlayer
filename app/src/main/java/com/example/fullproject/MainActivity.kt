package com.example.fullproject


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fullproject.services.model.SongMusic
import com.example.fullproject.databinding.ActivityMainScreenBinding
import com.example.fullproject.screens.dblists.DataBaseListFragment
import com.example.fullproject.screens.musiclist.MusicListFragment
import com.example.fullproject.screens.musicplayer.MusicPlayerFragment


class MainActivity : AppCompatActivity(), Navigator{
    private lateinit var binding: ActivityMainScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater).also { setContentView(it.root) }

        Repositories.init(applicationContext)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragmentContainer, MusicListFragment())
            .commit()

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun goBack() {
        @Suppress("DEPRECATION")
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



    override fun onDataBaseList() { launchFragment(DataBaseListFragment()) }
}