package org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.GerthaHardwareMap

@TeleOp
class TurretTest: LinearOpMode() {

    lateinit var hw: GerthaHardwareMap

    override fun runOpMode() {

        hw = GerthaHardwareMap(hardwareMap)

        waitForStart()

        while (opModeIsActive()){

            val currentTicks = hw.pollenTurretEncoder.getCurrentPosition()
            telemetry.addData("ticks", currentTicks)
            telemetry.update()
        }
    }
}