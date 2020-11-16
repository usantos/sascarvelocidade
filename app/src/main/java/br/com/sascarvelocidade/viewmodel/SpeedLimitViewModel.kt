package br.com.sascarvelocidade.viewmodel

import androidx.lifecycle.*
import br.com.sascarvelocidade.entity.SpeedLimit
import br.com.sascarvelocidade.repository.SpeedLimitRepository
import kotlinx.coroutines.launch

class SpeedLimitViewModel(private val repository: SpeedLimitRepository) : ViewModel() {

    val allSpeedLimits: LiveData<List<SpeedLimit>> = repository.allSpeedLimits.asLiveData()

    fun insert(speedLimit: SpeedLimit) = viewModelScope.launch {
        repository.insert(speedLimit)
    }

    suspend fun getByLocation(latitude: Double, longitude: Double): LiveData<SpeedLimit> {
        return repository.getByLocation(latitude, longitude)
    }
}

class SpeedLimitViewModelFactory(private val repository: SpeedLimitRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpeedLimitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SpeedLimitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
