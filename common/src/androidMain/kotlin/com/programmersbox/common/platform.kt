package com.programmersbox.common

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import androidx.room.RoomDatabase

public actual fun getPlatformName(): String {
    return "room_testing"
}

@Composable
public fun UIShow() {
    val context = LocalContext.current
    CompositionLocalProvider(
        LocalDatabase provides remember { getRoomDatabase(getDatabaseBuilder(context)) }
    ) {
        App()
    }
}

fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<ListDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("my_room.db")
    return Room.databaseBuilder<ListDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}