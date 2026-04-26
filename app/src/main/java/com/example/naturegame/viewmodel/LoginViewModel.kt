package com.example.naturegame.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _loginState = MutableStateFlow<Boolean?>(null)
    val loginState: StateFlow<Boolean?> = _loginState

    init {
        signIn()
    }

    private fun signIn() {
        auth.signInAnonymously()
            .addOnSuccessListener {
                _loginState.value = true
            }
            .addOnFailureListener {
                _loginState.value = false
            }
    }
}
