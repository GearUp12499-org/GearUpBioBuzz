package org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode.utils

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.GerthaHardwareMap

@TeleOp
class ShooterSteper : LinearOpMode() {

    lateinit var hw: GerthaHardwareMap

    override fun runOpMode() {

        hw = GerthaHardwareMap(hardwareMap)

        waitForStart()

        while(opModeIsActive()){
            
        }
    }

}