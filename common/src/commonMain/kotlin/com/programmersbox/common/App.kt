package com.programmersbox.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun App() {
    val scope = rememberCoroutineScope()
    val db = LocalDatabase.current.listDao()
    val lists by db.getAllLists().collectAsState(emptyList())
    var selected by remember { mutableStateOf<CustomList?>(null) }
    Surface {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(text = "App") })
            },
            bottomBar = {
                BottomAppBar(
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            onClick = {
                                scope.launch {
                                    selected?.let {
                                        db.addToList(
                                            uuid = it.item.uuid,
                                            title = "Title",
                                            description = "Description",
                                            url = UUID.randomUUID().toString(),
                                            imageUrl = "asdf",
                                            source = "asdf"
                                        )
                                    }
                                }
                            }
                        ) { Text("Add") }
                    },
                    actions = {
                        Button(
                            onClick = {
                                scope.launch {
                                    db.create(UUID.randomUUID().toString())
                                }
                            }
                        ) {
                            Text("Create")
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                contentPadding = padding,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(lists) {
                    OutlinedCard(
                        onClick = { selected = it },
                        border = BorderStroke(
                            1.dp,
                            animateColorAsState(
                                if (selected?.item?.uuid == it.item.uuid) Color.Green
                                else MaterialTheme.colorScheme.primary
                            ).value
                        ),
                        modifier = Modifier.animateItemPlacement()
                    ) {
                        ListItem(
                            headlineContent = { Text(it.item.name) },
                            trailingContent = { Text(it.list.size.toString()) }
                        )
                    }
                }
            }
        }
    }
}