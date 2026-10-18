package com.example.bidai.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bidai.data.model.Product
import com.example.bidai.ui.theme.Brand
import com.example.bidai.ui.theme.CardBorder
import com.example.bidai.ui.theme.Ink
import com.example.bidai.ui.theme.Poppins
import com.example.bidai.ui.theme.StrikeGray
import com.example.bidai.util.formatListedDate
import com.example.bidai.util.formatRupees

private val Muted = TextStyle(fontFamily = Poppins, fontSize = 9.5.sp, lineHeight = 14.sp, color = Color(0xFF5E5E5E))

//product card
@Composable
fun ProductCard(
    product: Product,
    favourite: Boolean,
    onFavourite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(10.dp),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1.44f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            if (product.imageUrl != null) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                )
            } else {
                Icon(
                    Icons.Outlined.ImageNotSupported,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            //logo tab
            Box(
                Modifier
                    .align(Alignment.TopStart)
                    .clip(RoundedCornerShape(bottomEnd = 16.dp))
                    .background(Color.White)
                    .padding(start = 6.dp, end = 10.dp, top = 3.dp, bottom = 3.dp),
            ) {
                BrandLogo(showTagline = true, scale = 0.52f)
            }

            //heart
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = onFavourite),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (favourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (favourite) "Remove from favourites" else "Add to favourites",
                    tint = if (favourite) Color(0xFFE53935) else Ink,
                    modifier = Modifier.size(12.dp),
                )
            }

            if (product.verified) {
                //verified tab
                Row(
                    Modifier
                        .align(Alignment.BottomStart)
                        .clip(RoundedCornerShape(topEnd = 22.dp))
                        .background(Color.White)
                        .padding(start = 6.dp, end = 16.dp, top = 6.dp, bottom = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box {
                        Icon(
                            Icons.Outlined.Inventory2,
                            contentDescription = null,
                            tint = Color(0xFF9C6B4E),
                            modifier = Modifier.size(20.dp).padding(top = 3.dp),
                        )
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Brand,
                            modifier = Modifier.size(10.dp).align(Alignment.TopCenter),
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Verified Product",
                        color = Brand,
                        maxLines = 1,
                        style = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 10.sp),
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, fontSize = 10.5.sp, color = Ink)) {
                        append(formatRupees(product.price))
                    }
                    if (product.originalPrice != null) {
                        withStyle(SpanStyle(fontSize = 8.sp, color = StrikeGray, textDecoration = TextDecoration.LineThrough)) {
                            append("  " + formatRupees(product.originalPrice))
                        }
                    }
                },
                maxLines = 1,
                softWrap = false,
                style = TextStyle(fontFamily = Poppins, letterSpacing = (-0.2).sp),
            )
            Spacer(Modifier.weight(1f))
            if (product.ecoScore != null) {
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, fontSize = 9.5.sp)) { append("Eco") }
                        append(" : ${product.ecoScore}/100")
                    },
                    maxLines = 1,
                    softWrap = false,
                    style = TextStyle(fontFamily = Poppins, fontSize = 8.5.sp, letterSpacing = (-0.2).sp, color = Ink),
                )
            }
        }
        Text(
            product.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp),
            style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Normal, fontSize = 11.5.sp, lineHeight = 17.sp, color = Ink),
        )
        Text(
            product.brand ?: product.productType ?: product.category,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = Muted,
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(product.location.orEmpty(), style = Muted, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
            Spacer(Modifier.width(6.dp))
            Text(formatListedDate(product.createdAt), style = Muted, maxLines = 1)
        }
    }
}
