package com.example.bait2073mobileapplicationdevelopment.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bait2073mobileapplicationdevelopment.dao.DiseaseDao
import com.example.bait2073mobileapplicationdevelopment.dao.DiseaseHospitalDao
import com.example.bait2073mobileapplicationdevelopment.dao.DiseaseRecipeDao
import com.example.bait2073mobileapplicationdevelopment.dao.DiseaseSymptomDao
import com.example.bait2073mobileapplicationdevelopment.dao.EventDao
import com.example.bait2073mobileapplicationdevelopment.dao.HospitalDao
import com.example.bait2073mobileapplicationdevelopment.dao.PersonalizedWorkoutDao
import com.example.bait2073mobileapplicationdevelopment.dao.RecipeDao
import com.example.bait2073mobileapplicationdevelopment.dao.StartWorkoutDao
import com.example.bait2073mobileapplicationdevelopment.dao.SymptomDao
import com.example.bait2073mobileapplicationdevelopment.dao.UserDao
import com.example.bait2073mobileapplicationdevelopment.dao.UserPlanDao
import com.example.bait2073mobileapplicationdevelopment.dao.UserPlanListDao
import com.example.bait2073mobileapplicationdevelopment.entities.Disease
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Hospital
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Recipe
import com.example.bait2073mobileapplicationdevelopment.entities.Disease_Symptom
import com.example.bait2073mobileapplicationdevelopment.entities.Event
import com.example.bait2073mobileapplicationdevelopment.entities.Hospital
import com.example.bait2073mobileapplicationdevelopment.entities.PersonalizedWorkout
import com.example.bait2073mobileapplicationdevelopment.entities.Recipe
import com.example.bait2073mobileapplicationdevelopment.entities.StartWorkout
import com.example.bait2073mobileapplicationdevelopment.entities.Symptom
import com.example.bait2073mobileapplicationdevelopment.entities.User
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlan
import com.example.bait2073mobileapplicationdevelopment.entities.UserPlanList
import com.example.bait2073mobileapplicationdevelopment.entities.Workout
import com.example.bait2073mobileapplicationdevelopment.utilities.DateConverter


@Database(entities = [User::class,PersonalizedWorkout::class,StartWorkout::class, Event::class,UserPlan::class,UserPlanList::class,Workout::class, Disease::class, Symptom::class, Disease_Symptom::class, Recipe::class, Disease_Recipe::class, Hospital::class, Disease_Hospital::class],version = 1,exportSchema = false)
@TypeConverters(DateConverter::class)
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
    abstract fun personalizedWorkoutDao(): PersonalizedWorkoutDao
    abstract fun startWorkoutDao(): StartWorkoutDao

    abstract fun userPlanDao(): UserPlanDao

    abstract fun userPlanListDao(): UserPlanListDao
    abstract fun symptomDao(): SymptomDao
    abstract fun diseaseDao(): DiseaseDao
    abstract fun diseaseSymptomDao(): DiseaseSymptomDao
    abstract fun recipeDao(): RecipeDao
    abstract fun diseaseRecipeDao(): DiseaseRecipeDao
    abstract fun hospitalDao(): HospitalDao
    abstract fun diseaseHospitalDao(): DiseaseHospitalDao
    abstract fun eventDao(): EventDao
}