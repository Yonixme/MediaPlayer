package com.example.fullproject


import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fullproject.databinding.ActivityMainScreenBinding
import com.example.fullproject.screens.dblists.DataBaseListFragment
import com.example.fullproject.screens.musiclist.MusicListFragment
import com.example.fullproject.screens.musicplayer.MusicPlayerFragment
import com.example.fullproject.services.model.songpack.entities.SongPackage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), Navigator{
    private lateinit var binding: ActivityMainScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater).also { setContentView(it.root) }
        Repositories.init(applicationContext)
        val handler = Handler(Looper.getMainLooper())

        object : CountDownTimer(500L, 500L){
            override fun onTick(millisUntilFinished: Long) = Unit
            override fun onFinish() {
                handler.post{
                    Log.d("DataBaseURI", "sss")
                    supportFragmentManager
                        .beginTransaction()
                        .add(R.id.fragmentContainer, MusicListFragment())
                        .commit()
                }
            }
        }.start()
    }

    override fun goBack() {
        @Suppress("DEPRECATION")
        onBackPressed()
    }

    override fun onMusicPlaylist(song:SongPackage) {
        launchFragment(MusicPlayerFragment.newInstance(song))
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