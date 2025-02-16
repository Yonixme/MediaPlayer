package com.example.fullproject.screens.musiclist.newr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullproject.model.room.song.MusicRepository
import com.example.fullproject.model.room.song.entities.SongNew
import com.example.fullproject.model.room.song.entities.SongWithDetails
import com.example.fullproject.model.room.song.infoprovider.MusicInfoProvider
import com.example.fullproject.model.services.newr.MusicServiceManager
import com.example.fullproject.model.services.newr.MusicServiceManager.CurrentSongState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicListViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val musicServiceManager: MusicServiceManager,
    private val musicInfoProvider: MusicInfoProvider
) : ViewModel() {
    private val _listSongWithDetails = MutableLiveData<ScreenStateWithDetails>(ScreenStateWithDetails.Loading)
    val listSongWithDetails: LiveData<ScreenStateWithDetails> get() = _listSongWithDetails

    init {
        viewModelScope.launch {
            launch {
                combine(
                    musicRepository.getListSongsFromDevice(),
                    musicServiceManager.getCurrentSongWithDetails()
                ) { listSongsFromDevice, currentSongDetailsState ->
                    val updatedList = listSongsFromDevice?.mapNotNull { song ->
                        if (song.uri == (currentSongDetailsState as? CurrentSongState.Success)?.currentSong?.song?.uri) {
                            currentSongDetailsState.currentSong
                        } else {
                            musicInfoProvider.getInformationForSong(song)
                                ?: return@combine
                        }
                    } ?: emptyList()

                    _listSongWithDetails.value = if (updatedList.isNotEmpty()) {
                        ScreenStateWithDetails.Success(updatedList)
                    } else if(currentSongDetailsState is CurrentSongState.Loading){
                        ScreenStateWithDetails.Loading
                    }else{
                        ScreenStateWithDetails.Empty
                    }
                }.catch { e ->
                    _listSongWithDetails.value = ScreenStateWithDetails.Error(e.toString())
                }.collect{}
            }
        }
    }

    fun onPlay(uri: String){
        musicServiceManager.onPlay(uri)
    }

    fun onPause(uri: String){
        musicServiceManager.onPause(uri)
    }

    fun onStop(uri: String){
        musicServiceManager.onStop(uri)
    }

    fun getIsPlayingState(): Boolean{
        return musicServiceManager.getIsPlaying()
    }

    override fun onCleared() {
        super.onCleared()
        println("Debug22 in viewModel $this")
        musicServiceManager.unBindService()
    }

    sealed class ScreenStateWithDetails{
        data object Loading : ScreenStateWithDetails()
        data class Success(val listSong: List<SongWithDetails>): ScreenStateWithDetails()
        data class Error(val massage: String) : ScreenStateWithDetails()
        data object Empty: ScreenStateWithDetails()
    }
}