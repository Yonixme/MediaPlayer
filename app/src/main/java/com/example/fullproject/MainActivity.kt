package com.example.fullproject


import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.OnBackPressedDispatcher
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

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, MusicListFragment())
                .commit()
        }
        test()
    }

//    override fun goBack() {
//        onBackPressed()
//    }
    override fun goBack() {
        OnBackPressedDispatcher()
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

    private fun test(){
        Log.d("Activity", "onCreate()")
        Log.d("test2", "${(this::launchFragment)}")
        Log.d("test2", " ")

        var str: String? = null
        if(str == null || str.isBlank()) Log.d("Check path", "work")
        var id = (applicationContext as App).id
        Log.d("idApp", "$id")
        (applicationContext as App).upValue()
        id = (applicationContext as App).id
        Log.d("idApp", "$id")

        Log.d("Check path", "${Environment.getExternalStorageDirectory()}/Download/")
        Log.d("Check path", "${this.getExternalFilesDir(null)!!.absolutePath}/Download/")
        Log.d("Check path", this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath)
        Log.d("Check path", Environment.DIRECTORY_DOWNLOADS)
    }
}