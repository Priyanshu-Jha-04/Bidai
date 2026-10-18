package com.example.bidai.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AdsClick
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bidai.R
import com.example.bidai.ui.theme.Brand
import com.example.bidai.ui.theme.FeatureGray
import com.example.bidai.ui.theme.Ink
import com.example.bidai.ui.theme.Poppins
import com.example.bidai.ui.theme.SearchBorder

private fun poppins(size: Int, weight: FontWeight = FontWeight.Normal, lineHeight: Int = size + 6, color: Color = Color.White) =
    TextStyle(fontFamily = Poppins, fontWeight = weight, fontSize = size.sp, lineHeight = lineHeight.sp, color = color)

//banners
private val HeroBanners = listOf(R.drawable.banner_job)

@Composable
fun HeroBanner(modifier: Modifier = Modifier) {
    val pager = rememberPagerState { HeroBanners.size }
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(pager, Modifier.fillMaxWidth()) { page ->
            Image(
                painter = painterResource(HeroBanners[page]),
                contentDescription = "Find your dream job with BID.AI",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(BorderStroke(1.dp, SearchBorder), RoundedCornerShape(8.dp)),
            )
        }
        if (HeroBanners.size > 1) {
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                repeat(HeroBanners.size) { i ->
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (i == pager.currentPage) Color(0xFF3C3C3C) else Color(0xFFDADADA)),
                    )
                }
            }
        }
    }
}

@Composable
fun FurnitureBanner(onStartSelling: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Brand),
    ) {
        Box(
            Modifier
                .align(Alignment.CenterEnd)
                .width(188.dp)
                .height(105.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.art_chairs),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                Modifier
                    .width(28.dp)
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(listOf(Brand, Color.Transparent))),
            )
        }
        Column(Modifier.padding(start = 14.dp, top = 18.dp).width(190.dp)) {
            Text("From Home to Market\n– List Furniture Fast\nwith BID.ai", style = poppins(14, FontWeight.Bold, 20))
            Spacer(Modifier.height(6.dp))
            Text("Sell your chairs, sofas, tables, and home\ndecor in seconds!", style = poppins(8, lineHeight = 11))
            Spacer(Modifier.height(10.dp))
            PillButton("Start Sell Now", onClick = onStartSelling)
        }
    }
}

@Composable
private fun PillButton(text: String, onClick: () -> Unit) {
    Row(
        Modifier
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(text, style = poppins(8, FontWeight.SemiBold, 11, Ink))
        Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, Modifier.size(9.dp), tint = Ink)
    }
}

@Composable
fun PlanBanner(onRenew: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.horizontalGradient(
                    0f to Color(0xFF1B72E4),
                    0.42f to Color(0xFF5A9BEB),
                    0.7f to Color(0xFF1470E4),
                    1f to Color(0xFF1C75E6),
                ),
            )
            .padding(horizontal = 12.dp, vertical = 16.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.art_hourglass),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 6.dp)
                .size(width = 54.dp, height = 72.dp),
        )
        Column(Modifier.padding(end = 56.dp)) {
            Text("Your Plan is About to Expire!", style = poppins(17, FontWeight.SemiBold, 24))
            Spacer(Modifier.height(4.dp))
            Text("Renew now to keep listing and boosting your products without interruption", style = poppins(12, lineHeight = 17))
            Spacer(Modifier.height(10.dp))
            OutlineWhiteButton("Renew Plan", onClick = onRenew)
        }
    }
}

@Composable
private fun OutlineWhiteButton(text: String, onClick: () -> Unit) {
    Row(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(BorderStroke(1.dp, Color.White), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text, style = poppins(13, FontWeight.Normal, 18))
        Icon(Icons.Outlined.AdsClick, null, Modifier.size(16.dp), tint = Color.White)
    }
}

@Composable
fun FeaturesCard(modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(FeatureGray)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top,
    ) {
        Feature(R.drawable.feature_categories, "More then 13+ Categories are in Bid.ai", Modifier.weight(1f))
        Feature(R.drawable.feature_oneclick, "Sell your unused items in 30 seconds With One click sell", Modifier.weight(1f))
        Feature(R.drawable.feature_referral, "Earn upto ₹100000000 in BID.ai credits by referral!", Modifier.weight(1f))
    }
}

@Composable
private fun Feature(@DrawableRes icon: Int, text: String, modifier: Modifier) {
    Column(modifier.padding(horizontal = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painterResource(icon), null, Modifier.size(46.dp), contentScale = ContentScale.Fit)
        Spacer(Modifier.height(8.dp))
        Text(text, textAlign = TextAlign.Center, style = poppins(10, FontWeight.Medium, 15, Ink))
    }
}

@Composable
fun PropertyBanner(onStartSelling: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(155.dp)
            .background(Brand),
    ) {
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .width(223.dp)
                .height(155.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.art_property),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                Modifier
                    .align(Alignment.CenterEnd)
                    .width(14.dp)
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(listOf(Color.Transparent, Brand))),
            )
        }
        Column(
            Modifier
                .align(Alignment.TopEnd)
                .fillMaxSize()
                .padding(start = 226.dp, end = 12.dp, top = 22.dp, bottom = 16.dp)
                .clickable(onClick = onStartSelling),
        ) {
            Text("Turn Your Property into Profit – List in Just One Click!", style = poppins(15, FontWeight.SemiBold, 20))
            Spacer(Modifier.height(6.dp))
            Text("AI based smart selling. Fast. Efficient.", style = poppins(8, lineHeight = 11))
            Spacer(Modifier.weight(1f))
            Text("#SmartSellRevolution", style = poppins(11, FontWeight.Medium, 15))
        }
    }
}

private class Testimonial(@DrawableRes val photo: Int, val quote: String, val author: String)

private val Testimonials = listOf(
    Testimonial(
        R.drawable.avatar_1,
        "I sold my cooler, sofa, and gym set using BID.ai. Now even my apartment group uses it to sell or swap items. It's that useful!",
        "Neha A, Mumbai",
    ),
    Testimonial(
        R.drawable.avatar_2,
        "I listed my bike on BID.ai just to try it out, and boom – got an offer the same day! No commission, no calls from dealers. Totally smooth.",
        "Ravi S, Pune",
    ),
    Testimonial(
        R.drawable.avatar_3,
        "As a working mom, I don't have time to haggle. BID.ai took care of it all – price, visibility, and even reminders",
        "Anjali , Delhi",
    ),
)

@Composable
fun TestimonialRow(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val gap = 6.dp
        val cardWidth: Dp = (maxWidth - gap * 2) / 3
        LazyRow(horizontalArrangement = Arrangement.spacedBy(gap)) {
            itemsIndexed(Testimonials) { _, t -> TestimonialCard(t, Modifier.width(cardWidth)) }
        }
    }
}

@Composable
private fun TestimonialCard(t: Testimonial, modifier: Modifier) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier
            .height(176.dp)
            .clip(shape)
            .background(Color(0xFFF8FAFF))
            .border(BorderStroke(1.dp, Color(0xFFE2E4E9)), shape),
    ) {
        Box(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 6.dp, vertical = 8.dp)) {
            Text("❝", Modifier.align(Alignment.TopStart), style = poppins(30, FontWeight.Bold, 34, Brand))
            Image(
                painterResource(t.photo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(width = 40.dp, height = 34.dp)
                    .clip(RoundedCornerShape(6.dp)),
            )
            Text(
                t.quote,
                Modifier.align(Alignment.TopStart).padding(top = 44.dp),
                style = poppins(10, FontWeight.Medium, 14, Ink),
            )
        }
        //divider
        Canvas(Modifier.fillMaxWidth().height(1.dp)) {
            drawLine(
                color = Color(0xFFD0D0D0),
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 3.dp.toPx())),
            )
        }
        Text(t.author, Modifier.padding(horizontal = 8.dp, vertical = 8.dp), style = poppins(9, FontWeight.Normal, 12, Ink))
    }
}

@Composable
fun InviteBanner(onInvite: () -> Unit, modifier: Modifier = Modifier) {
    val blob = Color(0xFFF3F3F3)
    Box(
        modifier
            .fillMaxWidth()
            .height(135.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Brand)
            .drawBehind {
                drawCircle(blob, radius = 46.dp.toPx(), center = Offset(-6.dp.toPx(), -10.dp.toPx()))
                drawCircle(blob, radius = 52.dp.toPx(), center = Offset(size.width + 10.dp.toPx(), size.height + 14.dp.toPx()))
            },
    ) {
        Image(
            painterResource(R.drawable.art_megaphone),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .size(100.dp),
        )
        Column(Modifier.padding(start = 31.dp, top = 22.dp, end = 90.dp)) {
            Text("Invite Friends", style = poppins(20, FontWeight.SemiBold, 28))
            Spacer(Modifier.height(4.dp))
            Text("Sign up using my referral code to\nearn up to 1 lakh Bid Credits!", style = poppins(12, lineHeight = 17))
            Spacer(Modifier.height(10.dp))
            OutlineWhiteButton("Invite & Earn", onClick = onInvite)
        }
    }
}
