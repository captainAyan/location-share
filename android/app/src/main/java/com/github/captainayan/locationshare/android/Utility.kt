import kotlin.math.pow
import kotlin.math.sqrt

class Utility {

    companion object {
        fun getLocationDifference(x1: Double, y1: Double, x2: Double, y2: Double): Double {
            return sqrt((y2 - y1).pow(2) + (x2 - x1).pow(2))
        }
    }
}
