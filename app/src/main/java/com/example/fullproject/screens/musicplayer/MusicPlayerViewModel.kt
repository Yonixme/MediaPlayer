package com.example.fullproject.screens.musicplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullproject.model.services.MusicServiceManager
import com.example.fullproject.model.services.MusicServiceManager.CurrentSongState
import com.example.fullproject.model.song.MusicRepository
import com.example.fullproject.model.song.entities.SongWithDetails
import com.example.fullproject.model.song.provider.infoprovider.MusicInfoProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val musicServiceManager: MusicServiceManager,
    private val musicInfoProvider: MusicInfoProvider,
    private val musicRepository: MusicRepository
): ViewModel() {
    private var timerJob: Job? = null
    private var newSongSelected: Boolean = false
    private var observeOnUpdating = false

    private val _selectedSong: MutableLiveData<SongWithDetails?> = MutableLiveData(null)
    val selectedSong: LiveData<SongWithDetails?> = _selectedSong

    private val _currentSongState = MutableLiveData<CustomSongState>(CustomSongState.Loading)
    val currentSongState: LiveData<CustomSongState> = _currentSongState

    init {
        viewModelScope.launch {
            launch {
                musicRepository.getListSongsFromDevice().collect{list->
                    val updatedSelectedSong = list?.firstOrNull{
                        it.uri == selectedSong.value?.song?.uri
                    }
                    if (updatedSelectedSong != _selectedSong.value?.song){
                        if (updatedSelectedSong == null) {
                            _selectedSong.value = null
                        }else{
                            _selectedSong.value =
                                musicInfoProvider.getInformationForSong(updatedSelectedSong)
                        }
                    }
                }
            }
            launch {
                musicServiceManager.getCurrentSongWithDetails().collect{
                    when(it){
                        is CurrentSongState.Empty -> {
                            _currentSongState.value = CustomSongState.Empty
                        }
                        is CurrentSongState.Loading -> {
                            _currentSongState.value = CustomSongState.Loading
                        }
                        is CurrentSongState.Error -> {
                            _currentSongState.value = CustomSongState.Error(it.massage)
                        }
                        is CurrentSongState.Success -> {
                            _currentSongState.value = CustomSongState.Success(it.currentSong)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun initSelectedSong(uri: String){
        viewModelScope.launch {
            _selectedSong.value = musicInfoProvider.getInformationForSong(
                musicRepository.getSongByURI(uri) ?: return@launch
            )
        }
    }

    fun onPlay(){
        if (timerJob == null) startUpdatingTimer()
        println("debug123 in viewModel ${getSelectedURI()}")
        val selectURI = getSelectedURI() ?: return
        musicServiceManager.onPlay(selectURI)
    }

    fun onPause(){
        if (timerJob != null) stopTimer()
        val selectURI = getSelectedURI() ?: return
        musicServiceManager.onPause(selectURI)
    }

    fun onStop(){
        if (timerJob != null) stopTimer()
        val selectURI = getSelectedURI() ?: return
        musicServiceManager.onStop(selectURI)
    }

    private fun getSelectedURI() : String?{
        return _selectedSong.value?.song?.uri
    }

    fun setCurrentTime(currentPosition: Int){
        musicServiceManager.setCurrentTime(currentPosition)
    }

    fun startUpdatingTimer(){
        if (timerJob == null) {
            timerJob = CoroutineScope(Dispatchers.Default).launch {
                while (true) {
                    musicServiceManager.updateSongState()
                    delay(1000L)
                }
            }
        }
    }

    fun stopTimer(){
        if (timerJob != null) {
            timerJob?.cancel()
            timerJob = null
        }
    }

    fun pauseMusic(){
        val selectURI = getSelectedURI() ?: return
        musicServiceManager.pauseMusic(selectURI)
    }

    fun continueMusic(){
        val selectURI = getSelectedURI() ?: return
        musicServiceManager.continueMusic(selectURI)
    }

    fun nextSong(){
        if (timerJob == null) startUpdatingTimer()
        val selectURI = getSelectedURI() ?: return
        musicServiceManager.nextSong(selectURI)
    }

    fun previousSong(){
        if (timerJob == null) startUpdatingTimer()
        val selectURI = getSelectedURI() ?: return
        musicServiceManager.previousSong(selectURI)
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }

    fun millisToMinute(progress: Int): String {
        var seconds:Int = progress / 1000

        val minute: Int
        if (seconds >= 60) {
            minute = seconds / 60
            seconds %= 60
        } else minute = 0

        return if (seconds >= 10) "$minute:$seconds"
        else "$minute:0$seconds"
    }

    sealed class CustomSongState{
        data object Loading : CustomSongState()
        data class Success(val currentSong: SongWithDetails?): CustomSongState()
        data class Error(val massage: String) : CustomSongState()
        data object Empty: CustomSongState()
    }
}