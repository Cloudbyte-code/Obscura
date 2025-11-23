package com.obscura.game.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obscura.game.R
import com.obscura.game.model.ItemType
import com.obscura.game.model.Player
import com.obscura.game.model.StoreItem

@Composable
fun StoreScreen(
    player: Player,
    items: List<StoreItem>,
    onBuyItem: (StoreItem) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.store),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = stringResource(R.string.your_coins, player.coins),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.avatars),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            items(items.filter { it.type == ItemType.AVATAR }) { item ->
                StoreItemCard(
                    item = item,
                    player = player,
                    onBuy = { onBuyItem(item) }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.accessories),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            items(items.filter { it.type == ItemType.ACCESSORY }) { item ->
                StoreItemCard(
                    item = item,
                    player = player,
                    onBuy = { onBuyItem(item) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Back to Menu",
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun StoreItemCard(
    item: StoreItem,
    player: Player,
    onBuy: () -> Unit
) {
    val isOwned = when (item.type) {
        ItemType.AVATAR -> player.avatarId == item.id
        ItemType.ACCESSORY -> player.accessoryIds.contains(item.id)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = item.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${item.price} coins",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            if (isOwned) {
                Text(
                    text = stringResource(R.string.equipped),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Button(
                    onClick = onBuy,
                    enabled = player.coins >= item.price
                ) {
                    Text(stringResource(R.string.buy))
                }
            }
        }
    }
}
