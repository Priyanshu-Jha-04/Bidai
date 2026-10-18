package com.example.bidai.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.bidai.ui.components.BottomDock
import com.example.bidai.ui.components.DockTab
import com.example.bidai.ui.items.EmptyState
import com.example.bidai.ui.items.TabTitle

//chat
@Composable
fun ChatScreen(onTabSelected: (DockTab) -> Unit) {
    Scaffold(
        containerColor = Color.White,
        topBar = { TabTitle("Chat") },
        bottomBar = { BottomDock(selected = DockTab.Chat, onSelect = onTabSelected) },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(title = "Chat is coming soon", body = "Message buyers and sellers right from the app.")
        }
    }
}
