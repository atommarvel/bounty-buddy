package com.radiantmood.bountybuddy

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.radiantmood.bountybuddy.ui.theme.BountyBuddyTheme

@Composable
fun DebugScreen(
    status: String,
    onFetchClick: () -> Unit,
) {
    BountyBuddyTheme {
        Surface(color = MaterialTheme.colors.background) {
            LazyColumn {
                item { Button(onClick = onFetchClick) { Text("Fetch Operational Data") } }
                item { Text(text = status) }
            }
        }
    }
}

@Composable
fun SimpleExpandableText(text: String) {
    var isExpanded by remember { mutableStateOf(false) }
    val modifier = if (isExpanded) Modifier else Modifier.height(40.dp)
    Text(modifier = modifier.clickable { isExpanded = !isExpanded }, text = text)
}