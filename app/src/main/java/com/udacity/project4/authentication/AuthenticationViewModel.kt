package com.udacity.project4.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class AuthenticationViewModel :ViewModel() {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    private var _intent=MutableLiveData<Boolean>()
    val intent :LiveData<Boolean> get()=_intent

    init {
        _intent.value=false
    }

    fun go_to_reminder_activity(){
        _intent.value=true
    }

    fun go_to_reminder_activity_completed(){
        _intent.value=false
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

}