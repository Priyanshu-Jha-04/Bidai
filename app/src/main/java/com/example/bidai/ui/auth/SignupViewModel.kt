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

data class SignupUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val signedUp: Boolean = false,
)

class SignupViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(SignupUiState())
    val state: StateFlow<SignupUiState> = _state.asStateFlow()

    fun onNameChange(value: String) = _state.update { it.copy(name = value, nameError = null, error = null) }

    fun onEmailChange(value: String) = _state.update { it.copy(email = value, emailError = null, error = null) }

    fun onPhoneChange(value: String) {
        //phone digits
        if (value.length > 10 || value.any { !it.isDigit() }) return
        _state.update { it.copy(phone = value, phoneError = null, error = null) }
    }

    fun onPasswordChange(value: String) = _state.update { it.copy(password = value, passwordError = null, error = null) }

    fun onConfirmChange(value: String) = _state.update { it.copy(confirmPassword = value, confirmError = null, error = null) }

    fun submit() {
        val s = _state.value
        if (s.isLoading) return

        val nameError = Validators.name(s.name)
        val emailError = Validators.email(s.email)
        val phoneError = Validators.phone(s.phone)
        val passwordError = Validators.newPassword(s.password)
        val confirmError = Validators.confirmPassword(s.password, s.confirmPassword)
        if (listOfNotNull(nameError, emailError, phoneError, passwordError, confirmError).isNotEmpty()) {
            _state.update {
                it.copy(
                    nameError = nameError,
                    emailError = emailError,
                    phoneError = phoneError,
                    passwordError = passwordError,
                    confirmError = confirmError,
                )
            }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = repository.signup(s.name, s.email, s.phone, s.password)) {
                is AppResult.Success -> _state.update { it.copy(isLoading = false, signedUp = true) }
                is AppResult.Failure -> _state.update {
                    it.copy(
                        isLoading = false,
                        //field errors
                        nameError = result.fieldErrors["name"],
                        emailError = result.fieldErrors["email"],
                        phoneError = result.fieldErrors["phone"],
                        passwordError = result.fieldErrors["password"],
                        error = result.message.takeIf { result.fieldErrors.isEmpty() },
                    )
                }
            }
        }
    }
}
