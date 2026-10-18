package com.example.bidai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bidai.ui.theme.Brand
import com.example.bidai.ui.theme.LogoNavy
import com.example.bidai.ui.theme.Poppins
import com.example.bidai.ui.theme.TextSubtle

@Composable
fun bidaiFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    errorContainerColor = Color.White,
)

@Composable
fun BidaiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    minLines: Int = 1,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null) } },
        trailingIcon = trailingIcon,
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        singleLine = singleLine,
        minLines = minLines,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = bidaiFieldColors(),
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            disabledContentColor = Color.White,
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.5.dp)
        } else {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

//logo
@Composable
fun BrandLogo(modifier: Modifier = Modifier, showTagline: Boolean = false, scale: Float = 1f) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                "BiD",
                color = Brand,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = (30 * scale).sp,
                lineHeight = (34 * scale).sp,
            )
            Text(
                ".ai",
                color = LogoNavy,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = (17 * scale).sp,
                lineHeight = (30 * scale).sp,
            )
        }
        if (showTagline) {
            Text(
                "Buy. Inspect. Deal",
                color = TextSubtle,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = (8 * scale).sp,
                lineHeight = (10 * scale).sp,
            )
        }
    }
}

@Composable
fun ErrorBanner(message: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer, MaterialTheme.shapes.medium)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
        Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
    }
}
