package android.waterreminder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.waterreminder.data.store.AppSettingsDataStore
import android.waterreminder.data.entity.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
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
}