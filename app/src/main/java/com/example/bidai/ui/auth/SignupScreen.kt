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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bidai.ui.AppViewModelProvider

@Composable
fun SignupScreen(
    onSignedUp: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: SignupViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.signedUp) { if (state.signedUp) onSignedUp() }

    AuthScaffold(waves = AuthWaves.Signup, logoGap = 56.dp) {
        AuthHeading("Join BID.ai\nList fast. Earn smart", "It only takes a click to start your resale journey")

        val gap = Modifier.height(14.dp)
        AuthField(
            label = "Name",
            value = state.name,
            onValueChange = viewModel::onNameChange,
            placeholder = "Enter your name",
            error = state.nameError,
            enabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
        )
        Spacer(gap)
        AuthField(
            label = "Mobile Number",
            value = state.phone,
            onValueChange = viewModel::onPhoneChange,
            placeholder = "Enter mobile number",
            error = state.phoneError,
            enabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
        )
        Spacer(gap)
        AuthField(
            label = "Gmail",
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            placeholder = "abcd@gmail.com",
            error = state.emailError,
            enabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        )
        Spacer(gap)
        AuthField(
            label = "Create Password",
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            placeholder = "**** ****",
            error = state.passwordError,
            enabled = !state.isLoading,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
        )
        Spacer(gap)
        AuthField(
            label = "Confirm Password",
            value = state.confirmPassword,
            onValueChange = viewModel::onConfirmChange,
            placeholder = "**** ****",
            error = state.confirmError,
            enabled = !state.isLoading,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { viewModel.submit() }),
        )

        state.error?.let { AuthError(it) }

        Spacer(Modifier.height(21.dp))
        AuthButton("Sign Up", onClick = viewModel::submit, loading = state.isLoading)
        Spacer(Modifier.height(17.dp))
        AuthFooter("If you already have account ?", "Sign In", onClick = onLoginClick)
    }
}
