package android.waterreminder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.waterreminder.data.store.AppSettingsDataStore
import android.waterreminder.data.entity.AppTheme
import android.waterreminder.service.init.AppInitializer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appInitializer: AppInitializer,
    appSettingsDataStore: AppSettingsDataStore
) : ViewModel() {

    // Transform the preferences flow directly into a type-safe AppTheme StateFlow
    val appTheme: StateFlow<AppTheme> = appSettingsDataStore.settingsFlow
        .map { it.theme }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.SYSTEM
        )

    // Safely initialize user location data in the background on startup
    init {
        viewModelScope.launch(Dispatchers.IO) {
            appInitializer.initializeDefaultUserRegion()
        }
    }
}