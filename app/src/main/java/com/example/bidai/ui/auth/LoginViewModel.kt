package com.example.bidai.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bidai.data.remote.AppResult
import com.example.bidai.data.repository.AuthRepository
import com.example.bidai.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedIn: Boolean = false,
)

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChange(value: String) = _state.update { it.copy(email = value, emailError = null, error = null) }

    fun onPasswordChange(value: String) = _state.update { it.copy(password = value, passwordError = null, error = null) }

    fun submit() {
        val current = _state.value
        if (current.isLoading) return

        val emailError = Validators.email(current.email)
        val passwordError = Validators.password(current.password)
        if (emailError != null || passwordError != null) {
            _state.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = repository.login(current.email, current.password)) {
                is AppResult.Success -> _state.update { it.copy(isLoading = false, loggedIn = true) }
                is AppResult.Failure -> _state.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }
}
