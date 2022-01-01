package com.radiantmood.bountybuddy

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.radiantmood.bountybuddy.data.InventoryItemDefinition
import com.radiantmood.bountybuddy.ui.theme.BountyBuddyTheme

@Composable
fun DebugScreen(
    isLoggedIn: Boolean,
    isLoading: Boolean,
    onLoginClick: () -> Unit,
) {
    BountyBuddyTheme {
        Surface(color = MaterialTheme.colors.background) {
            when {
                isLoading -> LoadingScreen()
                !isLoggedIn -> LoginScreen(onLoginClick)
                else -> BountiesScreen()
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Centerized {
        CircularProgressIndicator()
    }
}

@Composable
fun BountiesScreen() {
    val vm: InventoryViewModel = viewModel()
    val bounties by vm.bounties.observeAsState(listOf())
    LaunchedEffect(true) {
        vm.fetchBounties()
    }
    LazyColumn {
        items(bounties) { bounty ->
            Bounty(bounty)
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
fun Bounty(bounty: InventoryItemDefinition) {
    Row {
        Image(
            painter = rememberImagePainter("https://www.bungie.net/" + bounty.displayProperties.icon),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(bounty.displayProperties.name)
    }
}

@Composable
fun LoginScreen(onLoginClick: () -> Unit) {
    Centerized {
        Button(onClick = onLoginClick) {
            Text("Log in")
        }
    }
}

@Composable
fun Centerized(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
    }
}

@Composable
fun SimpleExpandableText(text: String) {
    var isExpanded by remember { mutableStateOf(false) }
    val modifier = if (isExpanded) Modifier else Modifier.height(40.dp)
    Text(modifier = modifier.clickable { isExpanded = !isExpanded }, text = text)
}