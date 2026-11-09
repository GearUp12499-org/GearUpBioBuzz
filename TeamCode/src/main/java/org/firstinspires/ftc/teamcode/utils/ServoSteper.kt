package org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode.utils

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.GerthaHardwareMap

@TeleOp
class ServoSteper: LinearOpMode() {
    lateinit var hw: GerthaHardwareMap
    override fun runOpMode() {
        hw = GerthaHardwareMap(hardwareMap)
        waitForStart()

        var pos = 0.0
        while (opModeIsActive()) {
            if (gamepad1.a && pos < 1) {
                pos += 0.01
            }
            if (gamepad1.b && pos > 0) {
                pos -= 0.01
            }
            hw.pFlip.position = pos

            telemetry.addData("position", pos)
            telemetry.update()

            sleep(10)
        }
    }
}