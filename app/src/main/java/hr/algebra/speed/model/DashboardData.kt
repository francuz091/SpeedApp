package hr.algebra.skichrono.model

data class DashboardData(
    var currentSpeed: Double = 0.0,
    var maxSpeed: Double = 0.0,
    var totalDistance: Double = 0.0,
    var totalSkiDistance: Double = 0.0,
    var totalLiftDistance: Double = 0.0,
    var activityTime: Double = 0.0,
    var liftTime: Double = 0.0,
    var skiTime: Double = 0.0,
    var startTime: String = "HH:mm:ss",
    var endTime: String = "HH:mm:ss",
    var altitude: Double = 0.0,
    var maxAltitude: Double = 0.0,
    var minAltitude: Double = Double.MAX_VALUE,
    var verticalDistance: Double = 0.0,
    var numberOfRuns: Int = 0,
    var gpsSignalStrength: Double = 0.0,
    var speeds: MutableList<Double> = mutableListOf()
){
fun reset() {
    endTime = "HH:mm:ss"
    currentSpeed = 0.0
    altitude = 0.0
    maxSpeed = 0.0
    minAltitude = Double.MAX_VALUE
    maxAltitude = Double.MIN_VALUE
    totalDistance = 0.0
    totalSkiDistance = 0.0
    gpsSignalStrength = 0.0
    speeds.clear()
}

fun calculateAverageSpeed(): Double {
    return if (speeds.isNotEmpty()) {
        speeds.sum() / speeds.size
    } else {
        0.0
    }
}
}
