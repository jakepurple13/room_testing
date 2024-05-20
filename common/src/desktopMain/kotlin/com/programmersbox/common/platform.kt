package com.programmersbox.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

public actual fun getPlatformName(): String {
    return "room_testing"
}

@Composable
public fun UIShow() {
    CompositionLocalProvider(
        LocalDatabase provides remember { getRoomDatabase(getDatabaseBuilder()) }
    ) {
        App()
    }
}

fun getDatabaseBuilder(): RoomDatabase.Builder<ListDatabase> {
    //val dbFile = File(System.getProperty("java.io.tmpdir"), "my_room.db")
    val dbFile = File(System.getProperty("user.home") + "/Documents/custom_list.db")
    return Room.databaseBuilder<ListDatabase>(
        name = dbFile.absolutePath,
    )
}