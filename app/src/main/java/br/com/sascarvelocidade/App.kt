package br.com.sascarvelocidade

import android.app.Application
import br.com.sascarvelocidade.repository.AppDatabase
import br.com.sascarvelocidade.repository.SpeedLimitRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class App : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { SpeedLimitRepository(database.speedLimitDao()) }
}