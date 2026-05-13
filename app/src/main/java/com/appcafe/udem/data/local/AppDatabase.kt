package com.appcafe.udem.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.appcafe.udem.data.local.dao.*
import com.appcafe.udem.data.local.entities.*

@Database(
    entities = [
        Usuario::class,
        Cafeteria::class,
        Resena::class,
        Favorito::class,
        NoticiaEvento::class,
        Visita::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun coffeeDao(): CoffeeDao
    abstract fun reviewDao(): ReviewDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun newsDao(): NewsDao
    abstract fun visitaDao(): VisitaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cafe_database"
                ).fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
