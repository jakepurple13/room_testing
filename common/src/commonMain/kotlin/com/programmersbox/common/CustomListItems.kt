package com.programmersbox.common

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.room.*
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import java.util.*

val LocalDatabase = staticCompositionLocalOf<ListDatabase> { error("Nothing") }

fun getRoomDatabase(
    builder: RoomDatabase.Builder<ListDatabase>,
): ListDatabase {
    return builder
        //.addMigrations(MIGRATIONS)
        //.fallbackToDestructiveMigrationOnDowngrade()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

@Database(
    entities = [CustomListItem::class, CustomListInfo::class],
    version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2)]
)
abstract class ListDatabase : RoomDatabase() {

    abstract fun listDao(): ListDao
}

@Dao
interface ListDao {

    @Transaction
    @Query("SELECT * FROM CustomListItem ORDER BY time DESC")
    fun getAllLists(): Flow<List<CustomList>>

    @Transaction
    @Query("SELECT * FROM CustomListItem WHERE :uuid = uuid")
    suspend fun getCustomListItem(uuid: String): CustomList

    @Transaction
    @Query("SELECT * FROM CustomListItem WHERE :uuid = uuid")
    fun getCustomListItemFlow(uuid: String): Flow<CustomList>

    @Insert
    suspend fun createList(listItem: CustomListItem)

    @Insert
    suspend fun addItem(listItem: CustomListInfo)

    @Delete
    suspend fun removeItem(listItem: CustomListInfo)

    @Update
    suspend fun updateList(listItem: CustomListItem)

    @Delete
    suspend fun removeList(item: CustomListItem)

    @Ignore
    suspend fun create(name: String) {
        createList(
            CustomListItem(
                uuid = UUID.randomUUID().toString(),
                name = name,
            )
        )
    }

    @Ignore
    suspend fun removeList(item: CustomList) {
        item.list.forEach { removeItem(it) }
        removeList(item.item)
    }

    @Ignore
    suspend fun updateFullList(item: CustomListItem) {
        updateList(item.copy(time = System.currentTimeMillis()))
    }

    @Ignore
    suspend fun addToList(
        uuid: String,
        title: String,
        description: String,
        url: String,
        imageUrl: String,
        source: String,
    ): Boolean {
        val item = getCustomListItem(uuid)
        return if (item.list.any { it.url == url && it.uuid == uuid }) {
            false
        } else {
            addItem(
                CustomListInfo(
                    uuid = uuid,
                    title = title,
                    description = description,
                    url = url,
                    imageUrl = imageUrl,
                    source = source
                )
            )
            updateFullList(item.item)
            true
        }
    }
}

data class CustomList(
    @Embedded
    val item: CustomListItem,
    @Relation(
        parentColumn = "uuid",
        entityColumn = "uuid"
    )
    val list: List<CustomListInfo>,
)

@Entity(tableName = "CustomListItem")
data class CustomListItem(
    @PrimaryKey
    @ColumnInfo(name = "uuid")
    val uuid: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "time")
    val time: Long = System.currentTimeMillis(),
)

@Entity(tableName = "CustomListInfo")
data class CustomListInfo(
    @PrimaryKey
    @ColumnInfo(defaultValue = "0c65586e-f3dc-4878-be63-b134fb46466c")
    val uniqueId: String = UUID.randomUUID().toString(),
    @ColumnInfo("uuid")
    val uuid: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name = "imageUrl")
    val imageUrl: String,
    @ColumnInfo(name = "sources")
    val source: String,
)

/*
@Entity(tableName = "CustomListGroups")
data class CustomGroups(
    @PrimaryKey
    @ColumnInfo(name = "uuid")
    val uuid: UUID,
)*/
