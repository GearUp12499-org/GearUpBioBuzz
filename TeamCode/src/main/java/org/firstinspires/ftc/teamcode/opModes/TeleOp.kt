package org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode.opModes

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.GerthaHardwareMap
import org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode.subsystems.DriveMachine
import org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode.subsystems.GamepadState
import org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode.subsystems.RobotState

@TeleOp
class TeleOp: LinearOpMode() {
    lateinit var hw: GerthaHardwareMap

    override fun runOpMode() {
        hw = GerthaHardwareMap(hardwareMap)
        val driveMachine = DriveMachine(hw)
        val robotState = RobotState(hw)
        val gamepadState = GamepadState(gamepad1, gamepad2)
        waitForStart()

        driveMachine.init()
        robotState.init()

        while (opModeIsActive()) {
            robotState.update()
            gamepadState.update()
            driveMachine.update(robotState, gamepadState)
            telemetry.addData("ppAngle", robotState.ppThetaDeg)
            telemetry.update()
        }
    }
}