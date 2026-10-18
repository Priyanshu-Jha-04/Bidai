package com.example.bidai.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.bidai.data.model.Catalog
import com.example.bidai.data.model.Category
import com.example.bidai.data.model.Product
import com.example.bidai.ui.AppViewModelProvider
import com.example.bidai.ui.components.BottomDock
import com.example.bidai.ui.components.BrandLogo
import com.example.bidai.ui.components.DockTab
import com.example.bidai.ui.components.ProductCard
import com.example.bidai.ui.theme.Brand
import com.example.bidai.ui.theme.BrandTint
import com.example.bidai.ui.theme.Ink
import com.example.bidai.ui.theme.Poppins
import com.example.bidai.ui.theme.SearchBorder
import com.example.bidai.ui.theme.SectionGray
import com.example.bidai.util.formatRupees
import kotlinx.coroutines.delay

private val ScreenPadding = 20.dp
private val CardGap = 12.dp

@Composable
fun HomeScreen(
    onTabSelected: (DockTab) -> Unit,
    onLoggedOut: () -> Unit,
    refreshRequested: Boolean,
    onRefreshHandled: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val goSell = { onTabSelected(DockTab.Sell) }

    //refresh flag
    LaunchedEffect(refreshRequested) {
        if (refreshRequested) {
            viewModel.refresh()
            onRefreshHandled()
        }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = { BottomDock(selected = DockTab.Home, onSelect = onTabSelected) },
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            BoxWithConstraints(Modifier.fillMaxSize()) {
                //card width
                val cardWidth = (maxWidth - ScreenPadding * 2 - CardGap) / 2

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    item {
                        Header(
                            userName = state.userName,
                            location = state.location,
                            onLogout = { viewModel.logout(); onLoggedOut() },
                            modifier = Modifier.padding(horizontal = ScreenPadding),
                        )
                    }
                    item {
                        SearchBar(state.search, viewModel::onSearchChange, Modifier.padding(horizontal = ScreenPadding))
                    }
                    if (!state.isFiltering) {
                        item { HeroBanner(Modifier.padding(horizontal = ScreenPadding)) }
                    }
                    item {
                        ExploreCategories(
                            selected = state.category,
                            onSelected = viewModel::onCategorySelected,
                            modifier = Modifier.padding(horizontal = ScreenPadding),
                        )
                    }

                    when {
                        state.isLoading -> item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                        }
                        state.error != null -> item {
                            MessageState(
                                title = "Couldn't load listings",
                                body = state.error.orEmpty(),
                                actionLabel = "Try again",
                                onAction = viewModel::refresh,
                            )
                        }
                        state.isFiltering -> results(state, cardWidth, viewModel, goSell)
                        else -> sections(state, cardWidth, viewModel, goSell)
                    }
                }
            }
        }
    }

    state.selected?.let { ProductSheet(it, onDismiss = { viewModel.onProductSelected(null) }) }
}

//content

private fun LazyListScope.sections(state: HomeUiState, cardWidth: Dp, vm: HomeViewModel, onSell: () -> Unit) {
    item { FurnitureBanner(onStartSelling = onSell, modifier = Modifier.padding(horizontal = ScreenPadding)) }

    item { SectionTitle("Newly Listed Cars") }
    item { BrandChips(state.cars, vm::onBrandSelected) }
    item { ProductRow(state.cars, cardWidth, state, vm) }

    item { SectionTitle("Newly Listed Budget Bikes") }
    item { BrandChips(state.bikes, vm::onBrandSelected) }
    item { ProductRow(state.bikes, cardWidth, state, vm) }

    item { PlanBanner(onRenew = {}, modifier = Modifier.padding(horizontal = ScreenPadding)) }

    if (state.recommendations.isNotEmpty()) {
        item { SectionTitle("Recommendations For You") }
        productGrid(state.recommendations, cardWidth, state, vm)
    }

    item { SectionTitle("Sell and buy every thing with BID.AI") }
    item { FeaturesCard(Modifier.padding(horizontal = ScreenPadding)) }
    item { PropertyBanner(onStartSelling = onSell) }
    item { SectionTitle("Transact with a trusted local community") }
    item { TestimonialRow(Modifier.padding(horizontal = ScreenPadding)) }
    item { InviteBanner(onInvite = {}, modifier = Modifier.padding(horizontal = ScreenPadding)) }
}

private fun LazyListScope.results(state: HomeUiState, cardWidth: Dp, vm: HomeViewModel, onSell: () -> Unit) {
    item {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = ScreenPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                state.category ?: "Results for “${state.search.trim()}”",
                style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Ink),
                modifier = Modifier.weight(1f),
            )
            Text(
                "Clear",
                modifier = Modifier.clickable(onClick = vm::clearFilters).padding(4.dp),
                style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = Brand),
            )
        }
    }
    if (state.results.isEmpty()) {
        item {
            MessageState(
                title = "Nothing here yet",
                body = "Be the first to list something in this category.",
                actionLabel = "Sell an item",
                onAction = onSell,
            )
        }
    } else {
        productGrid(state.results, cardWidth, state, vm)
    }
}

//product grid
private fun LazyListScope.productGrid(products: List<Product>, cardWidth: Dp, state: HomeUiState, vm: HomeViewModel) {
    items(products.chunked(2), key = { row -> row.first().id }) { row ->
        Row(Modifier.padding(horizontal = ScreenPadding), horizontalArrangement = Arrangement.spacedBy(CardGap)) {
            row.forEach { product ->
                ProductCard(
                    product = product,
                    favourite = product.id in state.favourites,
                    onFavourite = { vm.toggleFavourite(product.id) },
                    onClick = { vm.onProductSelected(product) },
                    modifier = Modifier.width(cardWidth),
                )
            }
        }
    }
}

@Composable
private fun ProductRow(section: ProductSection, cardWidth: Dp, state: HomeUiState, vm: HomeViewModel) {
    if (section.products.isEmpty()) {
        Text(
            "No listings yet. Be the first to sell one.",
            modifier = Modifier.padding(horizontal = ScreenPadding),
            style = TextStyle(fontFamily = Poppins, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant),
        )
        return
    }
    LazyRow(
        contentPadding = PaddingValues(horizontal = ScreenPadding),
        horizontalArrangement = Arrangement.spacedBy(CardGap),
    ) {
        items(section.products, key = { it.id }) { product ->
            ProductCard(
                product = product,
                favourite = product.id in state.favourites,
                onFavourite = { vm.toggleFavourite(product.id) },
                onClick = { vm.onProductSelected(product) },
                modifier = Modifier.width(cardWidth),
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        modifier = Modifier.padding(horizontal = ScreenPadding),
        style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp, color = Ink),
    )
}

@Composable
private fun BrandChips(section: ProductSection, onSelected: (category: String, brand: String) -> Unit) {
    if (section.brands.isEmpty()) return
    LazyRow(
        contentPadding = PaddingValues(horizontal = ScreenPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(section.brands) { brand ->
            val selected = brand == section.selectedBrand
            Text(
                brand,
                maxLines = 1,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selected) BrandTint else Color.White)
                    .border(BorderStroke(1.dp, Brand.copy(alpha = 0.7f)), RoundedCornerShape(8.dp))
                    .clickable { onSelected(section.category, brand) }
                    .padding(horizontal = 7.dp, vertical = 2.dp),
                style = TextStyle(fontFamily = Poppins, fontSize = 8.sp, lineHeight = 12.sp, color = Ink),
            )
        }
    }
}

//header

@Composable
private fun Header(userName: String, location: String, onLogout: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        BrandLogo(showTagline = true, scale = 0.62f)
        Spacer(Modifier.width(10.dp))
        Icon(Icons.Outlined.LocationOn, contentDescription = null, modifier = Modifier.size(17.dp), tint = Ink)
        Spacer(Modifier.width(4.dp))
        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(
                location,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
                style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 10.sp, color = Color(0xFF555555)),
            )
            Icon(Icons.Outlined.ExpandMore, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFF555555))
        }
        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", modifier = Modifier.size(19.dp), tint = Ink)
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Favourites", modifier = Modifier.size(19.dp), tint = Ink)
        Spacer(Modifier.width(8.dp))
        Icon(Icons.AutoMirrored.Outlined.Article, contentDescription = "Blog", modifier = Modifier.size(19.dp), tint = Ink)
        Spacer(Modifier.width(8.dp))
        Avatar(userName, onLogout)
    }
}

//avatar
@Composable
private fun Avatar(userName: String, onLogout: () -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        Box(
            Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(Brand)
                .clickable { open = true },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                userName.firstOrNull()?.uppercase() ?: "B",
                style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color.White),
            )
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }, containerColor = Color.White) {
            DropdownMenuItem(
                text = { Text(if (userName.isBlank()) "Log out" else "Log out ($userName)") },
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Logout, null) },
                onClick = { open = false; onLogout() },
            )
        }
    }
}

@Composable
private fun SearchBar(value: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val words = Catalog.categoryNames
    var index by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            index = (index + 1) % words.size
        }
    }
    val text = TextStyle(fontFamily = Poppins, fontSize = 16.sp, lineHeight = 22.sp, color = Ink)
    val shape = RoundedCornerShape(12.dp)
    BasicTextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        textStyle = text,
        cursorBrush = SolidColor(Brand),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions.Default,
        modifier = modifier.fillMaxWidth(),
        decorationBox = { inner ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(shape)
                    .background(Color.White)
                    .border(BorderStroke(1.dp, SearchBorder), shape)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = Ink, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Row {
                            Text("Search For ", style = text.copy(color = Color(0xFF6B6B6B)))
                            Crossfade(targetState = words[index], label = "search hint") { word ->
                                Text(word, style = text.copy(fontWeight = FontWeight.Medium), maxLines = 1)
                            }
                        }
                    }
                    inner()
                }
                if (value.isNotEmpty()) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Clear search",
                        tint = Ink,
                        modifier = Modifier.size(20.dp).clickable { onChange("") },
                    )
                } else {
                    Icon(Icons.Outlined.PhotoCamera, contentDescription = "Search by photo", tint = Ink, modifier = Modifier.size(22.dp))
                }
            }
        },
    )
}

@Composable
private fun ExploreCategories(selected: String?, onSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SectionGray)
            .padding(start = 10.dp, end = 10.dp, top = 14.dp, bottom = 12.dp),
    ) {
        Text(
            "Explore Categories",
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp),
            style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Ink),
        )
        CategoryGrid(selected, onSelected)
    }
}

//category grid
@Composable
fun CategoryGrid(selected: String?, onSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Catalog.categories.chunked(4).forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { category ->
                    CategoryTile(category, category.name == selected, { onSelected(category.name) }, Modifier.weight(1f))
                }
                //last row
                repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun CategoryTile(category: Category, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Column(
        modifier.clickable(
            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(category.icon),
            contentDescription = category.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .then(if (selected) Modifier.border(BorderStroke(2.dp, Brand), CircleShape) else Modifier),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            category.name,
            textAlign = TextAlign.Center,
            maxLines = 1,
            style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 15.sp, color = Ink),
        )
    }
}

//states

@Composable
private fun MessageState(title: String, body: String, actionLabel: String, onAction: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = ScreenPadding, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(Icons.Outlined.WifiOff, null, Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(
            body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        OutlinedButton(onClick = onAction) { Text(actionLabel) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductSheet(product: Product, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (product.images.size > 1) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(product.images) { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.height(180.dp).aspectRatio(1.4f).clip(RoundedCornerShape(12.dp)),
                        )
                    }
                }
            } else {
                SheetImage(product.imageUrl)
            }
            Spacer(Modifier.height(4.dp))
            Text(product.title, style = MaterialTheme.typography.headlineMedium)
            Text(formatRupees(product.price), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            val facts = listOfNotNull(
                Catalog.conditionLabel(product.condition),
                product.brand,
                product.productType,
                product.location,
                "Sold by ${product.seller.name}" + if (product.sellerType == Catalog.SELLER_VENDOR) " (vendor)" else "",
            )
            Text(facts.joinToString(" · "), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (product.description.isNotBlank()) {
                Text(product.description, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
private fun SheetImage(url: String?) {
    val modifier = Modifier.fillMaxWidth().aspectRatio(1.6f).clip(RoundedCornerShape(12.dp))
    if (url == null) {
        Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.ImageNotSupported, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        AsyncImage(model = url, contentDescription = null, contentScale = ContentScale.Crop, modifier = modifier)
    }
}
