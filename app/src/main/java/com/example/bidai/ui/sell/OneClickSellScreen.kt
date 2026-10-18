package com.example.bidai.ui.sell

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.bidai.data.model.Catalog
import com.example.bidai.ui.AppViewModelProvider
import com.example.bidai.ui.components.BidaiTextField
import com.example.bidai.ui.components.BottomDock
import com.example.bidai.ui.components.DockTab
import com.example.bidai.ui.components.ErrorBanner
import com.example.bidai.ui.components.PrimaryButton
import com.example.bidai.ui.components.bidaiFieldColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneClickSellScreen(
    onTabSelected: (DockTab) -> Unit,
    onPosted: () -> Unit,
    viewModel: OneClickSellViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia(), viewModel::onImagePicked)

    LaunchedEffect(state.posted) { if (state.posted) onPosted() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("One click sell", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
        bottomBar = { BottomDock(selected = DockTab.OneClickSell, onSelect = onTabSelected) },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize().imePadding(), contentAlignment = Alignment.TopCenter) {
            Column(
                Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                PhotoPicker(
                    imageUri = state.imageUri,
                    onPick = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    onRemove = viewModel::onImageRemoved,
                )
                Spacer(Modifier.height(20.dp))

                BidaiTextField(
                    value = state.title,
                    onValueChange = viewModel::onTitleChange,
                    label = "Title",
                    error = state.titleError,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next),
                    enabled = !state.isLoading,
                )
                Spacer(Modifier.height(8.dp))
                BidaiTextField(
                    value = state.price,
                    onValueChange = viewModel::onPriceChange,
                    label = "Price (₹)",
                    error = state.priceError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                    enabled = !state.isLoading,
                )
                Spacer(Modifier.height(8.dp))
                CategoryDropdown(
                    selected = state.category,
                    error = state.categoryError,
                    onSelected = viewModel::onCategoryChange,
                    enabled = !state.isLoading,
                )
                Spacer(Modifier.height(8.dp))

                Text("Condition", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Catalog.conditions.forEach { (value, label) ->
                        FilterChip(
                            selected = state.condition == value,
                            onClick = { viewModel.onConditionChange(value) },
                            label = { Text(label) },
                            enabled = !state.isLoading,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))

                BidaiTextField(
                    value = state.description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = "Description (optional)",
                    singleLine = false,
                    minLines = 3,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    enabled = !state.isLoading,
                )

                state.error?.let {
                    Spacer(Modifier.height(8.dp))
                    ErrorBanner(it)
                }

                Spacer(Modifier.height(20.dp))
                PrimaryButton("Sell now", onClick = viewModel::submit, loading = state.isLoading)
            }
        }
    }
}

@Composable
private fun PhotoPicker(imageUri: android.net.Uri?, onPick: () -> Unit, onRemove: () -> Unit) {
    val shape = MaterialTheme.shapes.large
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(1.7f)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline), shape)
            .clickable(onClick = onPick),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUri == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Outlined.AddPhotoAlternate, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.height(40.dp))
                Text("Add a photo", style = MaterialTheme.typography.titleMedium)
                Text("Tap to choose from your gallery", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            AsyncImage(model = imageUri, contentDescription = "Selected photo", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.5f), androidx.compose.foundation.shape.CircleShape),
            ) {
                Icon(Icons.Outlined.Close, contentDescription = "Remove photo", tint = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(selected: String, error: String?, onSelected: (String) -> Unit, enabled: Boolean) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { if (enabled) expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            isError = error != null,
            supportingText = error?.let { { Text(it) } },
            enabled = enabled,
            shape = MaterialTheme.shapes.medium,
            colors = bidaiFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Catalog.categoryNames.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onSelected(category)
                        expanded = false
                    },
                )
            }
        }
    }
}
