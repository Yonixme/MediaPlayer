package com.example.fullproject

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fullproject.databinding.ActivityMainScreenBinding
import com.example.fullproject.screens.MusicPlayerFragment


class MainActivity :AppCompatActivity(){
    private lateinit var binding: ActivityMainScreenBinding

    private val f = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::dot
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater).also { setContentView(it.root) }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, MusicPlayerFragment())
                .commit()
        }

//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//
//        } else{
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                REQUEST_PERMISSION_Write
//            )
//        }
    }

    private fun dot(b: Boolean){
        print("ddd")
    }
    private companion object{
        @JvmStatic private val REQUEST_PERMISSION_Write = 3
    }
}


