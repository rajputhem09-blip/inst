package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cached_posts")
data class CachedPostEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String,
    val isVerified: Boolean,
    val caption: String,
    val mediaUrlsJson: String,
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val isSponsored: Boolean,
    val timestamp: Long
)

@Entity(tableName = "saved_posts")
data class SavedPostEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "blocked_users")
data class BlockedUserEntity(
    @PrimaryKey val userId: String,
    val username: String,
    val blockedAt: Long = System.currentTimeMillis()
)

@Dao
interface LykoDao {
    @Query("SELECT * FROM cached_posts ORDER BY timestamp DESC")
    fun getAllCachedPosts(): Flow<List<CachedPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<CachedPostEntity>)

    @Query("UPDATE cached_posts SET isLiked = :isLiked, likesCount = :likesCount WHERE id = :postId")
    suspend fun updatePostLike(postId: String, isLiked: Boolean, likesCount: Int)

    @Query("UPDATE cached_posts SET isSaved = :isSaved WHERE id = :postId")
    suspend fun updatePostSaved(postId: String, isSaved: Boolean)

    @Query("SELECT * FROM saved_posts")
    fun getAllSavedPostIds(): Flow<List<SavedPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePost(entity: SavedPostEntity)

    @Query("DELETE FROM saved_posts WHERE postId = :postId")
    suspend fun removeSavedPost(postId: String)

    @Query("SELECT * FROM blocked_users")
    fun getBlockedUsers(): Flow<List<BlockedUserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun blockUser(entity: BlockedUserEntity)

    @Query("DELETE FROM blocked_users WHERE userId = :userId")
    suspend fun unblockUser(userId: String)
}

@Database(
    entities = [CachedPostEntity::class, SavedPostEntity::class, BlockedUserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LykoDatabase : RoomDatabase() {
    abstract fun lykoDao(): LykoDao

    companion object {
        @Volatile
        private var INSTANCE: LykoDatabase? = null

        fun getDatabase(context: Context): LykoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LykoDatabase::class.java,
                    "lyko_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
