package com.example.fullproject.screens.musiclist

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicListViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val musicServiceManager: MusicServiceManager,
    private val musicInfoProvider: MusicInfoProvider
) : ViewModel() {
    private val _listSongWithDetails = MutableLiveData<ScreenStateWithDetails>(
        ScreenStateWithDetails.Loading
    )
    val listSongWithDetails: LiveData<ScreenStateWithDetails> = _listSongWithDetails

    init {
        viewModelScope.launch {
            launch {
                combine(
                    musicRepository.getListSongsFromDevice(),
                    musicServiceManager.getCurrentSongWithDetails()
                ) { state, currentSongDetailsState ->
                    when (state) {
                        MusicRepository.SongDbState.Empty -> _listSongWithDetails.value = ScreenStateWithDetails.Empty
                        MusicRepository.SongDbState.Loading -> _listSongWithDetails.value = ScreenStateWithDetails.Loading
                        is MusicRepository.SongDbState.Success -> {
                            val updatedList = state.songs.mapNotNull { song ->
                                if (song.uri == (currentSongDetailsState as? CurrentSongState.Success)?.currentSong?.song?.uri) {
                                    currentSongDetailsState.currentSong
                                } else {
                                    musicInfoProvider.getInformationForSong(song)
                                }
                            }

                            _listSongWithDetails.value = if (updatedList.isNotEmpty()) {
                                ScreenStateWithDetails.Success(updatedList)
                            }else{
                                ScreenStateWithDetails.Empty
                            }
                        }
                    }
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

    fun loadSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            musicRepository.refreshSongsFromDevice()
        }
    }

    sealed class ScreenStateWithDetails{
        data object Loading : ScreenStateWithDetails()
        data class Success(val listSong: List<SongWithDetails>): ScreenStateWithDetails()
        data class Error(val massage: String) : ScreenStateWithDetails()
        data object Empty: ScreenStateWithDetails()
    }
}