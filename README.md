## Project Goal  
The main goal of this project is **background audio playback**. The app plays music files stored on the user's device, and background interaction is managed through a **persistent notification**.  

## Required Permissions  
The app requires the following permissions: `android.permission.READ_EXTERNAL_STORAGE` or `android.permission.READ_MEDIA_AUDIO`, and `android.permission.POST_NOTIFICATIONS`.  

## Project Architecture  
This project follows the **SOLID principles**, the **MVVM design pattern**, and **Clean Architecture**.  

## UI Implementation  
The UI is built using **Android Views** and **Jetpack Navigation**.  

## Asynchronous Operations  
Kotlin Coroutines are used for handling asynchronous tasks, while LiveData is utilized for UI-related data updates.  

## Database  
Room is used for database management.  

## Dependency Injection  
Hilt is used for dependency injection.  

## Audio Streaming  
MediaPlayer is responsible for handling audio playback. The playback runs in a **foreground service**, which works alongside **Notifications** and **BroadcastReceiver**.  

## Supported Formats  
The application supports the following audio formats: `.aac`, `.m4a`, `.ogg`, `.wav`, and `.mp3`.  

## SDK Requirements  
The minimum required Android SDK is **23**, while the target SDK version is **35**.  
