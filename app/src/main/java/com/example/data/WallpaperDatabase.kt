package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorites")
data class FavoriteWallpaper(
    @PrimaryKey val id: String,
    val title: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY savedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteWallpaper>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    fun isFavorite(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(fav: FavoriteWallpaper)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun removeFavorite(id: String)
}

@Database(entities = [FavoriteWallpaper::class], version = 1, exportSchema = false)
abstract class WallpaperDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
