package com.example.bidai.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bidai.ui.components.BrandLogo
import com.example.bidai.ui.theme.Brand
import com.example.bidai.ui.theme.ErrorRed
import com.example.bidai.ui.theme.FieldBackground
import com.example.bidai.ui.theme.FieldBorder
import com.example.bidai.ui.theme.LinkBlue
import com.example.bidai.ui.theme.Ink
import com.example.bidai.ui.theme.Placeholder
import com.example.bidai.ui.theme.Poppins
import com.example.bidai.ui.theme.TextDark
import com.example.bidai.ui.theme.TextSubtle

//corner shapes
class AuthWaves(
    //top right
    val top: List<Offset>,
    //bottom left
    val bottom: List<Offset>,
) {
    companion object {
        val Login = AuthWaves(
            top = pts(
                0.187f, 0.000f, 0.215f, 0.017f, 0.274f, 0.032f, 0.353f, 0.047f, 0.437f, 0.062f,
                0.515f, 0.077f, 0.580f, 0.092f, 0.632f, 0.107f, 0.675f, 0.122f, 0.710f, 0.138f,
                0.740f, 0.153f, 0.767f, 0.168f, 0.791f, 0.183f, 0.815f, 0.198f, 0.837f, 0.213f,
                0.861f, 0.228f, 0.885f, 0.243f, 0.913f, 0.259f, 0.943f, 0.274f, 1.000f, 0.294f,
            ),
            bottom = pts(
                0.000f, 0.760f, 0.038f, 0.776f, 0.062f, 0.792f, 0.083f, 0.808f, 0.103f, 0.824f,
                0.123f, 0.840f, 0.144f, 0.856f, 0.167f, 0.871f, 0.195f, 0.887f, 0.228f, 0.903f,
                0.269f, 0.919f, 0.317f, 0.935f, 0.375f, 0.951f, 0.445f, 0.967f, 0.600f, 1.000f,
            ),
        )

        val Signup = AuthWaves(
            top = pts(
                0.199f, 0.000f, 0.232f, 0.016f, 0.293f, 0.031f, 0.370f, 0.046f, 0.453f, 0.061f,
                0.530f, 0.076f, 0.594f, 0.091f, 0.645f, 0.106f, 0.687f, 0.121f, 0.721f, 0.136f,
                0.751f, 0.151f, 0.778f, 0.166f, 0.802f, 0.181f, 0.825f, 0.196f, 0.848f, 0.211f,
                0.871f, 0.226f, 0.895f, 0.240f, 0.921f, 0.255f, 0.950f, 0.270f, 1.000f, 0.290f,
            ),
            bottom = pts(
                0.000f, 0.800f, 0.030f, 0.813f, 0.047f, 0.825f, 0.064f, 0.838f, 0.081f, 0.851f,
                0.099f, 0.864f, 0.118f, 0.877f, 0.141f, 0.890f, 0.167f, 0.903f, 0.197f, 0.915f,
                0.233f, 0.928f, 0.276f, 0.941f, 0.326f, 0.954f, 0.384f, 0.967f, 0.540f, 1.000f,
            ),
        )

        private fun pts(vararg v: Float) = List(v.size / 2) { Offset(v[it * 2], v[it * 2 + 1]) }
    }
}

private const val DESIGN_WIDTH = 390f
private const val DESIGN_HEIGHT = 844f

@Composable
fun AuthBackground(waves: AuthWaves, modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxSize()) {
        //layout
        val frameHeight = size.width * DESIGN_HEIGHT / DESIGN_WIDTH
        fun top(p: Offset) = Offset(p.x * size.width, p.y * frameHeight)
        fun bottom(p: Offset) = Offset(p.x * size.width, size.height - (1f - p.y) * frameHeight)

        val topPts = waves.top.map(::top)
        val topPath = Path().apply {
            moveTo(topPts.first().x, topPts.first().y - 1f)
            smoothThrough(topPts)
            lineTo(size.width + 1f, topPts.last().y)
            lineTo(size.width + 1f, -1f)
            close()
        }
        drawPath(topPath, Brand)

        val bottomPts = waves.bottom.map(::bottom)
        val bottomPath = Path().apply {
            moveTo(-1f, bottomPts.first().y)
            lineTo(bottomPts.first().x, bottomPts.first().y)
            smoothThrough(bottomPts)
            lineTo(bottomPts.last().x, size.height + 1f)
            lineTo(-1f, size.height + 1f)
            close()
        }
        drawPath(bottomPath, Brand)
    }
}

//curve
private fun Path.smoothThrough(pts: List<Offset>) {
    for (i in 0 until pts.size - 1) {
        val p0 = pts[maxOf(i - 1, 0)]
        val p1 = pts[i]
        val p2 = pts[i + 1]
        val p3 = pts[minOf(i + 2, pts.size - 1)]
        cubicTo(
            p1.x + (p2.x - p0.x) / 6f, p1.y + (p2.y - p0.y) / 6f,
            p2.x - (p3.x - p1.x) / 6f, p2.y - (p3.y - p1.y) / 6f,
            p2.x, p2.y,
        )
    }
}

//auth screen
@Composable
fun AuthScaffold(
    waves: AuthWaves,
    logoGap: Dp,
    content: @Composable () -> Unit,
) {
    Box(Modifier.fillMaxSize().background(Color.White)) {
        AuthBackground(waves)
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BrandLogo(
                showTagline = true,
                scale = 1.12f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 8.dp),
            )
            Spacer(Modifier.height(logoGap))
            Column(
                Modifier
                    .widthIn(max = 440.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .padding(bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                content()
            }
        }
    }
}

@Composable
fun AuthHeading(title: String, subtitle: String) {
    Text(
        title,
        color = Ink,
        textAlign = TextAlign.Center,
        style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp),
    )
    Spacer(Modifier.height(4.dp))
    Text(
        subtitle,
        color = TextSubtle,
        textAlign = TextAlign.Center,
        style = TextStyle(fontFamily = Poppins, fontSize = 14.sp, lineHeight = 21.sp),
    )
    Spacer(Modifier.height(18.dp))
}

//field label
@Composable
fun AuthField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val textStyle = TextStyle(fontFamily = Poppins, fontSize = 13.sp, lineHeight = 20.sp, color = TextDark)
    Column(modifier.fillMaxWidth()) {
        Text(label, style = textStyle)
        Spacer(Modifier.height(4.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = true,
            textStyle = textStyle,
            cursorBrush = SolidColor(Brand),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            decorationBox = { inner ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(FieldBackground, RoundedCornerShape(8.dp))
                        .border(1.dp, if (error != null) ErrorRed else FieldBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty()) Text(placeholder, style = textStyle.copy(color = Placeholder))
                    inner()
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
        if (error != null) {
            Text(
                error,
                color = ErrorRed,
                modifier = Modifier.padding(start = 2.dp, top = 2.dp),
                style = TextStyle(fontFamily = Poppins, fontSize = 11.sp, lineHeight = 16.sp),
            )
        }
    }
}

@Composable
fun AuthButton(text: String, onClick: () -> Unit, loading: Boolean) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(if (loading) Brand.copy(alpha = 0.7f) else Brand, RoundedCornerShape(10.dp))
            .clickable(enabled = !loading, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            Text(text, color = Color.White, style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 16.sp))
        }
    }
}

@Composable
fun AuthFooter(prompt: String, action: String, onClick: () -> Unit) {
    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        val style = TextStyle(fontFamily = Poppins, fontSize = 16.sp, lineHeight = 24.sp)
        Text("$prompt ", color = TextDark, style = style)
        Text(action, color = LinkBlue, style = style, modifier = Modifier.clickable(onClick = onClick))
    }
}

@Composable
fun AuthError(message: String) {
    Text(
        message,
        color = ErrorRed,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        style = TextStyle(fontFamily = Poppins, fontSize = 12.sp, lineHeight = 18.sp),
    )
}
