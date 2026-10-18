package com.example.bidai.ui.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bidai.ui.AppViewModelProvider

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    onSignupClick: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.loggedIn) { if (state.loggedIn) onLoggedIn() }

    AuthScaffold(waves = AuthWaves.Login, logoGap = 150.dp) {
        AuthHeading("Welcome Back  to Bid.ai", "Sign in Your next deal is just a tap away.")

        AuthField(
            label = "Gmail",
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            placeholder = "Enter Gmail",
            error = state.emailError,
            enabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        )
        Spacer(Modifier.height(14.dp))
        AuthField(
            label = "Password",
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            placeholder = "Enter password",
            error = state.passwordError,
            enabled = !state.isLoading,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { viewModel.submit() }),
        )

        state.error?.let { AuthError(it) }

        Spacer(Modifier.height(21.dp))
        AuthButton("Sign in", onClick = viewModel::submit, loading = state.isLoading)
        Spacer(Modifier.height(21.dp))
        AuthFooter("Don't you have an account?", "Sign up", onClick = onSignupClick)
    }
}
