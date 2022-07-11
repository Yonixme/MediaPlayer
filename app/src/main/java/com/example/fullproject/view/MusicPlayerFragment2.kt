package com.example.fullproject.view

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fullproject.databinding.FragmentMusicPlayerBinding
import com.example.fullproject.model.Song

class MusicPlayerFragment2() : Fragment() {

    private lateinit var binding: FragmentMusicPlayerBinding
    private val viewModel: MusicPlayerViewModel by viewModels { factory() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container,false)
        viewModel.setContextForMusic(context)

        return binding.root
    }

    private fun controlSound(){
        binding.playOrPause.setOnClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setContextForMusic(null)
    }



    companion object{
        @JvmStatic
        fun newInstance(){
        }
    }
}
