package com.campusexchange.app.ui.screens.splash

import androidx.lifecycle.ViewModel
import com.campusexchange.app.data.local.TokenDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenDataStore: TokenDataStore
) : ViewModel() {
    val isLoggedIn: Flow<Boolean> = tokenDataStore.isLoggedIn()
}
