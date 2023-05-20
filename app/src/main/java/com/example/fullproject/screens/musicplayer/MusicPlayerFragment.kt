package com.example.fullproject.screens.musicplayer


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fullproject.*
import com.example.fullproject.databinding.FragmentMusicPlayerBinding
import com.example.fullproject.screens.viewmodel.MusicPlayerViewModel
import com.example.fullproject.utils.millisToMinute
import com.example.fullproject.model.songpack.entities.SongMapper
import com.example.fullproject.model.SongPackage
import com.example.fullproject.utils.activityNavigator
import com.example.fullproject.utils.factory

class MusicPlayerFragment : Fragment(){

    private lateinit var binding: FragmentMusicPlayerBinding

    private val viewModel: MusicPlayerViewModel by viewModels { factory() }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.song = SongMapper.SongPackageToSong(requireArguments().getParcelable(CHOOSED_SONG)!!).map()
        viewModel.manager = object : PlayerManager { override fun updateViewUI() { updateUI() } }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container,false)
        updateUI()
        controlSound()

        return binding.root
    }



    private fun controlSound(){
        viewModel.launchTimer()

        binding.playOrPause.setOnClickListener {
            if (viewModel.isPlaySound()){
                viewModel.onSoundPause()
            }else{
                viewModel.onSoundPlay()
            }
        }
        binding.stop.setOnClickListener {
            viewModel.onSoundStop()
        }
        binding.backBtn.setOnClickListener{
            activityNavigator().goBack()
        }

        binding.playlist.setOnClickListener{
            activityNavigator().onDataBaseList()
        }

        binding.setting.setOnClickListener{
            viewModel.onItemMoreClick(it)
        }

        binding.timeView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.setTimeSound(progress.toLong())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                viewModel.pauseTimeSound()
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.continueTimeSound()
            }
        })

        binding.next.setOnClickListener{
            Log.d("Debug123", "next in Frag")
            viewModel.nextSound()
        }

        binding.previous.setOnClickListener{
            Log.d("Debug123", "prev in Frag")
            viewModel.previouslySound()
        }
    }

    fun updateUI(){
        binding.timeView.progress = viewModel.getCurrentPosition().toInt()

        if(binding.currentTime.text != millisToMinute(viewModel.getCurrentPosition().toInt()))
            binding.currentTime.text = millisToMinute(viewModel.getCurrentPosition().toInt())

        if(binding.timeAll.text != millisToMinute(viewModel.getDuration().toInt()))
            binding.timeAll.text = millisToMinute(viewModel.getDuration().toInt())

        if(binding.timeView.max != viewModel.getDuration().toInt())
            binding.timeView.max = viewModel.getDuration().toInt()


        if(viewModel.isPlaySound()) {
            binding.playOrPause.setImageResource(R.drawable.ic_pause)
        } else {
            binding.playOrPause.setImageResource(R.drawable.ic_play)
        }

        if(binding.nameMusicHeader.text != viewModel.getNameField())
            binding.nameMusicHeader.text = viewModel.getNameField()

        if(binding.nameMusicPlaying.text != viewModel.getAuthorField())
            binding.nameMusicPlaying.text = viewModel.getAuthorField()
    }


    companion object{
        @JvmStatic
        private val CHOOSED_SONG = "Choosed song"

        @JvmStatic
        fun newInstance(song: SongPackage): MusicPlayerFragment {
            val fragment = MusicPlayerFragment()
            fragment.arguments = bundleOf(
                CHOOSED_SONG to song
            )
            return fragment
        }
    }
}
