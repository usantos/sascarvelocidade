package br.com.sascarvelocidade.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.sascarvelocidade.dao.SpeedLimitDao
import br.com.sascarvelocidade.entity.SpeedLimit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [SpeedLimit::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun speedLimitDao(): SpeedLimitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            return INSTANCE
                ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "sascar_speedlimit"
                    )
                        .fallbackToDestructiveMigration()
                        .addCallback(
                            SpeedLimitDatabaseCallback(
                                scope
                            )
                        )
                        .build()
                    INSTANCE = instance
                    instance
                }
        }

        private class SpeedLimitDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        resetDatabase(
                            database.speedLimitDao()
                        )
                    }
                }
            }
        }

        suspend fun resetDatabase(speedLimitDao: SpeedLimitDao) {
            speedLimitDao.deleteAll()
        }
    }
}
