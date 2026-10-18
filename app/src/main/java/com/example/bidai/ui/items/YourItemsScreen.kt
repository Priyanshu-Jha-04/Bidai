package com.example.bidai.ui.items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.bidai.data.model.Product
import com.example.bidai.ui.AppViewModelProvider
import com.example.bidai.ui.components.BottomDock
import com.example.bidai.ui.components.DockTab
import com.example.bidai.ui.components.ErrorBanner
import com.example.bidai.ui.theme.Brand
import com.example.bidai.ui.theme.CardBorder
import com.example.bidai.ui.theme.ErrorRed
import com.example.bidai.ui.theme.Ink
import com.example.bidai.ui.theme.Poppins
import com.example.bidai.util.formatListedDate
import com.example.bidai.util.formatRupees

@Composable
fun YourItemsScreen(
    onTabSelected: (DockTab) -> Unit,
    viewModel: YourItemsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    //reload
    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        containerColor = Color.White,
        topBar = { TabTitle("Your items") },
        bottomBar = { BottomDock(selected = DockTab.YourItems, onSelect = onTabSelected) },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.items.isEmpty() -> EmptyState(
                    title = "No items yet",
                    body = state.error ?: "Things you list for sale show up here.",
                    modifier = Modifier.align(Alignment.Center),
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    state.error?.let { item { ErrorBanner(it) } }
                    items(state.items, key = { it.id }) { product ->
                        ItemRow(product, onSold = { viewModel.markSold(product) }, onDelete = { viewModel.delete(product) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemRow(product: Product, onSold: () -> Unit, onDelete: () -> Unit) {
    val shape = RoundedCornerShape(6.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(BorderStroke(1.dp, CardBorder), shape)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val photo = Modifier.size(84.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF6F8FB))
        if (product.imageUrl != null) {
            AsyncImage(product.imageUrl, product.title, photo, contentScale = ContentScale.Crop)
        } else {
            Box(photo, contentAlignment = Alignment.Center) { Icon(Icons.Outlined.ImageNotSupported, null, tint = Color(0xFF6B7280)) }
        }
        Column(Modifier.weight(1f)) {
            Text(
                product.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(fontFamily = Poppins, fontSize = 15.sp, lineHeight = 22.sp, color = Ink),
            )
            Text(formatRupees(product.price), style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Ink))
            val sold = product.status == "sold"
            Text(
                (if (sold) "Sold" else "Active") + " · " + formatListedDate(product.createdAt),
                style = TextStyle(fontFamily = Poppins, fontSize = 11.sp, color = if (sold) ErrorRed else Brand),
            )
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (!sold) TextAction("Mark sold", Brand, onSold)
                TextAction("Delete", ErrorRed, onDelete)
            }
        }
    }
}

@Composable
private fun TextAction(text: String, color: Color, onClick: () -> Unit) {
    Text(
        text,
        modifier = Modifier.clickable(onClick = onClick).padding(vertical = 2.dp),
        style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 12.sp, color = color),
    )
}

//tab title
@Composable
fun TabTitle(text: String) {
    Box(Modifier.fillMaxWidth().statusBarsPadding().height(56.dp), contentAlignment = Alignment.Center) {
        Text(text, style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Ink))
    }
}

@Composable
fun EmptyState(title: String, body: String, modifier: Modifier = Modifier) {
    Column(
        modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(Icons.Outlined.Inventory2, null, Modifier.width(40.dp).height(40.dp), tint = Color(0xFF6B7280))
        Text(title, style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Ink))
        Text(body, textAlign = TextAlign.Center, style = TextStyle(fontFamily = Poppins, fontSize = 13.sp, lineHeight = 20.sp, color = Color(0xFF6B7280)))
    }
}
