package uz.mobiler.lesson75.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.mobiler.lesson75.database.dao.HitDao
import uz.mobiler.lesson75.database.entity.HitEntity

@Database(entities = [HitEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun hitDao(): HitDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, AppDatabase::class.java, "my_db")
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE!!
        }
    }
}