package hr.algebra.skichrono.utils

import android.location.GnssStatus
import android.location.Location
import hr.algebra.skichrono.model.DashboardData
import hr.algebra.speed.MainActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DashboardUtils {

    fun formatTime(hours: Long, minutes: Long, seconds: Long): String {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun updateParameters(
        location: Location,
        lastLocation: Location?,
        dashboardData: DashboardData,

    ): Location {
        // Update speed and altitude
        dashboardData.currentSpeed = (location.speed * 3.6f).toDouble() // m/s to km/h
        dashboardData.altitude = location.altitude

        if (dashboardData.currentSpeed > dashboardData.maxSpeed) dashboardData.maxSpeed = dashboardData.currentSpeed
        if (dashboardData.altitude > dashboardData.maxAltitude) dashboardData.maxAltitude = dashboardData.altitude
        if (dashboardData.altitude < dashboardData.minAltitude) dashboardData.minAltitude = dashboardData.altitude

        // Calculate distance
        lastLocation?.let {
            val distance = it.distanceTo(location).toDouble() / 1000
            dashboardData.totalDistance += distance

            if (location.altitude < it.altitude) dashboardData.totalSkiDistance += distance
            if (location.altitude > it.altitude) dashboardData.totalLiftDistance += distance



        }
        return location
    }

    fun gnssStatusCallback(callback: (Double) -> Unit) = object : GnssStatus.Callback() {
        override fun onSatelliteStatusChanged(status: GnssStatus) {
            var avgSignalStrength = 0.0
            var satelliteCount = 0

            for (i in 0 until status.satelliteCount) {
                val cn0DbHz = status.getCn0DbHz(i)
                if (cn0DbHz > 0.0) {
                    avgSignalStrength += cn0DbHz
                    satelliteCount++
                }
            }

            if (satelliteCount > 0) {
                avgSignalStrength /= satelliteCount
                MainActivity.dashboardData.gpsSignalStrength = avgSignalStrength
                callback(avgSignalStrength)
            }
        }
    }
    fun getCurrentTime(): String{
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }
    fun getCurrentDateTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }
    fun formatTime(startTime: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        val date: Date = inputFormat.parse(startTime) ?: return ""
        return outputFormat.format(date)
    }
}
