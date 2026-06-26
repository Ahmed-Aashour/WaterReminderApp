package android.waterreminder.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.waterreminder.service.init.AppInitializer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appInitializer: AppInitializer
) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            appInitializer.initializeDefaultUserRegion()
        }
    }
}