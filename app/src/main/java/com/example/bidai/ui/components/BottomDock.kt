package com.example.bidai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.AdsClick
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bidai.ui.theme.NavBlue
import com.example.bidai.ui.theme.Poppins

enum class DockTab(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
) {
    Home("Home", Icons.Outlined.Home, Icons.Filled.Home),
    Sell("Sell", Icons.Outlined.Storefront, Icons.Filled.Storefront),
    OneClickSell("One click sell", Icons.Outlined.AdsClick, Icons.Outlined.AdsClick),
    Chat("Chat", Icons.Outlined.Sms, Icons.Outlined.Sms),
    YourItems("Your items", Icons.Outlined.ShoppingBag, Icons.Outlined.ShoppingBag),
}

//dock
@Composable
fun BottomDock(selected: DockTab, onSelect: (DockTab) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .height(66.dp)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DockTab.entries.forEach { tab ->
            val active = tab == selected
            val tint = if (active) NavBlue else Color(0xFF111111)
            Column(
                Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { if (!active) onSelect(tab) },
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = if (active) tab.selectedIcon else tab.icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(26.dp),
                )
                Text(
                    tab.label,
                    color = tint,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 3.dp),
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                    ),
                )
            }
        }
    }
}
