package com.yatrisetu.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yatrisetu.app.data.local.dao.CrowdDao
import com.yatrisetu.app.data.local.dao.DestinationDao
import com.yatrisetu.app.data.local.dao.EmergencyContactDao
import com.yatrisetu.app.data.local.dao.TripDao
import com.yatrisetu.app.data.local.entity.CachedCrowdEntity
import com.yatrisetu.app.data.local.entity.DestinationEntity
import com.yatrisetu.app.data.local.entity.EmergencyContactEntity
import com.yatrisetu.app.data.local.entity.TripEntity

@Database(
    entities = [
        DestinationEntity::class,
        CachedCrowdEntity::class,
        TripEntity::class,
        EmergencyContactEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class YatriDatabase : RoomDatabase() {

    abstract fun destinationDao(): DestinationDao
    abstract fun crowdDao(): CrowdDao
    abstract fun tripDao(): TripDao
    abstract fun emergencyContactDao(): EmergencyContactDao

    companion object {
        private const val DB_NAME = "yatri_setu_mobile.db"

        @Volatile
        private var INSTANCE: YatriDatabase? = null

        fun getInstance(context: Context): YatriDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    YatriDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
