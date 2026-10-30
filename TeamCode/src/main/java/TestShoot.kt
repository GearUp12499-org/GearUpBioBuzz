package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.hardware.HardwareMapper

@TeleOp
class TestShoot: LinearOpMode() {
    lateinit var hardware: Hardware
    override fun runOpMode() {
        hardware = Hardware(hardwareMap)

        waitForStart()

        var vel = 1000.0

        while (opModeIsActive()){
//            hardware.setShooterVel(vel)
            hardware.shooter.setPower(0.3)

            if (gamepad1.x) {
                vel -= 20
            }

            if (gamepad1.b) {
                vel += 20
            }

            telemetry.addData("setVel", vel)
            telemetry.addData("actualVel", hardware.shooter.getVelocity())
            telemetry.update()
        }
    }
}