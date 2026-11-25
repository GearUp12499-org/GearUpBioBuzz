package org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode.subsystems

import org.firstinspires.ftc.teamcode.GerthaHardwareMap
import kotlin.math.abs

class TurretImplPID(private val hw: GerthaHardwareMap) {

    //servo is in CR, counter clockwise is positive ticks, don't know ticks per degree yet

    companion object {
        const val TICKS_PER_DEGREE = 0.0
        const val CLOCKWISE_TICK_LIMIT = 0.0
        const val COUNTER_CLOCKWISE_TICK_LIMIT = 0.0
        const val SLEW_RATE_LIMITER = 0.0
        const val I_DEG_THRESHOLD = 0.0

        const val kp = 0.0
        const val ki = 0.0
        const val kd = 0.0
    }

    var targetDeg = 0.0 //degrees
    var prevError = 0.0
    var prevTime = 0.0

    var I_sum = 0.0
    var prevPower = 0.0

    //let's just do everything in degrees
    fun update() {

        if (targetDeg > COUNTER_CLOCKWISE_TICK_LIMIT) {
            targetDeg = COUNTER_CLOCKWISE_TICK_LIMIT
        } else if (targetDeg < CLOCKWISE_TICK_LIMIT) {
            targetDeg = CLOCKWISE_TICK_LIMIT
        }

        val currentDeg = hw.pollenTurretEncoder.getCurrentPosition()
        val errorDeg = targetDeg - currentDeg

        val currentTime = System.nanoTime()/1e9
        var dt = 0.0
        if (prevTime != 0.0) {
            dt = currentTime - prevTime / 1e9
        }
        prevTime = currentTime

        val p = kp * errorDeg

        val d = if (dt > 0.0) kd*(errorDeg - prevError) / dt else 0.0

        if (abs(errorDeg) < I_DEG_THRESHOLD) {
            I_sum += errorDeg * dt
        } else {
            I_sum = 0.0
        }
        val i = ki * I_sum

        var power = p + i + d


        power = when {
            power > prevPower + SLEW_RATE_LIMITER -> prevPower + SLEW_RATE_LIMITER
            power < prevPower - SLEW_RATE_LIMITER -> prevPower - SLEW_RATE_LIMITER
            else -> power
        }

        if (power > 1.0) {
            power = 1.0
        } else if (power < -1.0){
            power = -1.0
        }












    }

}