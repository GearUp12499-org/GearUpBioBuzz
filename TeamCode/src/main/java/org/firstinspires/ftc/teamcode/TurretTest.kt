package org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.robot.Robot
import org.firstinspires.ftc.teamcode.GerthaHardwareMap
import org.firstinspires.ftc.teamcode.hardware.RobotPose
import org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode.subsystems.GamepadState
import org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode.subsystems.RobotState
import org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode.subsystems.TurretImplPID

@TeleOp
class TurretTest: LinearOpMode() {

    lateinit var hw: GerthaHardwareMap

    override fun runOpMode() {

        hw = GerthaHardwareMap(hardwareMap)

        val robotState = RobotState(hw)
        val turretImplPID = TurretImplPID(hw)
        val gamepadState = GamepadState(gamepad1, gamepad2)

        var target = 0.0

        robotState.init(RobotPose(0.0, 0.0, 0.0))

        waitForStart()

        while (opModeIsActive()){

            if (gamepadState.rt1) {
                target += 10.0
            }
            else if (gamepadState.lt1) {
                target -= 10.0
            }

            if (gamepadState.rb1){
                TurretImplPID.kp += 0.001
            }
            else if (gamepadState.lb1) {
                TurretImplPID.kp -= 0.001
            }

            if (gamepadState.a1) {
                TurretImplPID.ki += 0.0001
            }
            else if (gamepadState.b1) {
                TurretImplPID.ki -= 0.0001
            }

            if (gamepadState.start1) {
                TurretImplPID.kd += 0.001
            }
            else if (gamepadState.jx1 > 0.5) {
                TurretImplPID.kd -= 0.001
            }

            val currentTicks = hw.pollenTurretEncoder.getCurrentPosition()
            telemetry.addData("kp", TurretImplPID.kp)
            telemetry.addData("ki", TurretImplPID.ki)
            telemetry.addData("kd", TurretImplPID.kd)
            telemetry.addData("targetDegrees", target)
//            telemetry.addData("ticks", currentTicks)
            telemetry.addData("degrees", currentTicks/ TurretImplPID.TICKS_PER_DEGREE)
            telemetry.update()

//            turretImplPID.targetDeg = target

            robotState.update()
            turretImplPID.update(target)
            gamepadState.update()
        }
    }
}