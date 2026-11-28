package org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode.subsystems

import android.util.Log
import org.firstinspires.ftc.teamcode.GerthaHardwareMap
import org.firstinspires.ftc.teamcode.GerthaHardwareMap.*
import org.firstinspires.ftc.teamcode.ShooterImpl
import org.firstinspires.ftc.teamcode.hardware.RobotPose
import org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode.hardware.PoseSet
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.tan

class AimingMachine(private val hw: GerthaHardwareMap, private val poseSet: PoseSet) {

    enum class State {
        FULL,
        NO_TURRET,

        NO_NOTHING,
        AUTO,
        TURRET_NO_SHOOTER
    }

    lateinit var state: State

    lateinit var prevState: State

    var justTransitioned = true

    val shooterImpl = ShooterImpl(hw)
    val turretImplPID = TurretImplPID(hw)

    var targetPose = poseSet.LEFT_HIVE_AT

    var constantVel = 0.0

    fun setHardcode(vel: Double) {
        constantVel = vel
    }

    fun init() {
        state = State.FULL
        prevState = State.FULL
        targetPose = poseSet.LEFT_HIVE_AT
        shooterImpl.setTarget(AUTO_SHOOT_SPEED)
    }

    fun update (robotState: RobotState, input: GamepadState) {

        if (robotState.rightHive) {
            targetPose = poseSet.RIGHT_HIVE_AT.copy()
        }
        else {
            targetPose = poseSet.LEFT_HIVE_AT.copy()
        }

        justTransitioned = prevState != state
        prevState = state
        val horizontalDistance = distance(robotState, poseSet) * 0.0254
        val top = (-9.8) * (horizontalDistance * horizontalDistance)
        val bottom = (2 * Math.cos(ANGLE_OF_SHOT) * Math.cos(ANGLE_OF_SHOT) * (DISTANCE_TO_TOP - (horizontalDistance * tan(ANGLE_OF_SHOT))))
        val velMps = Math.sqrt(top / bottom)
        // 126.25m 474.5b
//        val velShooter = (126.25 * velMps) + 474.5
//        var velShooter = (92.23 * velMps) + 283.4 //11-21
        var velShooter = (91.23 * velMps) + 271.4
        //92.23
        if (abs(bottom) < 0.001){
            velShooter = 800.0
        }
        val diffOff20 = velShooter % 20

        if (diffOff20 <= 10){
            velShooter -= diffOff20
        }
        else {
            velShooter += (20 - diffOff20)
        }
        Log.i("distance", horizontalDistance.toString())
        Log.i("top", top.toString())
        Log.i("bottom", bottom.toString())
        Log.i("Target", velShooter.toString())

        val targetDeg = getTargetFromPinpoint(robotState.ppRobotPose)

        when (state) {
            State.FULL -> {
//                turretImpl.update(robotState)
                shooterImpl.setTarget(velShooter)
                turretImplPID.update(targetDeg)
                shooterImpl.tickShooter()

                if (input.start2) {
                    state = State.NO_TURRET
                }
            }

            State.NO_TURRET -> {
                if (justTransitioned) {
//                    turretImpl.setToZero()
                }
                shooterImpl.setTarget(velShooter)
                shooterImpl.tickShooter()
                turretImplPID.update(0.0)

                if (input.start2) {
                    state = State.NO_NOTHING
                }
            }
            State.NO_NOTHING -> {
                if (justTransitioned) {
//                    turretImpl.setToZero()
                }
                shooterImpl.setTarget(AUTO_SHOOT_SPEED)
                shooterImpl.tickShooter()
                turretImplPID.update(0.0)

                if (input.start2) {
                    state = State.TURRET_NO_SHOOTER
                }
            }
            State.AUTO -> {
                if (justTransitioned) {
//                    turretImpl.setToZero()
                }
//                turretImpl.update(robotState)
                shooterImpl.setTarget(constantVel)
                shooterImpl.tickShooter()
                turretImplPID.update(targetDeg)
//                if (robotState.ppX > 0) {
//                    turretImpl.setDegrees(15.0)
//                }
//                else {
//                    turretImpl.setDegrees(-15.0)
//                }
            }
            State.TURRET_NO_SHOOTER -> {
                if (justTransitioned){

                }
//                turretImpl.update(robotState)
                shooterImpl.setTarget(AUTO_SHOOT_SPEED)
                shooterImpl.tickShooter()
                turretImplPID.update(targetDeg)


                if (input.start2) {
                    state = State.FULL
                }
            }
        }
    }

    private fun toDeg(radians: Double): Double {
        return 180 * radians / PI
    }

    private fun getTargetFromPinpoint(currentPose: RobotPose): Double {
        val x = targetPose.x - (currentPose.x)
        val y = targetPose.y - (currentPose.y)
        val robotAngle = toDeg(currentPose.a)
        val goalAngle = toDeg(atan2(y, x))

        var targetAngle = (180 + goalAngle - robotAngle)

        while (targetAngle >= 180.0) targetAngle -= 360.0
        while (targetAngle < -180.0) targetAngle += 360.0

        return(targetAngle)
    }
}
