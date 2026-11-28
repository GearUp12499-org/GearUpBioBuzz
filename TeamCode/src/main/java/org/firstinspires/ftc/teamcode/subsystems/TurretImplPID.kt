package org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode.subsystems

import android.util.Log
import org.firstinspires.ftc.teamcode.GerthaHardwareMap
import org.firstinspires.ftc.teamcode.hardware.RobotPose
import org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode.hardware.PoseSet
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

class TurretImplPID(private val hw: GerthaHardwareMap) {

    //servo is in CR, counter clockwise is positive ticks, don't know ticks per degree yet

    companion object {
        const val TICKS_PER_DEGREE = 74.959
        const val CLOCKWISE_DEG_LIMIT = -90.0
        const val COUNTER_CLOCKWISE_DEG_LIMIT = 168.0
        const val SLEW_RATE_LIMITER = 0.3
        const val I_DEG_THRESHOLD = 0.0

        var kp = 0.016
        var ki = 0.0006
        var kd = 0.002
    }

    var prevError = 0.0
    var prevTime = 0.0

    var I_sum = 0.0
    var prevPower = 0.0

    //let's just do everything in degrees
    fun update(targetDeg: Double) {

        var targetDeg = targetDeg

        if (targetDeg > COUNTER_CLOCKWISE_DEG_LIMIT) {
            targetDeg = COUNTER_CLOCKWISE_DEG_LIMIT
        } else if (targetDeg < CLOCKWISE_DEG_LIMIT) {
            targetDeg = CLOCKWISE_DEG_LIMIT
        }

        val currentDeg = hw.pollenTurretEncoder.getCurrentPosition() / TICKS_PER_DEGREE
        val errorDeg = targetDeg - currentDeg

        val currentTime = System.nanoTime() / 1e9
        var dt = 0.0
        if (prevTime != 0.0) {
            dt = currentTime - prevTime / 1e9
        }
        prevTime = currentTime

        val p = kp * errorDeg

        val d = if (dt > 0.0) kd * (errorDeg - prevError) / dt else 0.0

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

        prevPower = power

        if (power > 1.0) {
            power = 1.0
        } else if (power < -1.0) {
            power = -1.0
        }

        if (abs(errorDeg) < 1 && hw.pollenTurretEncoder.getVelocity() < 5) {
            return
        }

        hw.pollenTurret.power = power

        Log.i("TurretError", errorDeg.toString())
        Log.i("TurretPower", power.toString())
    }

}