package hr.algebra.speed

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import hr.algebra.skichrono.model.DashboardData
import hr.algebra.skichrono.utils.DashboardUtils
import hr.algebra.speed.databinding.ActivityMainBinding
import hr.algebra.speed.utils.SpeedUtils

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var lastLocation: Location? = null
    private var startActionn = false


    companion object {
        val dashboardData = DashboardData()
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()

        binding.root.setOnClickListener {
            SpeedUtils.cycleUnit()
            Toast.makeText(this, "Jedinica: ${SpeedUtils.getUnitLabel()}", Toast.LENGTH_SHORT).show()
            updateUI()
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startLocationUpdates()
                startGnssStatusUpdates()
            } else {
                Toast.makeText(this,
                    getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startGnssStatusUpdates() {
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.registerGnssStatusCallback(DashboardUtils.gnssStatusCallback {
            updateGPSStatus(it)
        })
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
            .setMinUpdateIntervalMillis(1000L)
            .build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    lastLocation = DashboardUtils.updateParameters(location, lastLocation, dashboardData)
                    if (startActionn)
                        //updateUIstart()
                    else
                        updateUI()

                }
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun updateGPSStatus(signalStrength: Double) {
        when {
            signalStrength < 20 -> {
                binding.loadingLayout.visibility = View.VISIBLE
                binding.loadingText.text = getString(R.string.waiting_for_gps_signal)
            }
            signalStrength in 20.0..25.0 -> {
                binding.loadingLayout.visibility = View.VISIBLE
                binding.loadingText.text = getString(R.string.gps_signal_low)
            }
            else -> binding.loadingLayout.visibility = View.GONE
        }
    }

    private fun updateUI() {
        val speed = SpeedUtils.convertSpeed(dashboardData.currentSpeed)
        val maxSpeed = SpeedUtils.convertSpeed(dashboardData.maxSpeed)
        val label = SpeedUtils.getUnitLabel()

        binding.tvSpeed.text = String.format("%.1f %s", speed, label)
        binding.tvMaxSpeed.text = String.format("%.1f %s", maxSpeed, label)
    }
}