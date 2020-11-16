package br.com.sascarvelocidade

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.observe
import br.com.sascarvelocidade.entity.SpeedLimit
import br.com.sascarvelocidade.repository.Endpoint
import br.com.sascarvelocidade.repository.NetworkUtils
import br.com.sascarvelocidade.viewmodel.SpeedLimitViewModel
import br.com.sascarvelocidade.viewmodel.SpeedLimitViewModelFactory
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {

    // declare a global variable FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // globally declare LocationRequest
    private lateinit var locationRequest: LocationRequest

    // globally declare LocationCallback
    private lateinit var locationCallback: LocationCallback

    private var location: Location? = null

    private var _scaleLatLng = 4

    private val speedLimitViewModel: SpeedLimitViewModel by viewModels {
        SpeedLimitViewModelFactory((application as App).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext!!)

        speedView.speedTo(0.0F, 1000)
        speedView.withTremble = false

        //Get Speed Limits in the Api and insert in the local database
        getDataForOffline()

        getLocationUpdates()

    }

    private fun getLocationUpdates() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext!!)
        locationRequest = LocationRequest()
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 1000
        locationRequest.smallestDisplacement = 100f // 170 m
        locationRequest.priority =
            LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                if (locationResult.locations.isNotEmpty()) {
                    location = locationResult.lastLocation

                    location ?: return

                    GlobalScope.launch(Dispatchers.Main) { getByLocation(location!!) }
                }
            }
        }
    }

    //start location updates
    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    // stop location updates
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // stop receiving location update when activity not visible/foreground
    override fun onPause() {
        super.onPause()
        //stopLocationUpdates()
    }

    // start receiving location update when activity  visible/foreground
    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ==
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    //TODO Warn that it will only work with permission
                    //Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }


    //Todo refactor to use this in ViewModel
    private fun getDataForOffline() {
        val retrofitClient = NetworkUtils
            .getRetrofitInstance(BuildConfig.SASCAR_BASE_URL)

        val endpoint = retrofitClient.create(Endpoint::class.java)
        val callback = endpoint.retrieveAll()

        callback.enqueue(object : Callback<List<SpeedLimit>> {
            override fun onFailure(call: Call<List<SpeedLimit>>, t: Throwable) {
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<List<SpeedLimit>>,
                response: Response<List<SpeedLimit>>
            ) {
                response.body()?.forEach { it ->
                    val speedLimit = SpeedLimit(
                        it.viaId,
                        it.viaName,
                        it.latitude,
                        it.longitude,
                        it.speedLimit,
                        it.direction
                    )

                    speedLimitViewModel.insert(speedLimit)
                }
            }
        })

    }

    private suspend fun getByLocation(location: Location) {

        speedLimitViewModel.getByLocation(
            BigDecimal(location.latitude).setScale(_scaleLatLng, RoundingMode.UP).toDouble(),
            BigDecimal(location.longitude).setScale(_scaleLatLng, RoundingMode.UP).toDouble()
        )
            .observe(owner = this) { speedLimit ->

                if (speedLimit != null
                    && BigDecimal(speedLimit.latitude)
                        .setScale(_scaleLatLng, RoundingMode.UP)
                        .toDouble().equals(
                        BigDecimal(location.latitude)
                            .setScale(_scaleLatLng, RoundingMode.UP)
                            .toDouble()
                    )
                    && BigDecimal(speedLimit.longitude)
                        .setScale(_scaleLatLng, RoundingMode.DOWN)
                        .toDouble().equals(
                        BigDecimal(location.longitude)
                            .setScale(_scaleLatLng, RoundingMode.DOWN)
                            .toDouble()
                    )
                ) {
                    //speedLimitTextView.textSize = 18.0F
                    //speedLimitTextView.text = "Velocidade da Via: " + speedLimit.speedLimit.toString() + "Km/h"
                    speedView.speedTo(speedLimit.speedLimit.toFloat(), 1000)
                    speedView.withTremble = false
                    Log.d("sascar", getString(R.string.in_location))
                } else {
                    //speedLimitTextView.text = getString(R.string.not_in_location)
                    Log.d("sascar", getString(R.string.not_in_location))
                }
            }
    }
}