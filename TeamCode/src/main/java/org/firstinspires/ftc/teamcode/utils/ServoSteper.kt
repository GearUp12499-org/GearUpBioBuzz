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

        var pos1 = 0.0
        var pos2 = 1.0
        while (opModeIsActive()) {
            if (gamepad1.a && pos1 < 1) {
                pos1 += 0.01
            }
            if (gamepad1.b && pos1 > 0) {
                pos1 -= 0.01
            }

            if (gamepad1.x && pos2 < 1) {
                pos2 += 0.01
            }
            if (gamepad1.y && pos2 > 0) {
                pos2 -= 0.01
            }
            hw.rightRamp.position = pos1
            hw.leftRamp.position = 1.03 - pos1

            telemetry.addData("position1", pos1)
            telemetry.addData("position2", pos2)

            telemetry.update()

            sleep(10)
        }
    }
}