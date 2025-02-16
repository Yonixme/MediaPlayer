package com.example.fullproject


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fullproject.databinding.ActivityMainScreenBinding
import com.example.fullproject.screens.dblists.oldr.DataBaseListFragmentOLD
import com.example.fullproject.screens.musicplayer.oldr.MusicPlayerFragmentOLD
import com.example.fullproject.model.songpack.entities.SongPackage
import dagger.hilt.android.AndroidEntryPoint


/*
class MainActivity : AppCompatActivity(), Navigator{
    private lateinit var binding: ActivityMainScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater).also { setContentView(it.root) }
        DBRepositories.init(applicationContext)

        if (supportFragmentManager.backStackEntryCount == 0 ){
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, MusicListFragmentOLD())
                .commit()
        }
    }

    override fun goBack() {
        @Suppress("DEPRECATION  ")
        onBackPressed()
    }

    override fun onMusicPlaylist(song: SongPackage) {
        launchFragment(MusicPlayerFragmentOLD.newInstance(song))
    }

    private fun launchFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onDataBaseList() { launchFragment(DataBaseListFragmentOLD()) }
}*/

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Navigator{
    private lateinit var binding: ActivityMainScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater).also { setContentView(it.root) }

//        if (supportFragmentManager.backStackEntryCount == 0 ){
//            supportFragmentManager
//                .beginTransaction()
//                .add(R.id.fragmentContainer, MusicListFragmentOLD())
//                .commit()
//        }
    }

    override fun goBack() {
        @Suppress("DEPRECATION  ")
        onBackPressed()
    }

    override fun onMusicPlaylist(song: SongPackage) {
        launchFragment(MusicPlayerFragmentOLD.newInstance(song))
    }

    private fun launchFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onDataBaseList() { launchFragment(DataBaseListFragmentOLD()) }
}