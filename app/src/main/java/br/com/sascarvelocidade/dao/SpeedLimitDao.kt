package br.com.sascarvelocidade.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import br.com.sascarvelocidade.entity.SpeedLimit
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeedLimitDao {
    @Query("SELECT * FROM speedlimit ORDER BY viaId ASC")
    fun getAll(): Flow<List<SpeedLimit>>

    @Query("SELECT * FROM speedlimit Where latitude LIKE '%' || :latitude || '%' and longitude LIKE '%' || :longitude || '%'")
    fun getByLocation(latitude : Double, longitude: Double): LiveData<SpeedLimit>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(speedLimitEntity: SpeedLimit)

    @Query("DELETE FROM speedlimit")
    suspend fun deleteAll()
}
