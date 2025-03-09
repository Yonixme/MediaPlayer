## Project Goal  
The main goal of this project is **background audio playback**.  
- The app plays music files stored on the user's device.  
- Background interaction is managed through a **persistent notification**.  

## Required Permissions  
The app requires the following permissions:  
- `android.permission.READ_EXTERNAL_STORAGE` or `android.permission.READ_MEDIA_AUDIO`  
- `android.permission.POST_NOTIFICATIONS`  

## Project Architecture  
The project follows the **SOLID principles**, the **MVVM design pattern**, and **Clean Architecture**.  

## UI Implementation  
The UI is built using **Android Views** and **Jetpack Navigation**.  

## Asynchronous Operations  
- **Kotlin Coroutines** are used for handling asynchronous tasks.  
- **LiveData** is used for UI-related data updates.  

## Database  
- **Room** is used for database management.  

## Dependency Injection  
- **Hilt** is used for dependency injection.  

## Audio Streaming  
- **MediaPlayer** is used for handling audio playback.  
- Audio playback runs in a **foreground service**, combined with **Notifications** and **BroadcastReceiver**.  

## Supported Formats  
- `.aac`, `.m4a`, `.ogg`, `.wav`, `.mp3`  

## SDK Requirements  
- **Minimum Android SDK:** 23  
- **Target Android SDK:** 35  
