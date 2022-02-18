package com.example.fullproject.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fullproject.databinding.FragmentMusicPlayerBinding

class MusicPlayerFragment : Fragment() {
    lateinit var binding: FragmentMusicPlayerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object{

        private const val ARG = "ARG"

        fun newInstance(): Fragment {
            return MusicPlayerFragment()
        }

    }
}