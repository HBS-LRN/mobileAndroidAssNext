package com.example.bait2073mobileapplicationdevelopment.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bait2073mobileapplicationdevelopment.dao.UserDao
import com.example.bait2073mobileapplicationdevelopment.entities.User


@Database(entities = [User::class],version = 1,exportSchema = false)
abstract class HealthyLifeDatabase: RoomDatabase()  {

    companion object{

        var healthyLifeDatabase:HealthyLifeDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): HealthyLifeDatabase{
            if (healthyLifeDatabase == null){
                healthyLifeDatabase = Room.databaseBuilder(
                    context,
                    HealthyLifeDatabase::class.java,
                    "healthylife.db"
                ).build()
            }
            return healthyLifeDatabase!!
        }
    }

    abstract fun userDao():UserDao
}