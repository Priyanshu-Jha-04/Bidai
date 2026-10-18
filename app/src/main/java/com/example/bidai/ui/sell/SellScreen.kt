package com.example.bidai.ui.sell

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.bidai.R
import com.example.bidai.data.model.Catalog
import com.example.bidai.ui.AppViewModelProvider
import com.example.bidai.ui.components.BottomDock
import com.example.bidai.ui.components.DockTab
import com.example.bidai.ui.components.ErrorBanner
import com.example.bidai.ui.home.CategoryGrid
import com.example.bidai.ui.theme.Brand
import com.example.bidai.ui.theme.ErrorRed
import com.example.bidai.ui.theme.FieldHint
import com.example.bidai.ui.theme.FieldOutline
import com.example.bidai.ui.theme.Ink
import com.example.bidai.ui.theme.Poppins
import com.example.bidai.ui.theme.UploadBorder
import com.example.bidai.ui.theme.UploadFill
import java.io.File

private val Body = TextStyle(fontFamily = Poppins, fontSize = 13.sp, lineHeight = 19.sp, color = Ink)

@Composable
fun SellScreen(
    onTabSelected: (DockTab) -> Unit,
    onBackHome: () -> Unit,
    onPosted: () -> Unit,
    viewModel: SellViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val gallery = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(Catalog.MAX_PHOTOS),
        viewModel::onPhotosPicked,
    )
    var cameraTarget by remember { mutableStateOf<Uri?>(null) }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
        if (saved) cameraTarget?.let { viewModel.onPhotosPicked(listOf(it)) }
    }

    val back = { if (!viewModel.onBack()) onBackHome() }
    BackHandler(onBack = back)

    Scaffold(
        containerColor = Color.White,
        topBar = { SellTopBar(onBack = back) },
        bottomBar = { BottomDock(selected = DockTab.Sell, onSelect = onTabSelected) },
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
            ) {
                val choosePhotos: @Composable () -> Unit = {
                    PhotoChooser(
                        enabled = state.photos.size < Catalog.MAX_PHOTOS,
                        guard = viewModel::canPickPhotos,
                        onGallery = { gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        onCamera = {
                            val dir = File(context.cacheDir, "photos").apply { mkdirs() }
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                File(dir, "cam_${System.currentTimeMillis()}.jpg"),
                            )
                            cameraTarget = uri
                            camera.launch(uri)
                        },
                    )
                }

                when (state.step) {
                    SellStep.Category -> CategoryStep(state, viewModel, choosePhotos)
                    SellStep.Details -> DetailsStep(state, viewModel, choosePhotos)
                }
            }
        }
    }

    if (state.posted) {
        ResultDialog(
            onClose = {
                viewModel.reset()
                onPosted()
            },
        )
    }
}

@Composable
private fun SellTopBar(onBack: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back", tint = Ink, modifier = Modifier.size(30.dp))
        }
        Text("Sell your Product", style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Ink))
    }
}

//step 1

@Composable
private fun CategoryStep(state: SellUiState, vm: SellViewModel, choosePhotos: @Composable () -> Unit) {
    Text(
        "Select Categories",
        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp),
        style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Ink),
    )
    CategoryGrid(selected = state.category, onSelected = vm::onCategorySelected)
    state.categoryError?.let { FieldError(it, Modifier.padding(top = 8.dp)) }
    Spacer(Modifier.height(20.dp))
    UploadZone(height = 245.dp, chooser = choosePhotos)
}

//step 2

@Composable
private fun ColumnScope.DetailsStep(state: SellUiState, vm: SellViewModel, choosePhotos: @Composable () -> Unit) {
    UploadZone(height = 165.dp, chooser = choosePhotos)
    if (state.photos.isNotEmpty()) {
        Spacer(Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(state.photos, key = { it.toString() }) { uri -> PhotoTile(uri, onRemove = { vm.onPhotoRemoved(uri) }) }
        }
    }
    state.photoError?.let { FieldError(it, Modifier.padding(top = 8.dp)) }

    Spacer(Modifier.height(16.dp))
    Label("Sell as")
    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        SellerOption("vendor / Business", state.sellerType == Catalog.SELLER_VENDOR) { vm.onSellerTypeChange(Catalog.SELLER_VENDOR) }
        SellerOption("Individual", state.sellerType == Catalog.SELLER_INDIVIDUAL) { vm.onSellerTypeChange(Catalog.SELLER_INDIVIDUAL) }
    }

    Spacer(Modifier.height(16.dp))
    DesignField("Brands", state.brand, vm::onBrandChange, "Enter Brands", capitalization = KeyboardCapitalization.Words)
    DesignField("Select Product Type", state.productType, vm::onProductTypeChange, "Type", capitalization = KeyboardCapitalization.Words)
    DesignField("Ad Title", state.title, vm::onTitleChange, "Enter Title", error = state.titleError, capitalization = KeyboardCapitalization.Sentences)
    DesignField(
        "Description", state.description, vm::onDescriptionChange, "", singleLine = false, height = 96.dp,
        capitalization = KeyboardCapitalization.Sentences,
    )
    DesignField("Price", state.price, vm::onPriceChange, "Enter price", error = state.priceError, keyboardType = KeyboardType.Decimal)
    DesignField("Location", state.location, vm::onLocationChange, "Enter Location", capitalization = KeyboardCapitalization.Words)

    state.error?.let {
        ErrorBanner(it, Modifier.padding(bottom = 12.dp))
    }
    Box(
        Modifier
            .align(Alignment.CenterHorizontally)
            .width(110.dp)
            .height(38.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (state.isLoading) Brand.copy(alpha = 0.6f) else Brand)
            .clickable(enabled = !state.isLoading, onClick = vm::submit),
        contentAlignment = Alignment.Center,
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            Text("Upload", style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = Color.White))
        }
    }
}

//pieces

//photo button
@Composable
private fun PhotoChooser(
    enabled: Boolean,
    guard: () -> Boolean,
    onGallery: () -> Unit,
    onCamera: () -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    Box {
        Box(
            Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (enabled) Brand else Brand.copy(alpha = 0.5f))
                .clickable(enabled = enabled) { if (guard()) open = true }
                .padding(horizontal = 28.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "Select or Take Photo",
                style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 12.sp, color = Color.White),
            )
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }, containerColor = Color.White) {
            DropdownMenuItem(text = { Text("Choose from gallery") }, onClick = { open = false; onGallery() })
            DropdownMenuItem(text = { Text("Take a photo") }, onClick = { open = false; onCamera() })
        }
    }
}

@Composable
private fun UploadZone(height: Dp, chooser: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(4.dp))
            .background(UploadFill)
            .drawBehind {
                drawRoundRect(
                    color = UploadBorder,
                    cornerRadius = CornerRadius(4.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.dp.toPx(), 2.dp.toPx()))),
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Outlined.AddPhotoAlternate, contentDescription = null, tint = Brand, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(14.dp))
        chooser()
        Spacer(Modifier.height(8.dp))
        Text(
            "Upload photos (Min 2, Max 5, Video Max 30s)",
            style = TextStyle(fontFamily = Poppins, fontSize = 9.sp, lineHeight = 13.sp, color = Ink),
        )
    }
}

@Composable
private fun PhotoTile(uri: Uri, onRemove: () -> Unit) {
    Column(
        Modifier
            .width(98.dp)
            .height(118.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Brand)
            .padding(bottom = 4.dp, start = 4.dp, end = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Photos",
            modifier = Modifier.padding(vertical = 3.dp),
            style = TextStyle(fontFamily = Poppins, fontSize = 8.sp, lineHeight = 11.sp, color = Color.White),
        )
        Box(Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(4.dp))) {
            AsyncImage(
                model = uri,
                contentDescription = "Selected photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = onRemove),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.DeleteOutline, contentDescription = "Remove photo", tint = Ink, modifier = Modifier.size(12.dp))
            }
        }
    }
}

@Composable
private fun SellerOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(BorderStroke(1.dp, FieldOutline), RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(end = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = Brand, unselectedColor = Brand),
        )
        Text(label, style = Body.copy(fontWeight = FontWeight.Medium, fontSize = 14.sp))
    }
}

@Composable
private fun Label(text: String) {
    Text(text, style = Body.copy(fontWeight = FontWeight.Medium, fontSize = 14.sp))
}

@Composable
private fun FieldError(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier = modifier, style = TextStyle(fontFamily = Poppins, fontSize = 11.sp, lineHeight = 15.sp, color = ErrorRed))
}

//field
@Composable
private fun DesignField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    placeholder: String,
    error: String? = null,
    singleLine: Boolean = true,
    height: Dp = 40.dp,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
) {
    Label(label)
    Spacer(Modifier.height(8.dp))
    val shape = RoundedCornerShape(6.dp)
    BasicTextField(
        value = value,
        onValueChange = onChange,
        singleLine = singleLine,
        textStyle = Body,
        cursorBrush = SolidColor(Brand),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, capitalization = capitalization),
        modifier = Modifier.fillMaxWidth(),
        decorationBox = { inner ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(height)
                    .clip(shape)
                    .border(BorderStroke(1.dp, if (error != null) ErrorRed else FieldOutline), shape)
                    .padding(horizontal = 12.dp, vertical = if (singleLine) 0.dp else 10.dp),
                contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart,
            ) {
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(placeholder, style = Body.copy(fontSize = 12.sp, color = FieldHint))
                }
                inner()
            }
        },
    )
    if (error != null) FieldError(error, Modifier.padding(top = 4.dp))
    Spacer(Modifier.height(14.dp))
}

//success dialog
@Composable
private fun ResultDialog(onClose: () -> Unit) {
    Dialog(onDismissRequest = onClose) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(painterResource(R.drawable.dialog_congrats), contentDescription = null, modifier = Modifier.size(width = 112.dp, height = 100.dp))
            Text(
                "Congratulations",
                style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 26.sp, color = Ink),
            )
            Text(
                "Your product get Approved  and its live Now",
                textAlign = TextAlign.Center,
                style = TextStyle(fontFamily = Poppins, fontSize = 12.sp, lineHeight = 18.sp, color = Ink),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Close",
                modifier = Modifier.clickable(onClick = onClose).padding(8.dp),
                style = TextStyle(fontFamily = Poppins, fontSize = 10.sp, color = Color(0xFF6B6B6B)),
            )
        }
    }
}
