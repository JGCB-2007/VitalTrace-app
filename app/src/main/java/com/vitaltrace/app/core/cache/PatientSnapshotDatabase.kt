package com.vitaltrace.app.core.cache

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Entity(tableName = "patient_snapshots")
data class PatientSnapshotEntity(
    @PrimaryKey val cacheKey: String,
    val payload: String,
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Dao
interface PatientSnapshotDao {
    @Query("SELECT * FROM patient_snapshots WHERE cacheKey = :key LIMIT 1")
    suspend fun get(key: String): PatientSnapshotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(snapshot: PatientSnapshotEntity)

    @Query("DELETE FROM patient_snapshots WHERE cacheKey LIKE :prefix || '%'")
    suspend fun deleteByPrefix(prefix: String)

    @Query("DELETE FROM patient_snapshots")
    suspend fun clear()
}

@Database(
    entities = [PatientSnapshotEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PatientSnapshotDatabase : RoomDatabase() {
    abstract fun snapshots(): PatientSnapshotDao
}
