package br.com.sascarvelocidade

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.sascarvelocidade.dao.SpeedLimitDao
import br.com.sascarvelocidade.entity.SpeedLimit
import br.com.sascarvelocidade.repository.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SpeedLimitDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var speedLimitDao: SpeedLimitDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        speedLimitDao = db.speedLimitDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetSpeedLimit() = runBlocking {
        val speedLimit = SpeedLimit(1, "", -18.08080429, -52.04050607, 50, "SW,N")
        speedLimitDao.insert(speedLimit)
        val speedLimits = speedLimitDao.getAll().first()
        assertEquals(speedLimits[0].viaId, speedLimit.speedLimit)
    }

    @Test
    @Throws(Exception::class)
    fun getAllSpeedLimits() = runBlocking {
        val speedLimit = SpeedLimit(1, "", -18.08080429, -52.04050607, 50, "SW,N")
        speedLimitDao.insert(speedLimit)
        val speedLimit2 = SpeedLimit(1, "", -24.08080429, -10.04050607, 50, "SW,N")
        speedLimitDao.insert(speedLimit2)
        val speedLimits = speedLimitDao.getAll().first()
        assertEquals(speedLimits[0].viaId, speedLimit.speedLimit)
        assertEquals(speedLimits[1].viaId, speedLimit.speedLimit)
    }

    @Test
    @Throws(Exception::class)
    fun deleteAll() = runBlocking {
        val speedLimit = SpeedLimit(1, "", -18.08080429, -52.04050607, 50, "SW,N")
        speedLimitDao.insert(speedLimit)
        val speedLimit2 = SpeedLimit(1, "", -28.08080429, -22.04050607, 50, "SW,N")
        speedLimitDao.insert(speedLimit2)
        speedLimitDao.deleteAll()
        val speedLimits = speedLimitDao.getAll().first()
        assertTrue(speedLimits.isEmpty())
    }
}
