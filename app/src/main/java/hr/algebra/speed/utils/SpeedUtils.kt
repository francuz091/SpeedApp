package hr.algebra.speed.utils

object SpeedUtils {

    enum class SpeedUnit {
        KILOMETERS_PER_HOUR,
        METERS_PER_SECOND,
        KNOTS
    }

    var currentUnit = SpeedUnit.KILOMETERS_PER_HOUR

    fun cycleUnit(): SpeedUnit {
        currentUnit = when (currentUnit) {
            SpeedUnit.KILOMETERS_PER_HOUR -> SpeedUnit.METERS_PER_SECOND
            SpeedUnit.METERS_PER_SECOND -> SpeedUnit.KNOTS
            SpeedUnit.KNOTS -> SpeedUnit.KILOMETERS_PER_HOUR
        }
        return currentUnit
    }

    fun getUnitLabel(unit: SpeedUnit = currentUnit): String {
        return when (unit) {
            SpeedUnit.KILOMETERS_PER_HOUR -> "km/h"
            SpeedUnit.METERS_PER_SECOND -> "m/s"
            SpeedUnit.KNOTS -> "kt"
        }
    }

    fun convertSpeed(speedInKmH: Double, unit: SpeedUnit = currentUnit): Double {
        return when (unit) {
            SpeedUnit.KILOMETERS_PER_HOUR -> speedInKmH
            SpeedUnit.METERS_PER_SECOND -> speedInKmH / 3.6
            SpeedUnit.KNOTS -> speedInKmH / 1.852
        }
    }
}